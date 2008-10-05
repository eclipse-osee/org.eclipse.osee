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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.navigate.VisitedItems;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResolutionOptionRule;
import org.eclipse.osee.ats.util.widgets.task.IXTaskViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.IRelationModifiedEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.AttributesComposite;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Donald G. Dunne
 */
public class SMAEditor extends AbstractArtifactEditor implements IDirtiableEditor, IActionable, IArtifactsPurgedEventListener, IRelationModifiedEventListener, IFrameworkTransactionEventListener, IBranchEventListener, IXTaskViewer {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.editor.SMAEditor";
   private SMAManager smaMgr;
   private int workFlowPageIndex, taskPageIndex, historyPageIndex, relationPageIndex, attributesPageIndex;
   private SMAWorkFlowTab workFlowTab;
   private SMATaskComposite taskComposite;
   private SMAHistoryComposite historyComposite;
   private RelationsComposite relationsComposite;
   private AttributesComposite attributesComposite;
   private final MultiPageEditorPart editor;
   public static enum PriviledgedEditMode {
      Off, CurrentState, Global
   };
   private PriviledgedEditMode priviledgedEditMode = PriviledgedEditMode.Off;
   private Action printAction;

   public SMAEditor() {
      super();
      editor = this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
      if (smaMgr.isHistoricalVersion()) {
         AWorkbench.popup(
               "Historical Error",
               "You can not change a historical version of " + smaMgr.getSma().getArtifactTypeName() + ":\n\n" + smaMgr.getSma());
      } else if (!smaMgr.isAccessControlWrite()) {
         AWorkbench.popup("Authentication Error",
               "You do not have permissions to save " + smaMgr.getSma().getArtifactTypeName() + ":" + smaMgr.getSma());
      } else {
         try {
            AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
               @Override
               protected void handleTxWork() throws OseeCoreException, SQLException {
                  if (getActivePage() == attributesPageIndex) {
                     smaMgr.getSma().persistAttributes();
                  }
                  // Save widget data to artifact
                  workFlowTab.saveXWidgetToArtifact();
                  smaMgr.getSma().saveSMA();
               }
            };
            txWrapper.execute();
            workFlowTab.refresh();
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
         onDirtied();
      }
      try {
         OseeNotificationManager.sendNotifications();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }

   void enableGlobalPrint() {
      printAction = new SMAPrint(smaMgr, workFlowTab, taskComposite);
      getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), printAction);
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      if (smaMgr != null && !smaMgr.getSma().isDeleted() && smaMgr.getSma().isSMAEditorDirty().isTrue()) {
         smaMgr.getSma().revertSMA();
      }
      workFlowTab.dispose();
      if (taskComposite != null) {
         taskComposite.dispose();
      }
      if (relationsComposite != null) {
         relationsComposite.disposeRelationsComposite();
      }
      super.dispose();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
    */
   @Override
   public boolean isDirty() {
      return isDirtyResult().isTrue();
   }

   public Result isDirtyResult() {
      if (smaMgr.getSma().isDeleted()) return Result.FalseResult;
      try {
         Result result = workFlowTab.isXWidgetDirty();
         if (result.isTrue()) return result;

         result = ((StateMachineArtifact) ((SMAEditorInput) getEditorInput()).getArtifact()).isSMAEditorDirty();
         if (result.isTrue()) return result;

         if (smaMgr.getSma().isDirty(true)) {
            return new Result(true, "Another relation is dirty");
         }

      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return new Result(true, ex.getLocalizedMessage());
      }
      return Result.FalseResult;
   }

   @Override
   public String toString() {
      return "SMAEditor - " + smaMgr.getSma().getHumanReadableId() + " - " + smaMgr.getSma().getArtifactTypeName() + " named \"" + smaMgr.getSma().getDescriptiveName() + "\"";
   }

