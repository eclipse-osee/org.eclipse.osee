/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditor;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkflowLabelProvider;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.ats.ide.world.WorldEditorUISearchItemProvider;
import org.eclipse.osee.ats.ide.world.search.GroupWorldSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.FrameworkArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public final class AtsEditors {

   private AtsEditors() {
      super();
   }

   /**
    * Only to be used by browser. Use open (artifact) instead.
    */
   public static void openArtifactById(ArtifactId artifactId, OseeCmEditor editor) {
      try {
         Artifact artifact = ArtifactQuery.getArtifactFromId(artifactId, AtsApiService.get().getAtsBranch());
         openArtifact(artifact, editor);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static void openArtifact(Artifact artifact, OseeCmEditor editor) {
      try {
         if (editor == OseeCmEditor.CmPcrEditor) {
            if (artifact instanceof AbstractWorkflowArtifact || artifact.isOfType(AtsArtifactTypes.Action)) {
               openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
            } else {
               RendererManager.open(artifact, PresentationType.GENERALIZED_EDIT);
            }
         } else if (editor == OseeCmEditor.ArtifactEditor) {
            RendererManager.open(artifact, PresentationType.GENERALIZED_EDIT);
         } else if (editor == OseeCmEditor.ArtifactHyperViewer) {
            AWorkbench.popup("ERROR", "Unimplemented");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static void openATSArtifact(ArtifactToken artifact) {
      if (artifact.isOfType(AtsArtifactTypes.AtsArtifact)) {
         try {
            openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            AWorkbench.popup("ERROR", ex.getLocalizedMessage());
         }
      } else {
         AWorkbench.popup("ERROR", String.format("Trying to open %s with SMAEditor", artifact.toStringWithId()));
      }
   }

   public static void openATSAction(final ArtifactToken art, final AtsOpenOption atsOpenOption) {
      try {
         Artifact artifact = ArtifactQuery.getArtifactFromId(art, AtsApiService.get().getAtsBranch());
         if (artifact.isOfType(AtsArtifactTypes.Action)) {
            IAtsAction action = (IAtsAction) artifact;
            Collection<IAtsTeamWorkflow> teams = AtsApiService.get().getWorkItemService().getTeams(artifact);
            if (atsOpenOption == AtsOpenOption.OpenAll) {
               for (IAtsTeamWorkflow team : teams) {
                  WorkflowEditor.edit(team);
               }
            } else if (atsOpenOption == AtsOpenOption.AtsWorld) {
               WorldEditor.open(new WorldEditorSimpleProvider("Action " + action.getAtsId(), Arrays.asList(artifact)));
            } else if (atsOpenOption == AtsOpenOption.OpenOneOrPopupSelect) {
               if (teams.size() == 1) {
                  WorkflowEditor.edit(teams.iterator().next());
               } else {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           TeamWorkFlowArtifact teamArt = promptSelectTeamWorkflow(artifact);
                           if (teamArt != null) {
                              WorkflowEditor.editArtifact(teamArt);
                           } else {
                              return;
                           }
                        } catch (OseeCoreException ex) {
                           OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                     }
                  });
               }
            }
         } else {
            WorkflowEditor.editArtifact(artifact);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static TeamWorkFlowArtifact promptSelectTeamWorkflow(Artifact actArt) {
      ListDialog ld = new ListDialog(Displays.getActiveShell());
      ld.setContentProvider(new ArrayContentProvider());
      ld.setLabelProvider(new TeamWorkflowLabelProvider());
      ld.setTitle("Select Team Workflow");
      ld.setMessage("Select Team Workflow");
      ld.setInput(AtsApiService.get().getWorkItemService().getTeams(actArt));
      if (ld.open() == 0) {
         if (ld.getResult().length == 0) {
            AWorkbench.popup("Error", "No Workflow Selected");
         } else {
            return (TeamWorkFlowArtifact) ld.getResult()[0];
         }
      }
      return null;
   }

   public static void openInAtsWorldEditor(Collection<? extends IAtsWorkItem> workItems, String name) {
      openInAtsWorldEditor(name, AtsObjects.getArtifacts(workItems));
   }

   public static void openInAtsWorldEditor(String name, Collection<? extends ArtifactId> artifacts) {
      Set<Artifact> otherArts = new HashSet<>();
      for (ArtifactId artId : artifacts) {
         Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(artId);
         if (art.isOfType(CoreArtifactTypes.UniversalGroup)) {
            WorldEditor.open(
               new WorldEditorUISearchItemProvider(new GroupWorldSearchItem(art), null, TableLoadOption.None));
         } else {
            otherArts.add(art);
         }
      }
      if (otherArts.size() > 0) {
         WorldEditor.open(new WorldEditorSimpleProvider(name, otherArts));
      }
   }

   public static void openInAtsWorldEditor(String name, Collection<? extends Artifact> artifacts,
      CustomizeData customizeData) {
      Set<Artifact> otherArts = new HashSet<>();
      for (Artifact art : artifacts) {
         if (art.isOfType(CoreArtifactTypes.UniversalGroup)) {
            WorldEditor.open(
               new WorldEditorUISearchItemProvider(new GroupWorldSearchItem(art), null, TableLoadOption.None));
         } else {
            otherArts.add(art);
         }
      }
      if (otherArts.size() > 0) {
         WorldEditor.open(new WorldEditorSimpleProvider(name, otherArts, customizeData));
      }
   }

   public static void openInAtsWorldEditor(String name, List<IAtsTeamWorkflow> newTeamWfs) {
      openInAtsWorldEditor(name, AtsObjects.getArtifacts(newTeamWfs));
   }

   public static void openInAtsTaskEditor(String name, Collection<Artifact> artifacts) {
      TaskEditor.open(new TaskEditorSimpleProvider(name, artifacts));
   }

   public static Image getImage(Collection<AtsUser> atsUsers) {
      Set<User> users = new HashSet<>();
      for (AtsUser user : atsUsers) {
         users.add((User) AtsApiService.get().getUserService().getUserByUserId(user.getUserId()).getStoreObject());
      }
      return FrameworkArtifactImageProvider.getUserImage(users);
   }

   public static void openAction(IAtsAction action, AtsOpenOption atsOpenOption) {
      openATSAction(action.getStoreObject(), atsOpenOption);
   }

   public static XViewerAlign getXViewerAlign(ColumnAlign columnAlign) {
      if (columnAlign == ColumnAlign.Center) {
         return XViewerAlign.Center;
      }
      if (columnAlign == ColumnAlign.Right) {
         return XViewerAlign.Right;
      }
      return XViewerAlign.Left;
   }

   public static ColumnAlign getColumnAlign(XViewerAlign xViewerAlign) {
      if (xViewerAlign == XViewerAlign.Center) {
         return ColumnAlign.Center;
      }
      if (xViewerAlign == XViewerAlign.Right) {
         return ColumnAlign.Right;
      }
      return ColumnAlign.Left;
   }

}