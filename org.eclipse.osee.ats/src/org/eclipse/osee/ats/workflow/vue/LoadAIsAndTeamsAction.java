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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact.TeamDefinitionOptions;
import org.eclipse.osee.ats.config.AtsConfig;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.workflow.vue.DiagramNode.PageType;
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

/**
 * Retrieve "AIs and Teams.vue" extension points and configure ATS for Actionable Items and Teams from within.
 */
public class LoadAIsAndTeamsAction {

   private static final String FULL_NAME = "Full Name:";
   private static final String DESCRIPTION = "Description:";
   private static final String WORKFLOW_ID = "WorkflowId:";
   private static final String STATIC_ID = "StaticId:";
   private static final String GET_OR_CREATE = "GetOrCreate";
   private static final String NOT_ACTIONABLE = "NotActionable";
   private static final String LEAD = "Lead:";
   private static final String MEMBER = "Member:";

   private final Map<String, ActionableItemArtifact> idToActionItem;
   private final String bundleId;
   private final boolean prompt;
   private final boolean allowUserCreation;

   private LoadAIsAndTeamsAction(boolean prompt, String bundleId, boolean allowUserCreation) {
      this.idToActionItem = new HashMap<String, ActionableItemArtifact>();
      this.prompt = prompt;
      this.bundleId = bundleId;
      this.allowUserCreation = allowUserCreation;
   }

   /**
    * This method is package private to prevent others from using it - only AtsDbConfig children are allowed access;
    * 
    * @param bundleId
    */
   static void executeForDbConfig(String bundleId) {
      new LoadAIsAndTeamsAction(false, bundleId, true).run();
   }

   public static void executeForAtsRuntimeConfig(boolean prompt, String bundleId) throws OseeCoreException {
      new LoadAIsAndTeamsAction(false, bundleId, !AtsPlugin.isProductionDb()).run();
   }

   private void run() {
      if (prompt && !MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Import ATS Config?",
            "Importing ATS Config from ActionableItems.vue.\n\nAre you sure?")) return;

      for (Entry<String, String> entry : loadResources().entrySet()) {
         Diagram workFlow = DiagramFactory.getInstance().getWorkFlowFromFileContents(entry.getKey(), entry.getValue());
         processWorkflow(workFlow);
      }
   }

   private Map<String, String> loadResources() {
      Map<String, String> resources = new HashMap<String, String>();
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsAIandTeamConfig");
      if (point == null) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't access AtsAIandTeamConfig extension point");
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
                        OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Error loading AtsStateItem extension", ex);
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
         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         // Get or create ATS root artifact
         Artifact atsHeading = AtsConfig.getInstance().getOrCreateAtsHeadingArtifact(transaction);

         // Create Actionable Items
         DiagramNode workPage = workFlow.getPage("Actionable Items");
         addActionableItem(atsHeading, workPage, transaction);

         // Create Teams
         workPage = workFlow.getPage("Teams");
         addTeam(atsHeading, workPage, transaction);

         atsHeading.persistAttributesAndRelations(transaction);
         transaction.execute();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private User getUserByName(String name, boolean create, SkynetTransaction transaction) throws OseeCoreException {
      try {
         return UserManager.getUserByName(name);
      } catch (UserNotInDatabase ex) {
         if (create) {
            return UserManager.createUser(new OseeUser(name, name, "", true), transaction);
         } else {
            throw ex;
         }
      }
   }

   private TeamDefinitionArtifact addTeam(Artifact parent, DiagramNode page, SkynetTransaction transaction) throws OseeCoreException {
      // System.out.println("Adding Team " + page.getName());
      TeamDefinitionArtifact teamDefArt = null;
      if (page.getName().equals(AtsConfig.TEAMS_HEADING)) {
         teamDefArt = AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact(transaction);
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
                  User u = getUserByName(name, allowUserCreation, transaction);
                  leads.add(u);
               } else if (line.startsWith(MEMBER)) {
                  String name = line.replaceFirst(MEMBER, "");
                  User u = getUserByName(name, allowUserCreation, transaction);
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

         if (getOrCreate) {
            teamDefArt =
                  (TeamDefinitionArtifact) Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(),
                        TeamDefinitionArtifact.ARTIFACT_NAME, page.getName());
         } else {
            teamDefArt =
                  (TeamDefinitionArtifact) ArtifactTypeManager.addArtifact(TeamDefinitionArtifact.ARTIFACT_NAME,
                        AtsPlugin.getAtsBranch(), page.getName());
         }
         if (!teamDefArt.isInDb()) {
            teamDefArt.initialize(fullName, desc, leads, members, actionableItems,
                  teamDefinitionOptions.toArray(new TeamDefinitionOptions[teamDefinitionOptions.size()]));
            if (parent == null) {
               // Relate to team heading
               parent = AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact(transaction);
            }
            parent.addChild(teamDefArt);

            for (Artifact actionableItem : actionableItems) {
               teamDefArt.addRelation(AtsRelation.TeamActionableItem_ActionableItem, actionableItem);
            }
            for (String staticId : staticIds) {
               StaticIdManager.setSingletonAttributeValue(teamDefArt, staticId);
            }
            teamDefArt.setSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), actionable);
         }
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
         teamDefArt.persistAttributesAndRelations(transaction);
      }

      // Handle all team children
      for (DiagramNode childPage : page.getToPages()) {
         if (childPage.getPageType() == PageType.Team) {
            addTeam(teamDefArt, childPage, transaction);
         }
      }
      return teamDefArt;
   }

   private ActionableItemArtifact addActionableItem(Artifact parent, DiagramNode page, SkynetTransaction transaction) throws OseeCoreException {
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
               User u = getUserByName(name, allowUserCreation, transaction);
               leads.add(u);
            } else if (line.startsWith(STATIC_ID)) staticIds.add(line.replaceFirst(STATIC_ID, ""));
         }
      }
      if (page.getName().equals(AtsConfig.ACTIONABLE_ITEMS_HEADING)) {
         aia = AtsConfig.getInstance().getOrCreateActionableItemsHeadingArtifact(transaction);
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
                        AtsPlugin.getAtsBranch());
            aia.setDescriptiveName(page.getName());
            for (String staticId : staticIds) {
               StaticIdManager.setSingletonAttributeValue(aia, staticId);
            }
            for (User user : leads) {
               aia.addRelation(AtsRelation.TeamLead_Lead, user);
            }

            aia.persistAttributes(transaction);
            idToActionItem.put(page.getId(), aia);
            parent.addChild(aia);
            parent.persistAttributesAndRelations(transaction);
         }
      }
      for (DiagramNode childPage : page.getToPages()) {
         addActionableItem(aia, childPage, transaction);
      }
      aia.setSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), actionable);

      aia.persistAttributes(transaction);
      return aia;
   }

   //   /**
   //    * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but
   //    * this can only happen after the delegate has been created.
   //    */
   //   public void selectionChanged(IAction action, ISelection selection) {
   //   }
   //
   //   /**
   //    * We can use this method to dispose of any system resources we previously allocated.
   //    * 
   //    * @see IWorkbenchWindowActionDelegate#dispose
   //    */
   //   public void dispose() {
   //   }
   //
   //   /**
   //    * We will cache window object in order to be able to provide parent shell for the message dialog.
   //    * 
   //    * @see IWorkbenchWindowActionDelegate#init
   //    */
   //   public void init(IWorkbenchWindow window) {
   //   }
}