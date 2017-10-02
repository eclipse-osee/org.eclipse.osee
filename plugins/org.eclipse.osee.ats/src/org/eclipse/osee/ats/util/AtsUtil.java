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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.actions.ModifyActionableItemAction;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.artifact.TeamWorkflowLabelProvider;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.search.AtsArtifactQuery;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.editor.WorkflowEditor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.ats.world.WorldEditorUISearchItemProvider;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.FrameworkArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public final class AtsUtil {

   public final static Color ACTIVE_COLOR = new Color(null, 206, 212, 239);
   private static final Date today = new Date();
   public final static int MILLISECS_PER_DAY = 1000 * 60 * 60 * 24;
   public final static String normalColor = "#FFFFFF";
   public final static String activeColor = "#EEEEEE";

   private AtsUtil() {
      super();
   }

   public static boolean isProductionDb()  {
      return ClientSessionManager.isProductionDataStore();
   }

   public static long daysTillToday(Date date) {
      return (date.getTime() - today.getTime()) / MILLISECS_PER_DAY;
   }

   public static boolean isInTest() {
      return Boolean.valueOf(System.getProperty("osee.isInTest"));
   }

   public static Composite createCommonPageComposite(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      layout.verticalSpacing = 0;
      composite.setLayout(layout);

      return composite;
   }

   /**
    * The development of ATS requires quite a few Actions to be created. To facilitate this, this method will retrieve a
    * persistent number from the file-system so each action has a different name. By entering "tt" in the title, new
    * action wizard will be pre-populated with selections and the action name will be created as "tt <number in
    * atsNumFilename>". Get an incrementing number. This number only resets on new workspace creation. Should not be
    * used for anything but developmental purposes.
    */
   private static int atsDevNum = 0;

   public static int getAtsDeveloperIncrementingNum() {
      try {
         File numFile = OseeData.getFile("atsDevNum.txt");
         if (numFile.exists() && atsDevNum == 0) {
            try {
               atsDevNum = new Integer(Lib.fileToString(numFile).replaceAll("\\s", ""));
            } catch (NumberFormatException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            } catch (NullPointerException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         atsDevNum++;
         Lib.writeStringToFile(String.valueOf(atsDevNum), numFile);
         return atsDevNum;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return 99;
   }

   public static ToolBar createCommonToolBar(Composite parent) {
      return createCommonToolBar(parent, null);
   }

   public static ToolBar createCommonToolBar(Composite parent, XFormToolkit toolkit) {
      ToolBar toolBar = ALayout.createCommonToolBar(parent);
      if (toolkit != null) {
         toolkit.adapt(toolBar.getParent());
      }
      if (toolkit != null) {
         toolkit.adapt(toolBar);
      }
      return toolBar;
   }

   public static void editActionableItems(TeamWorkFlowArtifact teamArt) {
      new ModifyActionableItemAction(teamArt).run();
   }

   public static void openArtifact(String guid, BranchId branch, OseeCmEditor view) {
      try {
         Artifact artifact = ArtifactQuery.getArtifactFromId(guid, branch);
         openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * Only to be used by browser. Use open (artifact) instead.
    */
   public static void openArtifact(long uuid, OseeCmEditor editor) {
      Artifact artifact = null;
      try {
         artifact = AtsArtifactQuery.getArtifactFromId(uuid);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return;
      }

      openArtifact(artifact, editor);
   }

   public static void openArtifact(String guid, OseeCmEditor editor) {
      Artifact artifact = null;
      try {
         artifact = AtsArtifactQuery.getArtifactFromId(guid);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return;
      }

      openArtifact(artifact, editor);
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

   public static void openATSArtifact(Artifact art) {
      if (art.isOfType(AtsArtifactTypes.AtsArtifact)) {
         try {
            openATSAction(art, AtsOpenOption.OpenOneOrPopupSelect);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            AWorkbench.popup("ERROR", ex.getLocalizedMessage());
         }
      } else {
         AWorkbench.popup("ERROR", "Trying to open " + art.getArtifactTypeName() + " with SMAEditor");
      }
   }

   public static void openATSAction(final Artifact art, final AtsOpenOption atsOpenOption) {
      try {
         if (art.isOfType(AtsArtifactTypes.Action)) {
            ActionArtifact action = (ActionArtifact) art;
            Collection<IAtsTeamWorkflow> teams = AtsClientService.get().getWorkItemService().getTeams(art);
            if (atsOpenOption == AtsOpenOption.OpenAll) {
               for (IAtsTeamWorkflow team : teams) {
                  WorkflowEditor.edit(team);
               }
            } else if (atsOpenOption == AtsOpenOption.AtsWorld) {
               WorldEditor.open(new WorldEditorSimpleProvider("Action " + action.getAtsId(), Arrays.asList(art)));
            } else if (atsOpenOption == AtsOpenOption.OpenOneOrPopupSelect) {
               if (teams.size() == 1) {
                  WorkflowEditor.edit(teams.iterator().next());
               } else {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           TeamWorkFlowArtifact teamArt = promptSelectTeamWorkflow(art);
                           if (teamArt != null) {
                              WorkflowEditor.editArtifact((Artifact) teamArt);
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
            WorkflowEditor.editArtifact(art);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static TeamWorkFlowArtifact promptSelectTeamWorkflow(Artifact actArt)  {
      ListDialog ld = new ListDialog(Displays.getActiveShell());
      ld.setContentProvider(new ArrayContentProvider());
      ld.setLabelProvider(new TeamWorkflowLabelProvider());
      ld.setTitle("Select Team Workflow");
      ld.setMessage("Select Team Workflow");
      ld.setInput(AtsClientService.get().getWorkItemService().getTeams(actArt));
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
         Artifact art = (Artifact) artId;
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

   public static void openInAtsWorldEditor(String name, Collection<? extends Artifact> artifacts, CustomizeData customizeData) {
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

   public static void openInAtsTaskEditor(String name, Collection<Artifact> artifacts)  {
      TaskEditor.open(new TaskEditorSimpleProvider(name, artifacts));
   }

   public static ToolItem actionToToolItem(ToolBar toolBar, Action action, KeyedImage imageEnum) {
      final Action fAction = action;
      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(imageEnum));
      item.setToolTipText(action.getToolTipText());
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            fAction.run();
         }
      });
      return item;
   }

   public static MenuItem actionToMenuItem(Menu menu, final Action action, final int buttonType) {
      final Action fAction = action;
      MenuItem item = new MenuItem(menu, buttonType);
      item.setText(action.getText());
      if (action.getImageDescriptor() != null) {
         item.setImage(action.getImageDescriptor().createImage());
      }
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (buttonType == SWT.CHECK) {
               action.setChecked(!action.isChecked());
            }
            fAction.run();
         }
      });
      return item;
   }

   /**
    * return currently assigned state machine artifacts
    */
   public static Set<Artifact> getAssigned(IAtsUser user)  {
      return getAssigned(user, null);
   }

   /**
    * return currently assigned state machine artifacts that match clazz
    *
    * @param clazz to match or all if null
    */
   public static Set<Artifact> getAssigned(IAtsUser user, Class<?> clazz)  {
      return getAssigned(user.getUserId(), clazz);
   }

   /**
    * return currently assigned state machine artifacts that match clazz
    *
    * @param clazz to match or all if null
    */
   public static Set<Artifact> getAssigned(String userId, Class<?> clazz)  {
      Set<Artifact> assigned = new HashSet<>();
      for (Artifact artifact : ArtifactQuery.getArtifactListFromAttribute(AtsAttributeTypes.CurrentState,
         "<" + userId + ">", AtsClientService.get().getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS)) {
         if (clazz == null || clazz.isInstance(artifact)) {
            assigned.add(artifact);
         }
      }
      return assigned;

   }

   /**
    * @return true if Action artifact or AbstractWorkflowArtifact
    */
   public static boolean isAtsArtifact(Object object) {
      if (object instanceof Artifact) {
         return ((Artifact) object).isOfType(AtsArtifactTypes.AbstractWorkflowArtifact, AtsArtifactTypes.Action);
      }
      return false;
   }

   public static Image getImage(Collection<IAtsUser> atsUsers) {
      Set<User> users = new HashSet<>();
      for (IAtsUser user : atsUsers) {
         users.add((User) AtsClientService.get().getUserService().getUserById(user.getUserId()).getStoreObject());
      }
      return FrameworkArtifactImageProvider.getUserImage(users);
   }

   public static void openAction(IAtsAction action, AtsOpenOption atsOpenOption) {
      openATSAction((Artifact) action.getStoreObject(), atsOpenOption);
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