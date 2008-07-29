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

package org.eclipse.osee.ats.util;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowLabelProvider;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.config.BulkLoadAtsCache;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.ats.IAtsLib;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class AtsLib implements IAtsLib {

   public AtsLib() {
      super();
   }

   /**
    * Return set of Artifacts of type clazz that match the given active state of the "Active" attribute value. If no
    * attribute exists, Active == true; If does exist then attribute value "yes" == true, "no" == false.
    * 
    * @param <A>
    * @param artifacts to iterate through
    * @param active state to validate against; Both will return all artifacts matching type
    * @param clazz type of artifacts to consider
    * @return
    * @throws SQLException
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> Set<A> getActiveSet(Collection<A> artifacts, Active active, Class<? extends Artifact> clazz) throws SQLException, MultipleAttributesExist {
      Set<A> results = new HashSet<A>();
      for (Artifact art : artifacts) {
         if ((art.getClass().equals(clazz)) && art.isAttributeTypeValid(ATSAttributes.ACTIVE_ATTRIBUTE.getStoreName())) {
            if (active == Active.Both)
               results.add((A) art);
            else {
               // Ats config Artifact is Active unless otherwise specified
               boolean attributeActive =
                     ((A) art).getSoleAttributeValue(ATSAttributes.ACTIVE_ATTRIBUTE.getStoreName(), false);
               if (active == Active.Active && attributeActive) {
                  results.add((A) art);
               } else if (active == Active.InActive && !attributeActive) {
                  results.add((A) art);
               }
            }
         }
      }
      return results;
   }

   public boolean isAtsAdmin() {
      return AtsAdmin.isAtsAdmin();
   }

   public static String doubleToStrString(double d) {
      return doubleToStrString(d, false);
   }

   public static String doubleToStrString(double d, boolean blankIfZero) {
      if (blankIfZero && d == 0)
         return "";
      else
         return String.format("%5.2f", d);
   }

   public static void editActionActionableItems(ActionArtifact actionArt) throws OseeCoreException, SQLException {
      Result result = actionArt.addActionableItems();
      if (result.isFalse() && result.getText().equals("")) return;
      if (result.isFalse()) result.popup(result.isTrue());
   }

   public static void editTeamActionableItems(TeamWorkFlowArtifact teamArt) throws OseeCoreException, SQLException {
      Result result = teamArt.addActionableItems();
      if (result.isFalse() && result.getText().equals("")) return;
      if (result.isFalse() && !result.getText().equals("")) result.popup(result.isTrue());
   }

   public static void open(String guid, OseeAts.OpenView view) {
      (new AtsLib()).openArtifact(guid, view);
   }

   public void openArtifact(String guidOrHrid, Integer branchId, OseeAts.OpenView view) {
      try {
         Branch branch = BranchPersistenceManager.getBranch(branchId);
         Artifact artifact = ArtifactQuery.getArtifactFromId(guidOrHrid, branch);
         openAtsAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /**
    * Only to be used by browser. Use open (artifact) instead.
    * 
    * @param guid
    */
   public void openArtifact(String guid, OseeAts.OpenView view) {
      BulkLoadAtsCache.run(false);
      Artifact artifact = null;
      try {
         artifact = ArtifactQuery.getArtifactFromId(guid, BranchPersistenceManager.getAtsBranch());
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return;
      }

      if (view == OseeAts.OpenView.ActionEditor) {
         if ((artifact instanceof StateMachineArtifact) || (artifact instanceof ActionArtifact))
            openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
         else
            ArtifactEditor.editArtifact(artifact);
      } else if (view == OseeAts.OpenView.ArtifactEditor) {
         ArtifactEditor.editArtifact(artifact);
      } else if (view == OseeAts.OpenView.ArtifactHyperViewer) {
         AWorkbench.popup("ERROR", "Unimplemented");
      }
   }

   public static void createAtsAction(String initialDescription, String actionableItem) {
      (new AtsLib()).createATSAction(initialDescription, actionableItem);
   }

   public void createATSAction(String initialDescription, String actionableItemName) {
      // Ensure actionable item is configured for ATS before continuing
      try {
         AtsCache.getSoleArtifactByName(actionableItemName, VersionArtifact.class);
      } catch (ArtifactDoesNotExist ex) {
         AWorkbench.popup(
               "Configuration Error",
               "Actionable Item \"" + actionableItemName + "\" is not configured for ATS tracking.\n\nAction can not be created.");
         return;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return;
      }

      NewAction newAction = new NewAction(actionableItemName);
      newAction.setInitialDescription(initialDescription);
      newAction.run();

   }

   public static void openAtsAction(final Artifact art, final AtsOpenOption option) {
      (new AtsLib()).openATSAction(art, option);
   }

   public void openATSAction(final Artifact art, final AtsOpenOption option) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               if (art instanceof ActionArtifact) {
                  ActionArtifact actionArt = (ActionArtifact) art;
                  Collection<TeamWorkFlowArtifact> teams = actionArt.getTeamWorkFlowArtifacts();
                  if (option == AtsOpenOption.OpenAll)
                     for (TeamWorkFlowArtifact team : teams)
                        SMAEditor.editArtifact(team);
                  else if (option == AtsOpenOption.AtsWorld)
                     WorldView.loadIt("Action " + actionArt.getHumanReadableId(), Arrays.asList(actionArt));
                  else if (option == AtsOpenOption.OpenOneOrPopupSelect) {
                     if (teams.size() == 1)
                        SMAEditor.editArtifact(teams.iterator().next());
                     else {
                        TeamWorkFlowArtifact art = promptSelectTeamWorkflow(actionArt);
                        if (art != null)
                           SMAEditor.editArtifact((Artifact) art);
                        else
                           return;
                     }
                  }
               } else
                  SMAEditor.editArtifact(art);
            } catch (SQLException ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });
   }

   public static TeamWorkFlowArtifact promptSelectTeamWorkflow(ActionArtifact actArt) throws SQLException {
      ListDialog ld = new ListDialog(Display.getCurrent().getActiveShell());
      ld.setContentProvider(new ArrayContentProvider());
      ld.setLabelProvider(new TeamWorkflowLabelProvider());
      ld.setTitle("Select Team Workflow");
      ld.setMessage("Select Team Workflow");
      ld.setInput(actArt.getTeamWorkFlowArtifacts());
      if (ld.open() == 0) {
         if (ld.getResult().length == 0)
            AWorkbench.popup("Error", "No Workflow Selected");
         else
            return (TeamWorkFlowArtifact) ld.getResult()[0];
      }
      return null;
   }

   public void openInAtsWorld(String name, Collection<Artifact> artifacts) {
      WorldView.loadIt(name, artifacts);
   }
}
