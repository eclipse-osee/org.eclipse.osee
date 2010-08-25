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
package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.goal.GoalXViewerFactory;
import org.eclipse.osee.ats.goal.RemoveFromGoalAction;
import org.eclipse.osee.ats.goal.SetGoalOrderAction;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.world.IMenuActionProvider;
import org.eclipse.osee.ats.world.IWorldEditor;
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.WorldComposite;
import org.eclipse.osee.ats.world.WorldLabelProvider;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDragAndDrop;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class SMAGoalMembersSection extends SectionPart implements ISelectedAtsArtifacts, IWorldEditor, IMenuActionProvider {

   private final SMAEditor editor;
   private WorldComposite worldComposite;
   private static final Map<SMAEditor, CustomizeData> editorToCustDataMap = new HashMap<SMAEditor, CustomizeData>(20);
   private static Map<SMAEditor, Boolean> editorToTableExpanded = new HashMap<SMAEditor, Boolean>();
   private final static int DEFAULT_TABLE_HEIGHT = 400;

   public SMAGoalMembersSection(SMAEditor editor, Composite parent, XFormToolkit toolkit, int style) {
      super(parent, toolkit, style | ExpandableComposite.TITLE_BAR);
      this.editor = editor;
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();

      Section section = getSection();
      section.setText("Members");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
      sectionBody.setLayout(ALayout.getZeroMarginLayout(2, false));
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.widthHint = 300;
      sectionBody.setLayoutData(gd);

      ToolBar toolBar = createToolBar(sectionBody);
      addDropToAddLabel(toolkit, sectionBody);
      addDropToRemoveLabel(toolkit, sectionBody);

      createWorldComposite(sectionBody);
      createActions();
      setupListenersForCustomizeDataCaching();
      fillActionBar(toolBar);

      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);

      RefreshTableSizeJob job = new RefreshTableSizeJob("");
      job.schedule(300);
   }

   private class RefreshTableSizeJob extends Job {
      public RefreshTableSizeJob(String name) {
         super(name);
      }

      @Override
      protected IStatus run(IProgressMonitor monitor) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               refreshTableSize();
            }
         });
         return Status.OK_STATUS;
      }
   }

   private ToolBar createToolBar(Composite parent) {
      Composite actionComp = new Composite(parent, SWT.NONE);
      actionComp.setLayout(ALayout.getZeroMarginLayout());
      GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
      gd.horizontalSpan = 2;
      actionComp.setLayoutData(gd);

      ToolBar toolBar = new ToolBar(actionComp, SWT.FLAT | SWT.RIGHT);
      gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);

      ToolItem expandItem = new ToolItem(toolBar, SWT.PUSH);
      expandItem.setImage(ImageManager.getImage(AtsImage.EXPAND_TABLE));
      expandItem.setToolTipText("Expand/Collapse Table Height");
      expandItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            toggleTableExpand();
            refreshTableSize();
         }
      });

      return toolBar;
   }

   private void refreshTableSize() {
      GridData gd = null;
      if (!isTableExpanded()) {
         gd = new GridData(SWT.FILL, SWT.NONE, true, false);
         gd.heightHint = DEFAULT_TABLE_HEIGHT;
      } else {
         gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      }
      gd.horizontalSpan = 2;
      worldComposite.setLayoutData(gd);
      worldComposite.layout(true);
      getManagedForm().reflow(true);
   }

   private void fillActionBar(ToolBar toolBar) {

      new ActionContributionItem(worldComposite.getXViewer().getCustomizeAction()).fill(toolBar, -1);
   }

   private void createWorldComposite(final Composite sectionBody) {
      worldComposite =
         new WorldComposite(this, new GoalXViewerFactory((GoalArtifact) editor.getSma()), sectionBody, SWT.BORDER);

      CustomizeData customizeData = editorToCustDataMap.get(editor);
      if (customizeData == null) {
         customizeData = worldComposite.getCustomizeDataCopy();
      }
      WorldLabelProvider labelProvider = (WorldLabelProvider) worldComposite.getXViewer().getLabelProvider();
      labelProvider.setParentGoal((GoalArtifact) editor.getSma());

      worldComposite.getWorldXViewer().addMenuActionProvider(this);

      try {
         customizeData = null;
         worldComposite.load("Members", editor.getSma().getRelatedArtifacts(AtsRelationTypes.Goal_Member),
            customizeData, TableLoadOption.None);

      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

   }

   private boolean isTableExpanded() {
      if (editor != null && editorToTableExpanded.containsKey(editor)) {
         return editorToTableExpanded.get(editor);
      }
      return false;
   }

   private void toggleTableExpand() {
      if (editor != null) {
         Boolean expanded = editorToTableExpanded.get(editor);
         if (expanded == null) {
            expanded = true;
         } else {
            expanded = !expanded;
         }
         editorToTableExpanded.put(editor, expanded);
      }
   }

   private void setupListenersForCustomizeDataCaching() {
      worldComposite.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            editorToCustDataMap.put(editor, worldComposite.getCustomizeDataCopy());
         }
      });
      editor.addEditorListeners(new ISMAEditorListener() {

         @Override
         public void editorDisposing() {
            editorToCustDataMap.remove(editor);
            editorToTableExpanded.remove(editor);
         }
      });
   }

   protected void addDropToAddLabel(FormToolkit toolkit, Composite sectionBody) {
      Label dropToAddLabel = new Label(sectionBody, SWT.BORDER);
      dropToAddLabel.setText(" Drop New Members Here");
      dropToAddLabel.setBackgroundImage(ImageManager.getImage(AtsImage.DROP_HERE_TO_ADD_BACKGROUND));
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 25;
      dropToAddLabel.setLayoutData(gd);
      toolkit.adapt(dropToAddLabel, true, true);

      new ArtifactDragAndDrop(dropToAddLabel, editor.getSma(), ArtifactEditor.EDITOR_ID) {
         @Override
         public void performArtifactDrop(Artifact[] dropArtifacts) {
            super.performArtifactDrop(dropArtifacts);
            try {
               List<Artifact> members = new ArrayList<Artifact>();
               members.addAll(((GoalArtifact) editor.getSma()).getMembers());
               for (Artifact art : dropArtifacts) {
                  if (!members.contains(art)) {
                     members.add(art);
                     editor.getSma().addRelation(AtsRelationTypes.Goal_Member, art);
                  }
               }
               editor.getSma().setRelationOrder(AtsRelationTypes.Goal_Member, members);
               editor.doSave(null);
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };
   }

   protected void addDropToRemoveLabel(FormToolkit toolkit, Composite sectionBody) {
      Label dropToAddLabel = new Label(sectionBody, SWT.BORDER);
      dropToAddLabel.setText(" Drop Members to Remove");
      dropToAddLabel.setBackgroundImage(ImageManager.getImage(AtsImage.DROP_HERE_TO_REMOVE_BACKGROUND));
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 25;
      dropToAddLabel.setLayoutData(gd);
      toolkit.adapt(dropToAddLabel, true, true);

      new ArtifactDragAndDrop(dropToAddLabel, editor.getSma(), ArtifactEditor.EDITOR_ID) {
         @Override
         public void performArtifactDrop(Artifact[] dropArtifacts) {
            super.performArtifactDrop(dropArtifacts);
            final Set<Artifact> artifacts = new HashSet<Artifact>();
            final List<Artifact> artList = new ArrayList<Artifact>();
            for (Artifact artifact : dropArtifacts) {
               artifacts.add(artifact);
               artList.add(artifact);
            }
            RemoveFromGoalAction remove =
               new RemoveFromGoalAction((GoalArtifact) editor.getSma(), new ISelectedAtsArtifacts() {

                  @Override
                  public Set<? extends Artifact> getSelectedSMAArtifacts() {
                     return artifacts;
                  }

                  @Override
                  public List<Artifact> getSelectedAtsArtifacts() {
                     return artList;
                  }
               });
            remove.run();
         }
      };
   }

   @Override
   public void refresh() {
      super.refresh();
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            if (Widgets.isAccessible(worldComposite)) {
               worldComposite.getXViewer().refresh();
            }
         }
      });
   }

   @Override
   public void dispose() {
      if (Widgets.isAccessible(worldComposite)) {
         worldComposite.dispose();
      }
      super.dispose();
   }

   @Override
   public void createToolBarPulldown(Menu menu) {
      // do nothing
   }

   @Override
   public String getCurrentTitleLabel() {
      return "";
   }

   @Override
   public IActionable getIActionable() {
      return null;
   }

   @Override
   public IWorldEditorProvider getWorldEditorProvider() {
      return null;
   }

   @Override
   public void reSearch() {
      // do nothing
   }

   @Override
   public void reflow() {
      // do nothing
   }

   @Override
   public void setTableTitle(String title, boolean warning) {
      // do nothing
   }

   Action setGoalOrderAction, removeFromGoalAction;

   public void createActions() {
      setGoalOrderAction = new SetGoalOrderAction((GoalArtifact) editor.getSma(), this);
      removeFromGoalAction = new RemoveFromGoalAction((GoalArtifact) editor.getSma(), this);
   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = worldComposite.getXViewer().getMenuManager();

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, setGoalOrderAction);
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, removeFromGoalAction);
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, new Separator());
   }

   @Override
   public Set<Artifact> getSelectedSMAArtifacts() {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      for (Artifact art : worldComposite.getSelectedArtifacts()) {
         if (art instanceof StateMachineArtifact) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   @Override
   public List<Artifact> getSelectedAtsArtifacts() {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      for (Artifact art : worldComposite.getSelectedArtifacts()) {
         if (art.isOfType(AtsArtifactTypes.AtsArtifact)) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }
}
