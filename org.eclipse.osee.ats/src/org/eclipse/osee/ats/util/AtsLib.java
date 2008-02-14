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
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
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

   private static ArtifactPersistenceManager apm = ArtifactPersistenceManager.getInstance();

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
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> Set<A> getActiveSet(Collection<A> artifacts, Active active, Class<? extends Artifact> clazz) {
      Set<A> results = new HashSet<A>();
      for (Artifact art : artifacts) {
         if ((art.getClass().equals(clazz)) && art.isAttributeTypeValid(ATSAttributes.ACTIVE_ATTRIBUTE.getStoreName())) {
            if (active == Active.Both)
               results.add((A) art);
            else {
               // Ats config Artifact is Active unless otherwise specified
               String activeStr = art.getSoleAttributeValue(ATSAttributes.ACTIVE_ATTRIBUTE.getStoreName());
               if (active == Active.Active && (activeStr.equals("") || activeStr.equals("yes")))
                  results.add((A) art);
               else if (active == Active.InActive && activeStr.equals("no")) results.add((A) art);
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

   public static void editActionActionableItems(ActionArtifact actionArt) throws Exception {
      Result result = actionArt.addActionableItems();
      if (result.isFalse() && result.getText().equals("")) return;
      if (result.isFalse()) result.popup(result.isTrue());
   }

   public static void editTeamActionableItems(TeamWorkFlowArtifact teamArt) throws Exception {
      Result result = teamArt.addActionableItems();
      if (result.isFalse() && result.getText().equals("")) return;
      if (result.isFalse() && !result.getText().equals("")) result.popup(result.isTrue());
   }

   public static void open(String guid, OseeAts.OpenView view) {
      (new AtsLib()).openArtifact(guid, view);
   }

   public void openArtifact(String guidOrHrid, Integer branchId, OseeAts.OpenView view) {
      try {
         Branch branch = BranchPersistenceManager.getInstance().getBranch(branchId);
         Artifact art = null;
         if (guidOrHrid.length() == 5) {
            Collection<Artifact> arts =
                  ArtifactPersistenceManager.getInstance().getArtifactsFromHrid(guidOrHrid, branch);
            if (arts.size() > 0) art = arts.iterator().next();
         } else {
            art = ArtifactPersistenceManager.getInstance().getArtifact(guidOrHrid, branch);
         }
         if (art != null) openAtsAction(art, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /**
    * Only to be used by browser. Use open (artifact) instead.
    * 
    * @param guid
    */
   public void openArtifact(String guid, OseeAts.OpenView view) {
      Artifact art = null;
      try {
         if (guid.length() == 5) {
            Collection<Artifact> arts =
                  apm.getArtifactsFromHrid(guid, BranchPersistenceManager.getInstance().getAtsBranch());
            if (arts.size() > 1) {
               OSEELog.logSevere(AtsPlugin.class, "Action in DB more than once " + guid, true);
            } else if (arts.size() == 1) art = arts.iterator().next();
         } else
            art = apm.getArtifact(guid, BranchPersistenceManager.getInstance().getAtsBranch());
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      if (art == null) {
         AWorkbench.popup("ERROR", "Action not in DB");
         return;
      }
      if (view == OseeAts.OpenView.ActionEditor) {
         if ((art instanceof StateMachineArtifact) || (art instanceof ActionArtifact))
            openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
         else
            ArtifactEditor.editArtifact(art);
      } else if (view == OseeAts.OpenView.ArtifactEditor) {
         ArtifactEditor.editArtifact(art);
      } else if (view == OseeAts.OpenView.ArtifactHyperViewer) {
         AWorkbench.popup("ERROR", "Unimplemented");
         // try {
         // ArtifactHyperView.openArtifact(art);
         // }
         // catch (PartInitException ex) {
         // OSEELog.logException(AtsPlugin.class, ex, true);
         // }
      }
   }

   public static void createAtsAction(String initialDescription, String actionableItem) {
      (new AtsLib()).createATSAction(initialDescription, actionableItem);
   }

   public void createATSAction(String initialDescription, String actionableItem) {

      NewAction newAction = new NewAction(actionableItem);
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
                     WorldView.loadIt("Action " + actionArt.getHumanReadableId(),
                           Arrays.asList(new Artifact[] {actionArt}));
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
