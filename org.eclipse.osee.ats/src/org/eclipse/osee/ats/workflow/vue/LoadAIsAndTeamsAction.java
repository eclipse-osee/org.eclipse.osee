/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.workflow.vue;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact.TeamDefinitionOptions;
import org.eclipse.osee.ats.config.AtsConfig;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.workflow.vue.DiagramNode.PageType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.osgi.framework.Bundle;

/**
 * Retrieve "AIs and Teams.vue" extension points and configure ATS for Actionable Items and Teams from within.
 */
public class LoadAIsAndTeamsAction extends Action {
   private Map<String, ActionableItemArtifact> idToActionItem = new HashMap<String, ActionableItemArtifact>();
   private final boolean prompt;
   private static String FULL_NAME = "Full Name:";
   private static String DESCRIPTION = "Description:";
   private static String WORKFLOW_ID = "WorkflowId:";
   private static String STATIC_ID = "StaticId:";
   private static String GET_OR_CREATE = "GetOrCreate";
   private static String NOT_ACTIONABLE = "NotActionable";
   private static String LEAD = "Lead:";
   private static String MEMBER = "Member:";
   private final String bundleId;

   /**
    * The constructor.
    */
   public LoadAIsAndTeamsAction(boolean prompt, String bundleId) {
      super("Load ATS Config");
      this.prompt = prompt;
      this.bundleId = bundleId;
   }

   /**
    * The action has been activated. The argument of the method represents the 'real' action sitting in the workbench
    * UI.
    */
   public void run() {
      if (prompt && !MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Import ATS Config?",
            "Importing ATS Config from ActionableItems.vue.\n\nAre you sure?")) return;