   @Override
   protected void createPages() {
      super.createPages();
      SkynetContributionItem.addTo(this, true);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
    */
   @Override
   protected void addPages() {

      IEditorInput editorInput = getEditorInput();
      StateMachineArtifact sma = null;
      if (editorInput instanceof SMAEditorInput) {
         SMAEditorInput aei = (SMAEditorInput) editorInput;
         if (aei.getArtifact() != null) {
            if (aei.getArtifact() instanceof StateMachineArtifact)
               sma = (StateMachineArtifact) aei.getArtifact();
            else
               throw new IllegalArgumentException("SMAEditorInput artifact must be StateMachineArtifact");
         }
      } else
         throw new IllegalArgumentException("Editor Input not SMAEditorInput");

      if (sma == null) {
         MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Error",
               "Can't Find Action in DB");
         return;
      }
      try {
         smaMgr = new SMAManager(sma, this);
         smaMgr.setEditor(this);

         OseeEventManager.addListener(this);

         setPartName(smaMgr.getSma().getEditorTitle());
         setContentDescription(priviledgedEditMode != PriviledgedEditMode.Off ? " PRIVILEGED EDIT MODE ENABLED - " + priviledgedEditMode.name() : "");
         setTitleImage(smaMgr.getSma().getImage());

         // Create WorkFlow tab
         workFlowTab = new SMAWorkFlowTab(smaMgr);
         workFlowPageIndex = addPage(workFlowTab);

         // Create Tasks tab
         if (smaMgr.showTaskTab()) {
            taskComposite = new SMATaskComposite(this, getContainer(), SWT.NONE);
            taskPageIndex = addPage(taskComposite);
            setPageText(taskPageIndex, "Tasks");
         }

         // Create History tab
         Composite composite = createCommonPageComposite();
         createCommonToolBar(composite);
         historyComposite = new SMAHistoryComposite(smaMgr, composite, SWT.NONE);
         historyPageIndex = addPage(composite);
         setPageText(historyPageIndex, "History");

         createRelationsTab();

         createAttributesTab();

         setActivePage(workFlowPageIndex);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }

      enableGlobalPrint();
   }

