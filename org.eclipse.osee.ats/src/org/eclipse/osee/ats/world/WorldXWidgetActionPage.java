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
package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewAction;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorAction;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorSelectedAction;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class WorldXWidgetActionPage extends AtsXWidgetActionFormPage {

   private final WorldEditor worldEditor;
   private WorldComposite worldComposite;
   private Action filterCompletedAction, filterMyAssigneeAction, selectionMetricsAction, toAction, toReview,
         toWorkFlow, toTask;
   private final WorldCompletedFilter worldCompletedFilter = new WorldCompletedFilter();
   private WorldAssigneeFilter worldAssigneeFilter = null;
   protected Label showReleaseMetricsLabel;

   public WorldComposite getWorldComposite() {
      return worldComposite;
   }

   public WorldXWidgetActionPage(WorldEditor worldEditor) {
      super(worldEditor, "org.eclipse.osee.ats.actionPage", "Actions");
      this.worldEditor = worldEditor;
   }

   @Override
   public void createPartControl(Composite parent) {
      super.createPartControl(parent);
      scrolledForm.setImage(ImageManager.getImage(AtsImage.GLOBE));

      Result result = AtsPlugin.areOSEEServicesAvailable();
      if (result.isFalse()) {
         AWorkbench.popup("ERROR", "DB Connection Unavailable");
         return;
      }

      try {
         worldEditor.getWorldEditorProvider().run(worldEditor, SearchType.Search, false);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   protected void createToolBar(IToolBarManager toolBarManager) {

      toolBarManager.add(worldComposite.getXViewer().getCustomizeAction());
      toolBarManager.add(new Separator());
      toolBarManager.add(new OpenNewAtsWorldEditorAction(worldComposite));
      toolBarManager.add(new OpenNewAtsWorldEditorSelectedAction(worldComposite));
      toolBarManager.add(new Separator());
      toolBarManager.add(new ExpandAllAction(worldComposite.getXViewer()));
      toolBarManager.add(new CollapseAllAction(worldComposite.getXViewer()));
      toolBarManager.add(new RefreshAction(worldComposite));
      toolBarManager.add(new Separator());
      toolBarManager.add(new NewAction());
      toolBarManager.add(OseeAts.createBugAction(AtsPlugin.getInstance(), worldEditor, WorldEditor.EDITOR_ID,
            "ATS World"));
      toolBarManager.add(new Separator());

      createDropDownMenuActions();
      toolBarManager.add(new DropDownAction());
   }

   @Override
   public Section createResultsSection(Composite body) {
      resultsSection = toolkit.createSection(body, Section.NO_TITLE);
      resultsSection.setText("Results");
      resultsSection.setLayoutData(new GridData(GridData.FILL_BOTH));

      resultsContainer = toolkit.createClientContainer(resultsSection, 1);

      showReleaseMetricsLabel = toolkit.createLabel(resultsContainer, "");
      showReleaseMetricsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      worldComposite = new WorldComposite(worldEditor, resultsContainer, SWT.BORDER);
      toolkit.adapt(worldComposite);
      return resultsSection;
   }

   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      if (worldEditor.getWorldEditorProvider() instanceof IWorldEditorParameterProvider) {
         return ((IWorldEditorParameterProvider) worldEditor.getWorldEditorProvider()).getDynamicWidgetLayoutListener();
      }
      return null;
   }

   @Override
   public Result isResearchSearchValid() throws OseeCoreException {
      return worldEditor.isDirty() ? new Result("Changes un-saved. Save first.") : Result.TrueResult;
   }

   public void reSearch() throws OseeCoreException {
      Result result = isResearchSearchValid();
      if (result.isFalse()) {
         result.popup();
         return;
      }
      reSearch(false);
   }

   /*
    * Mainly for testing purposes
    */
   public void reSearch(boolean forcePend) throws OseeCoreException {
      worldEditor.getWorldEditorProvider().run(worldEditor, SearchType.ReSearch, forcePend);
   }

   @Override
   public String getXWidgetsXml() throws OseeCoreException {
      if (worldEditor.getWorldEditorProvider() instanceof IWorldEditorParameterProvider) {
         return ((IWorldEditorParameterProvider) worldEditor.getWorldEditorProvider()).getParameterXWidgetXml();
      }
      return null;
   }

   @Override
   public void handleSearchButtonPressed() {
      try {
         reSearch();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public class DropDownAction extends Action implements IMenuCreator {
      private Menu fMenu;

      public DropDownAction() {
         setText("Other");
         setMenuCreator(this);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GEAR));
         addKeyListener();
         addSelectionListener();
      }

      public Menu getMenu(Control parent) {
         if (fMenu != null) fMenu.dispose();

         fMenu = new Menu(parent);
         addActionToMenu(fMenu, selectionMetricsAction);
         addActionToMenu(fMenu, filterCompletedAction);
         addActionToMenu(fMenu, filterMyAssigneeAction);
         new MenuItem(fMenu, SWT.SEPARATOR);
         addActionToMenu(fMenu, toAction);
         addActionToMenu(fMenu, toWorkFlow);
         addActionToMenu(fMenu, toTask);
         addActionToMenu(fMenu, toReview);

         worldEditor.createToolBarPulldown(fMenu);

         return fMenu;
      }

      public void dispose() {
         if (fMenu != null) {
            fMenu.dispose();
            fMenu = null;
         }
      }

      public Menu getMenu(Menu parent) {
         return null;
      }

      protected void addActionToMenu(Menu parent, Action action) {
         ActionContributionItem item = new ActionContributionItem(action);
         item.fill(parent, -1);
      }

      @Override
      public void run() {

      }

      /**
       * Get's rid of the menu, because the menu hangs on to * the searches, etc.
       */
      void clear() {
         dispose();
      }

      private void addKeyListener() {
         Tree tree = worldComposite.getXViewer().getTree();
         GridData gridData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
         gridData.heightHint = 100;
         gridData.widthHint = 100;
         tree.setLayoutData(gridData);
         tree.setHeaderVisible(true);
         tree.setLinesVisible(true);

         worldComposite.getXViewer().getTree().addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent event) {
            }

            public void keyReleased(KeyEvent event) {
               // if CTRL key is already pressed
               if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
                  if (event.keyCode == 'a') {
                     worldComposite.getXViewer().getTree().setSelection(
                           worldComposite.getXViewer().getTree().getItems());
                  } else if (event.keyCode == 'x') {
                     selectionMetricsAction.setChecked(!selectionMetricsAction.isChecked());
                     selectionMetricsAction.run();
                  } else if (event.keyCode == 'f') {
                     filterCompletedAction.setChecked(!filterCompletedAction.isChecked());
                     filterCompletedAction.run();
                  } else if (event.keyCode == 'g') {
                     filterMyAssigneeAction.setChecked(!filterMyAssigneeAction.isChecked());
                     filterMyAssigneeAction.run();
                  } else if (event.keyCode == 'd') {
                     filterMyAssigneeAction.setChecked(!filterMyAssigneeAction.isChecked());
                     filterCompletedAction.setChecked(!filterCompletedAction.isChecked());
                     filterCompletedAction.run();
                     filterMyAssigneeAction.run();
                  }
               }

            }
         });
      }
   }

   private void addSelectionListener() {
      worldComposite.getXViewer().getTree().addSelectionListener(new SelectionListener() {
         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (selectionMetricsAction != null) {
               if (selectionMetricsAction.isChecked()) {
                  selectionMetricsAction.run();
               } else {
                  if (worldComposite != null) {
                     showReleaseMetricsLabel.setText("");
                  }
               }
            }
         }
      });
   }

   public void updateExtraInfoLine() throws OseeCoreException {
      if (selectionMetricsAction != null && selectionMetricsAction.isChecked()) {
         if (worldComposite.getXViewer() != null && worldComposite.getXViewer().getSelectedSMAArtifacts() != null && !worldComposite.getXViewer().getSelectedSMAArtifacts().isEmpty()) {
            showReleaseMetricsLabel.setText(SMAMetrics.getEstRemainMetrics(
                  worldComposite.getXViewer().getSelectedSMAArtifacts(), null,
                  worldComposite.getXViewer().getSelectedSMAArtifacts().iterator().next().getManHrsPerDayPreference(),
                  null));
         } else
            showReleaseMetricsLabel.setText("");
      } else
         showReleaseMetricsLabel.setText("");
      showReleaseMetricsLabel.getParent().layout();
   }

   protected void createDropDownMenuActions() {
      try {
         worldAssigneeFilter = new WorldAssigneeFilter();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      selectionMetricsAction = new Action("Show Release Metrics by Selection - Ctrl-X", Action.AS_CHECK_BOX) {
         @Override
         public void run() {
            try {
               updateExtraInfoLine();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
      selectionMetricsAction.setToolTipText("Show Release Metrics by Selection - Ctrl-X");
      selectionMetricsAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.PAGE));

      filterCompletedAction = new Action("Filter Out Completed/Cancelled - Ctrl-F", Action.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (filterCompletedAction.isChecked()) {
               worldComposite.getXViewer().addFilter(worldCompletedFilter);
            } else {
               worldComposite.getXViewer().removeFilter(worldCompletedFilter);
            }
            updateExtendedStatusString();
            worldComposite.getXViewer().refresh();
         }
      };
      filterCompletedAction.setToolTipText("Filter Out Completed/Cancelled - Ctrl-F");
      filterCompletedAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.GREEN_PLUS));

      filterMyAssigneeAction = new Action("Filter My Assignee - Ctrl-G", Action.AS_CHECK_BOX) {

         @Override
         public void run() {
            if (filterMyAssigneeAction.isChecked()) {
               worldComposite.getXViewer().addFilter(worldAssigneeFilter);
            } else {
               worldComposite.getXViewer().removeFilter(worldAssigneeFilter);
            }
            updateExtendedStatusString();
            worldComposite.getXViewer().refresh();
         }
      };
      filterMyAssigneeAction.setToolTipText("Filter My Assignee - Ctrl-G");
      filterMyAssigneeAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.USER));

      toAction = new Action("Re-display as Actions", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsAction();
         }
      };
      toAction.setToolTipText("Re-display as Actions");
      toAction.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ACTION));

      toWorkFlow = new Action("Re-display as WorkFlows", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsWorkFlow();
         }
      };
      toWorkFlow.setToolTipText("Re-display as WorkFlows");
      toWorkFlow.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.WORKFLOW));

      toTask = new Action("Re-display as Tasks", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsTask();
         }
      };
      toTask.setToolTipText("Re-display as Tasks");
      toTask.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TASK));

      toReview = new Action("Re-display as Reviews", Action.AS_PUSH_BUTTON) {

         @Override
         public void run() {
            redisplayAsReviews();
         }
      };
      toReview.setToolTipText("Re-display as Reviews");
      toReview.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.REVIEW));

   }

   public void redisplayAsAction() {
      final ArrayList<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job("Re-display as Actions") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<Artifact>();
               for (Artifact art : artifacts) {
                  if (art instanceof ActionArtifact) {
                     arts.add(art);
                  } else if (art instanceof StateMachineArtifact) {
                     Artifact parentArt = ((StateMachineArtifact) art).getParentActionArtifact();
                     if (parentArt != null) {
                        arts.add(parentArt);
                     }
                  }
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     worldComposite.load(worldEditor.getWorldXWidgetActionPage().getCurrentTitleLabel(), arts);
                  }
               });
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void redisplayAsWorkFlow() {
      final ArrayList<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job("Re-display as Workflows") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<Artifact>();
               for (Artifact art : artifacts) {
                  if (art instanceof ActionArtifact) {
                     arts.addAll(((ActionArtifact) art).getTeamWorkFlowArtifacts());
                  } else if (art instanceof StateMachineArtifact) {
                     Artifact parentArt = ((StateMachineArtifact) art).getParentTeamWorkflow();
                     if (parentArt != null) {
                        arts.add(parentArt);
                     }
                  }
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     worldComposite.load(worldEditor.getWorldXWidgetActionPage().getCurrentTitleLabel(), arts);
                  }
               });
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void redisplayAsTask() {
      final ArrayList<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job("Re-display as Tasks") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<Artifact>();
               for (Artifact art : artifacts) {
                  if (art instanceof ActionArtifact) {
                     for (TeamWorkFlowArtifact team : ((ActionArtifact) art).getTeamWorkFlowArtifacts()) {
                        arts.addAll(team.getSmaMgr().getTaskMgr().getTaskArtifacts());
                     }
                  } else if (art instanceof StateMachineArtifact) {
                     arts.addAll(((StateMachineArtifact) art).getSmaMgr().getTaskMgr().getTaskArtifacts());
                  }
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     worldComposite.load(worldEditor.getWorldXWidgetActionPage().getCurrentTitleLabel(), arts);
                  }
               });
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void redisplayAsReviews() {
      final ArrayList<Artifact> artifacts = worldComposite.getXViewer().getLoadedArtifacts();
      Job job = new Job("Re-display as Reviews") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               final Set<Artifact> arts = new HashSet<Artifact>();
               for (Artifact art : artifacts) {
                  if (art instanceof ActionArtifact) {
                     for (TeamWorkFlowArtifact team : ((ActionArtifact) art).getTeamWorkFlowArtifacts()) {
                        arts.addAll(team.getSmaMgr().getReviewManager().getReviews());
                     }
                  } else if (art instanceof StateMachineArtifact) {
                     arts.addAll(((StateMachineArtifact) art).getSmaMgr().getReviewManager().getReviews());
                  }
               }
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     worldComposite.load(worldEditor.getWorldXWidgetActionPage().getCurrentTitleLabel(), arts);
                  }
               });
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
   }

   public void updateExtendedStatusString() {
      worldComposite.getXViewer().setExtendedStatusString(
      //
            (filterCompletedAction.isChecked() ? "[Complete/Cancel Filter]" : "") +
            //
            (filterMyAssigneeAction.isChecked() ? "[My Assignee Filter]" : ""));
   }

}