      for (Entry<String, String> entry : loadResources().entrySet()) {
         Diagram workFlow = DiagramFactory.getInstance().getWorkFlowFromFileContents(entry.getKey(), entry.getValue());
         processWorkflow(workFlow);
      }
   }

   @SuppressWarnings("deprecation")
   private Map<String, String> loadResources() {
      Map<String, String> resources = new HashMap<String, String>();
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsAIandTeamConfig");
      if (point == null) {
         OSEELog.logSevere(AtsPlugin.class, "Can't access AtsAIandTeamConfig extension point", true);
         return resources;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String vueFilename = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsAIandTeamConfig")) {
               vueFilename = el.getAttribute("vueFilename");
               bundleName = el.getContributor().getName();
               if (bundleId == null || bundleId.equals(bundleName)) {
                  if (vueFilename != null && bundleName != null) {
                     Bundle bundle = Platform.getBundle(bundleName);
                     try {
                        URL url = bundle.getEntry(vueFilename);
                        resources.put(bundleName + "/" + vueFilename, Lib.inputStreamToString(url.openStream()));
                     } catch (Exception ex) {
                        OSEELog.logException(AtsPlugin.class, "Error loading AtsStateItem extension", ex, true);
                     }
                  }
               }
            }
         }
      }
      return resources;
   }

   private void processWorkflow(final Diagram workFlow) {
      if (workFlow == null) throw new IllegalArgumentException("ATS config items can't be loaded.");

      try {
         AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {

            @Override
            protected void handleTxWork()throws OseeCoreException, SQLException{
               // Get or create ATS root artifact
               Artifact atsHeading = AtsConfig.getInstance().getOrCreateAtsHeadingArtifact();

               // Create Actionable Items
               DiagramNode workPage = workFlow.getPage("Actionable Items");
               addActionableItem(atsHeading, workPage);
               atsHeading.persistAttributesAndRelations();

               // Create Teams
               workPage = workFlow.getPage("Teams");
               addTeam(atsHeading, workPage);
               atsHeading.persistAttributesAndRelations();

            }
         };
         txWrapper.execute();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }

   public TeamDefinitionArtifact addTeam(Artifact parent, DiagramNode page)throws OseeCoreException, SQLException{
      // System.out.println("Adding Team " + page.getName());
      TeamDefinitionArtifact teamDefArt = null;
      if (page.getName().equals(AtsConfig.TEAMS_HEADING)) {
         teamDefArt = AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact();
      } else {

         ArrayList<User> leads = new ArrayList<User>();
         ArrayList<User> members = new ArrayList<User>();
         java.util.Set<String> staticIds = new HashSet<String>();
         String desc = "";
         boolean getOrCreate = false;
         boolean actionable = true;
         String fullName = "";
         String workflowId = "";
         List<TeamDefinitionOptions> teamDefinitionOptions = new ArrayList<TeamDefinitionOptions>();
         for (String line : page.getInstructionStr().replaceAll("\r", "\n").split("\n")) {
            if (!line.equals("")) {
               if (line.startsWith(DESCRIPTION))
                  desc = line.replaceFirst(DESCRIPTION, "");
               else if (line.startsWith(WORKFLOW_ID)) {
                  workflowId = line.replaceFirst(WORKFLOW_ID, "");
               } else if (line.startsWith(STATIC_ID)) {
                  staticIds.add(line.replaceFirst(STATIC_ID, ""));
               } else if (line.startsWith(GET_OR_CREATE))
                  getOrCreate = true;
               else if (line.startsWith(NOT_ACTIONABLE))
                  actionable = false;
               else if (line.startsWith(FULL_NAME))
                  fullName = line.replaceFirst(FULL_NAME, "");
               else if (line.contains(TeamDefinitionOptions.TeamUsesVersions.name()))
                  teamDefinitionOptions.add(TeamDefinitionOptions.TeamUsesVersions);
               else if (line.contains(TeamDefinitionOptions.RequireTargetedVersion.name()))
                  teamDefinitionOptions.add(TeamDefinitionOptions.RequireTargetedVersion);
               else if (line.startsWith(LEAD)) {
                  String name = line.replaceFirst(LEAD, "");
                  // Get user or create temp user if not on production DB
                  User u = SkynetAuthentication.getUserByName(name, !AtsPlugin.isProductionDb());
                  leads.add(u);
               } else if (line.startsWith(MEMBER)) {
                  String name = line.replaceFirst(MEMBER, "");
                  // Get user or create temp user if not on production DB
                  User u = SkynetAuthentication.getUserByName(name, !AtsPlugin.isProductionDb());
                  members.add(u);
               } else
                  throw new IllegalArgumentException(
                        "Unhandled AtsConfig Line\"" + line + "\" in diagram page \"" + page.getName() + "\"");
            }
         }

         ArrayList<ActionableItemArtifact> actionableItems = new ArrayList<ActionableItemArtifact>();
         for (DiagramNode childPage : page.getToPages()) {
            if (childPage.getPageType() == PageType.ActionableItem) {
               // Relate this Team Definition to the Actionable Item
               ActionableItemArtifact actItem = idToActionItem.get(childPage.getId());
               if (actItem != null) {
                  actionableItems.add(actItem);
               } else
                  throw new IllegalArgumentException(
                        "Can't retrieve Actionable Item \"" + childPage.getName() + "\" with id " + childPage.getId());
            }
         }
         // If getOrCreate has been specified, search to see if team already exists to reuse
         if (getOrCreate) {
            try {
               teamDefArt =
                     (TeamDefinitionArtifact) ArtifactQuery.getArtifactFromTypeAndName(
                           TeamDefinitionArtifact.ARTIFACT_NAME, page.getName(), AtsPlugin.getAtsBranch());
            } catch (ArtifactDoesNotExist ex) {
               // do nothing; artifact created below
            }

            if (teamDefArt != null) {
               for (Artifact actionableItem : actionableItems) {
                  teamDefArt.addRelation(AtsRelation.TeamActionableItem_ActionableItem, actionableItem);
               }
            }
         }
         if (teamDefArt == null) {
            teamDefArt =
                  TeamDefinitionArtifact.createNewTeamDefinition(page.getName(), fullName, desc, leads, members,
                        actionableItems, parent,
                        teamDefinitionOptions.toArray(new TeamDefinitionOptions[teamDefinitionOptions.size()]));
         }
         for (String staticId : staticIds) {
            teamDefArt.addAttribute(StaticIdQuery.STATIC_ID_ATTRIBUTE, staticId);
         }

         teamDefArt.setSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), actionable);

         if (!workflowId.equals("")) {
            try {
               Artifact workflowArt =
                     ArtifactQuery.getArtifactFromTypeAndName(WorkFlowDefinition.ARTIFACT_NAME, workflowId,
                           AtsPlugin.getAtsBranch());
               if (workflowArt != null)
                  teamDefArt.addRelation(AtsRelation.WorkItem__Child, workflowArt);
               else
                  System.err.println("Can't find workflow with id \"" + workflowId + "\"");
            } catch (Exception ex) {
               System.err.println(ex.getLocalizedMessage());
            }
         }
         teamDefArt.persistAttributesAndRelations();
      }

      // Handle all team children
      for (DiagramNode childPage : page.getToPages())
         if (childPage.getPageType() == PageType.Team) addTeam(teamDefArt, (DiagramNode) childPage);

      return teamDefArt;
   }

   public ActionableItemArtifact addActionableItem(Artifact parent, DiagramNode page)throws OseeCoreException, SQLException{
      // System.out.println("Processing page " + page.getName());
      ActionableItemArtifact aia = null;
      boolean getOrCreate = false;
      boolean actionable = true;
      Set<String> staticIds = new HashSet<String>();
      Set<User> leads = new HashSet<User>();
      for (String line : page.getInstructionStr().replaceAll("\r", "\n").split("\n")) {
         if (!line.equals("")) {
            if (line.startsWith(GET_OR_CREATE))
               getOrCreate = true;
            else if (line.startsWith(NOT_ACTIONABLE))
               actionable = false;
            else if (line.startsWith(LEAD)) {
               String name = line.replaceFirst(LEAD, "");
               // Get user or create temp user if not on production DB
               User u = SkynetAuthentication.getUserByName(name, !AtsPlugin.isProductionDb());
               leads.add(u);
            } else if (line.startsWith(STATIC_ID)) staticIds.add(line.replaceFirst(STATIC_ID, ""));
         }
      }
      if (page.getName().equals(AtsConfig.ACTIONABLE_ITEMS_HEADING)) {
         aia = AtsConfig.getInstance().getOrCreateActionableItemsHeadingArtifact();
      } else {
         if (getOrCreate) {
            try {
               aia =
                     (ActionableItemArtifact) ArtifactQuery.getArtifactFromTypeAndName(
                           ActionableItemArtifact.ARTIFACT_NAME, page.getName(), AtsPlugin.getAtsBranch());
            } catch (ArtifactDoesNotExist ex) {
               // do nothing; will be created below
            }
         }
         if (aia == null) {
            aia =
                  (ActionableItemArtifact) ArtifactTypeManager.addArtifact(ActionableItemArtifact.ARTIFACT_NAME,
                        BranchPersistenceManager.getAtsBranch());
            aia.setDescriptiveName(page.getName());
            for (String staticId : staticIds) {
               aia.addAttribute(StaticIdQuery.STATIC_ID_ATTRIBUTE, staticId);
            }
            for (User user : leads) {
               aia.addRelation(AtsRelation.TeamLead_Lead, user);
            }

            aia.persistAttributes();
            idToActionItem.put(page.getId(), aia);
            parent.addChild(aia);
            parent.persistAttributesAndRelations();
         }
      }
      for (DiagramNode childPage : page.getToPages()) {
         addActionableItem(aia, (DiagramNode) childPage);
      }
      aia.setSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), actionable);

      aia.persistAttributes();
      return aia;
   }

   /**
    * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but
    * this can only happen after the delegate has been created.
    */
   public void selectionChanged(IAction action, ISelection selection) {
   }

   /**
    * We can use this method to dispose of any system resources we previously allocated.
    * 
    * @see IWorkbenchWindowActionDelegate#dispose
    */
   public void dispose() {
   }

   /**
    * We will cache window object in order to be able to provide parent shell for the message dialog.
    * 
    * @see IWorkbenchWindowActionDelegate#init
    */
   public void init(IWorkbenchWindow window) {
   }
}