   private void createAttributesTab() {
      if (!AtsPlugin.isAtsAdmin()) return;

      // Create Attributes tab
      Composite composite = createCommonPageComposite();
      ToolBar toolBar = createCommonToolBar(composite);

      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("save.gif"));
      item.setToolTipText("Save attributes changes only");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               AbstractSkynetTxTemplate txWrapper =
                     new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {
                        @Override
                        protected void handleTxWork() throws OseeCoreException, SQLException {
                           smaMgr.getSma().persistAttributes();
                        }
                     };
               txWrapper.execute();
            } catch (Exception ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      });

      Label label = new Label(composite, SWT.NONE);
      label.setText("  NOTE: Changes made on this page MUST be saved through save icon on this page");
      label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

      attributesComposite = new AttributesComposite(this, composite, SWT.NONE, smaMgr.getSma());
      attributesPageIndex = addPage(composite);
      setPageText(attributesPageIndex, "Attributes");
   }

   private void createRelationsTab() {
      // Create Relations tab
      Composite composite = createCommonPageComposite();
      ToolBar toolBar = createCommonToolBar(composite);

      if (AtsPlugin.isAtsAdmin()) {
         final ToolItem showAllRelationsItem = new ToolItem(toolBar, SWT.CHECK);
         showAllRelationsItem.setImage(AtsPlugin.getInstance().getImage("relate.gif"));
         showAllRelationsItem.setToolTipText("Shows all relations - AtsAdmin only");
         showAllRelationsItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               if (showAllRelationsItem.getSelection()) {
                  relationsComposite.getTreeViewer().removeFilter(userRelationsFilter);
                  relationsComposite.refreshArtifact(smaMgr.getSma());
               } else {
                  relationsComposite.getTreeViewer().addFilter(userRelationsFilter);
               }
               relationsComposite.refresh();
            }
         });
      }

      ToolItem item = new ToolItem(toolBar, SWT.CHECK);
      item.setImage(AtsPlugin.getInstance().getImage("refresh.gif"));
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            relationsComposite.refreshArtifact(smaMgr.getSma());
         }
      });

      relationsComposite = new RelationsComposite(this, composite, SWT.NONE, smaMgr.getSma());
      relationPageIndex = addPage(composite);
      setPageText(relationPageIndex, "Relations");
      // Don't allow users to see all relations
      relationsComposite.getTreeViewer().addFilter(userRelationsFilter);

   }

   private static List<String> filteredRelationTypeNames =
         Arrays.asList(AtsRelation.ActionToWorkflow_Action.getTypeName(), AtsRelation.SmaToTask_Sma.getTypeName(),
               AtsRelation.TeamActionableItem_ActionableItem.getTypeName(),
               AtsRelation.TeamWorkflowTargetedForVersion_Version.getTypeName(),
               AtsRelation.TeamLead_Lead.getTypeName(), AtsRelation.TeamMember_Member.getTypeName(),
               AtsRelation.TeamWorkflowToReview_Review.getTypeName(), AtsRelation.WorkItem__Child.getTypeName(),
               CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD.getTypeName(),
               CoreRelationEnumeration.Users_Artifact.getTypeName());

   private static ViewerFilter userRelationsFilter = new ViewerFilter() {
      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
       */
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
         if (element instanceof RelationType) {
            return !filteredRelationTypeNames.contains(((RelationType) element).getTypeName());
         }
         return true;
      }
   };

   protected ToolBar createCommonToolBar(Composite parent) {
      Composite toolBarComposite = new Composite(parent, SWT.BORDER);
      GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1);
      toolBarComposite.setLayoutData(gridData);
      GridLayout layout = new GridLayout(2, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      toolBarComposite.setLayout(layout);

      ToolBar toolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);

      gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1);
      toolBar.setLayoutData(gridData);
      SkynetGuiPlugin skynetGuiPlugin = SkynetGuiPlugin.getInstance();
      ToolItem item;

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, EDITOR_ID, "ATS Editor");

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(skynetGuiPlugin.getImage("edit.gif"));
      item.setToolTipText("Show this artifact in the Resource History");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            RevisionHistoryView.open(smaMgr.getSma());
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("authenticated.gif"));
      item.setToolTipText("Access Control");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            PolicyDialog pd = new PolicyDialog(Display.getCurrent().getActiveShell(), smaMgr.getSma());
            pd.open();
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      Text artifactInfoLabel = new Text(toolBarComposite, SWT.END);
      artifactInfoLabel.setEditable(false);
      artifactInfoLabel.setText("Type: \"" + smaMgr.getSma().getArtifactTypeName() + "\"   HRID: " + smaMgr.getSma().getHumanReadableId());
      artifactInfoLabel.setToolTipText("The human readable id and database id for this artifact");

      return toolBar;
   }

   private Composite createCommonPageComposite() {
      Composite composite = new Composite(getContainer(), SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      layout.verticalSpacing = 0;
      composite.setLayout(layout);

      return composite;
   }

   public void refreshPages() throws OseeCoreException, SQLException {
      if (getContainer() == null || getContainer().isDisposed()) return;
      setTitleImage(smaMgr.getSma().getImage());
      if (workFlowTab != null) workFlowTab.refresh();
      if (historyComposite != null) historyComposite.refresh();
      if (relationsComposite != null) relationsComposite.refreshArtifact(smaMgr.getSma());
      if (attributesComposite != null) attributesComposite.refreshArtifact(smaMgr.getSma());
      smaMgr.getEditor().onDirtied();
   }

   public static void editArtifact(Artifact artifact) {
      if (artifact.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      if (artifact instanceof StateMachineArtifact)
         editArtifact((StateMachineArtifact) artifact);
      else
         ArtifactEditor.editArtifact(artifact);
   }

   public static void editArtifact(StateMachineArtifact sma) {
      if (sma.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      IWorkbenchPage page = AWorkbench.getActivePage();
      try {
         page.openEditor(new SMAEditorInput(sma), EDITOR_ID);
         VisitedItems.addVisited(sma);
      } catch (PartInitException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   @Override
   public void onDirtied() {
      Displays.ensureInDisplayThread(new Runnable() {

         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   public static void close(StateMachineArtifact artifact, boolean save) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         IEditorReference editor = editors[j];
         if (editor.getPart(false) instanceof SMAEditor) {
            if (((SMAEditor) editor.getPart(false)).getSmaMgr().getSma().equals(artifact)) {
               ((SMAEditor) editor.getPart(false)).closeEditor();
            }
         }
      }
   }

   public void closeEditor() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            AWorkbench.getActivePage().closeEditor(editor, false);
         }
      });
   }

   /**
    * @return Returns the smaMgr.
    */
   public SMAManager getSmaMgr() {
      return smaMgr;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getCurrentStateName()
    */
   public String getCurrentStateName() throws OseeCoreException, SQLException {
      return smaMgr.getStateMgr().getCurrentStateName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getEditor()
    */
   public IDirtiableEditor getEditor() throws OseeCoreException, SQLException {
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getParentSmaMgr()
    */
   public SMAManager getParentSmaMgr() throws OseeCoreException, SQLException {
      return smaMgr;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getResOptions()
    */
   public List<TaskResOptionDefinition> getResOptions() throws OseeCoreException {
      return TaskResolutionOptionRule.getTaskResolutionOptions(smaMgr.getWorkPageDefinition());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTabName()
    */
   public String getTabName() throws OseeCoreException, SQLException {
      return "Tasks";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTaskArtifacts(java.lang.String)
    */
   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException, SQLException {
      if (stateName == null || stateName.equals(""))
         return smaMgr.getTaskMgr().getTaskArtifacts();
      else
         return smaMgr.getTaskMgr().getTaskArtifacts(stateName);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isTaskable()
    */
   public boolean isTaskable() throws OseeCoreException, SQLException {
      return smaMgr.isTaskable();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isUsingTaskResolutionOptions()
    */
   public boolean isUsingTaskResolutionOptions() throws OseeCoreException, SQLException {
      if (smaMgr.getWorkPageDefinition() == null) return false;
      return (smaMgr.getWorkPageDefinition().getWorkItemDefinitionsByType(TaskResolutionOptionRule.WORK_TYPE).size() == 1);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isEditable()
    */
   public boolean isTasksEditable() throws OseeCoreException, SQLException {
      return smaMgr.getSma().isTaskable();
   }

   /**
    * @return the priviledgedEditMode
    */
   public PriviledgedEditMode getPriviledgedEditMode() {
      return priviledgedEditMode;
   }

   /**
    * @param priviledgedEditMode the priviledgedEditMode to set s * @throws OseeCoreException
    */
   public void setPriviledgedEditMode(PriviledgedEditMode priviledgedEditMode) throws OseeCoreException {
      this.priviledgedEditMode = priviledgedEditMode;
      smaMgr.getSma().saveSMA();
      workFlowTab.refresh();
   }

   /**
    * @return the isAccessControlWrite
    */
   public boolean isAccessControlWrite() {
      return AccessControlManager.getInstance().checkCurrentUserObjectPermission(smaMgr.getSma(), PermissionEnum.WRITE);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, org.eclipse.osee.framework.skynet.core.artifact.Branch, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      try {
         if (smaMgr.isInTransition()) return;
         if (branchModType == BranchEventType.Added || branchModType == BranchEventType.Deleted || branchModType == BranchEventType.Committed) {
            if (smaMgr.getBranchMgr().getBranchId() == null || smaMgr.getBranchMgr().getBranchId() != branchId) {
               return;
            }
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (getContainer() == null || getContainer().isDisposed()) return;
                  try {
                     refreshPages();
                     onDirtied();
                  } catch (Exception ex) {
                     OSEELog.logException(AtsPlugin.class, ex, false);
                  }
               }
            });
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.TransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) {
      if (smaMgr.isInTransition()) return;
      if (transData.branchId != AtsPlugin.getAtsBranch().getBranchId()) return;
      if (transData.isDeleted(smaMgr.getSma())) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               closeEditor();
            }
         });
      } else if (transData.isHasEvent(smaMgr.getSma())) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  refreshPages();
                  onDirtied();
               } catch (Exception ex) {
                  // do nothing
               }
            }
         });
      } else if (smaMgr.getReviewManager().hasReviews()) {
         try {
            // If related review has made a change, redraw
            for (ReviewSMArtifact reviewArt : smaMgr.getReviewManager().getReviews()) {
               if (transData.isHasEvent(reviewArt)) {
                  Displays.ensureInDisplayThread(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           refreshPages();
                           onDirtied();
                        } catch (Exception ex) {
                           // do nothing
                        }
                     }
                  });
               }
            }
         } catch (Exception ex) {
            // do nothings
         }
      }
      onDirtied();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener#handleArtifactsPurgedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.getLoadedArtifacts().contains(smaMgr.getSma())) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  closeEditor();
               }
            });
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IRelationModifiedEventListener#handleRelationModifiedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.relation.RelationModType, org.eclipse.osee.framework.skynet.core.relation.RelationLink, org.eclipse.osee.framework.skynet.core.artifact.Branch, java.lang.String)
    */
   @Override
   public void handleRelationModifiedEvent(Sender sender, RelationModType relationModType, RelationLink link, Branch branch, String relationType) {
      try {
         if (link.getArtifactA().equals(smaMgr.getSma()) || link.getArtifactB().equals(smaMgr.getSma())) {
            onDirtied();
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   @Override
   public String getActionDescription() {
      return null;
   }

}