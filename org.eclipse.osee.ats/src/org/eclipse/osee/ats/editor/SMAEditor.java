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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.navigate.VisitedItems;
import org.eclipse.osee.ats.task.IXTaskViewer;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.world.AtsMetricsComposite;
import org.eclipse.osee.ats.world.IAtsMetricsProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
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
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.AttributesComposite;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.panels.DetailsBrowserComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
public class SMAEditor extends AbstractArtifactEditor implements IDirtiableEditor, IActionable, IAtsMetricsProvider, IArtifactsPurgedEventListener, IRelationModifiedEventListener, IFrameworkTransactionEventListener, IBranchEventListener, IXTaskViewer {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.editor.SMAEditor";
   private SMAManager smaMgr;
   private int workFlowPageIndex, taskPageIndex, metricsPageIndex, historyPageIndex, relationPageIndex,
         attributesPageIndex, detailsPageIndex;
   private SMAWorkFlowTab workFlowTab;
   private SMATaskComposite taskComposite;
   private SMAHistoryComposite historyComposite;
   private RelationsComposite relationsComposite;
   private AttributesComposite attributesComposite;
   private AtsMetricsComposite metricsComposite;
   private static Font defaultLabelFont;
   private boolean priviledgedEditModeEnabled = false;
   private Action printAction;

   public SMAEditor() {
      super();
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
         setContentDescription(priviledgedEditModeEnabled ? " PRIVILEGED EDIT MODE ENABLED" : "");
         setTitleImage(ImageManager.getImage(smaMgr.getSma()));

         // Create WorkFlow tab
         try {
            workFlowTab = new SMAWorkFlowTab(smaMgr);
            workFlowPageIndex = addPage(workFlowTab);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }

         // Create Tasks tab
         if (smaMgr.showTaskTab()) {
            createTaskTab();
         }

         createHistoryTab();
         createRelationsTab();
         createAttributesTab();
         createDetailsTab();
         createMetricsTab();

         setActivePage(workFlowPageIndex);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      enableGlobalPrint();
   }

   public static void createLabelValue(XFormToolkit toolkit, Composite comp, String labelStr, String valueStr) throws OseeCoreException {
      createLabelValue(toolkit, comp, labelStr, valueStr, null);
   }

   public static void createLabelValue(XFormToolkit toolkit, Composite comp, String labelStr, String valueStr, String tooltip) throws OseeCoreException {
      Label label = toolkit.createLabel(comp, labelStr + ": ");
      if (tooltip != null && !tooltip.equals("")) label.setToolTipText(tooltip);
      SMAEditor.setLabelFonts(label, SMAEditor.getBoldLabelFont());
      Text text = new Text(comp, SWT.WRAP | SWT.NO_TRIM);
      toolkit.adapt(text, true, true);
      text.setText(valueStr);
      if (tooltip != null && !tooltip.equals("")) text.setToolTipText(tooltip);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         if (smaMgr.isHistoricalVersion()) {
            AWorkbench.popup(
                  "Historical Error",
                  "You can not change a historical version of " + smaMgr.getSma().getArtifactTypeName() + ":\n\n" + smaMgr.getSma());
         } else if (!smaMgr.isAccessControlWrite()) {
            AWorkbench.popup(
                  "Authentication Error",
                  "You do not have permissions to save " + smaMgr.getSma().getArtifactTypeName() + ":" + smaMgr.getSma());
         } else {
            try {
               SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
               if (getActivePage() == attributesPageIndex) {
                  smaMgr.getSma().persistAttributes(transaction);
               }
               // Save widget data to artifact
               workFlowTab.saveXWidgetToArtifact();
               smaMgr.getSma().saveSMA(transaction);
               transaction.execute();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
            onDirtied();
         }

         OseeNotificationManager.sendNotifications();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public static void setLabelFonts(Control parent, Font font) {
      if (parent instanceof Label) {
         Label label = ((Label) parent);
         label.setFont(font);
      }
      if (parent instanceof Composite) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            setLabelFonts(child, font);
         }
         container.layout();
      }
   }

   public static Font getBoldLabelFont() {
      if (defaultLabelFont == null) {
         Font baseFont = JFaceResources.getDefaultFont();
         FontData[] fontDatas = baseFont.getFontData();
         FontData fontData = fontDatas.length > 0 ? fontDatas[0] : new FontData("arial", 12, SWT.BOLD);
         defaultLabelFont = new Font(baseFont.getDevice(), fontData.getName(), fontData.getHeight(), SWT.BOLD);
      }
      return defaultLabelFont;
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
         taskComposite.disposeTaskComposite();
      }
      if (relationsComposite != null) {
         relationsComposite.dispose();
      }
      if (metricsComposite != null) metricsComposite.disposeComposite();

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
         String rString = smaMgr.getSma().reportIsDirty(true);
         return new Result((rString != null), rString);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result(true, ex.getLocalizedMessage());
      }
   }

   @Override
   public String toString() {
      return "SMAEditor - " + smaMgr.getSma().getHumanReadableId() + " - " + smaMgr.getSma().getArtifactTypeName() + " named \"" + smaMgr.getSma().getDescriptiveName() + "\"";
   }

   @Override
   protected void createPages() {
      super.createPages();
      OseeContributionItem.addTo(this, true);
   }

   private void createHistoryTab() {
      try {
         Composite composite = AtsLib.createCommonPageComposite(getContainer());
         createToolBar(composite);
         historyComposite = new SMAHistoryComposite(smaMgr, composite, SWT.NONE);
         historyPageIndex = addPage(composite);
         setPageText(historyPageIndex, "History");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createTaskTab() {
      try {
         Composite composite = AtsLib.createCommonPageComposite(getContainer());
         ToolBar toolBar = createToolBar(composite);
         taskComposite = new SMATaskComposite(this, composite, SWT.NONE, toolBar);
         taskPageIndex = addPage(composite);
         setPageText(taskPageIndex, "Tasks");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createDetailsTab() {
      try {
         Composite composite = AtsLib.createCommonPageComposite(getContainer());
         createToolBar(composite);
         new DetailsBrowserComposite(smaMgr.getSma(), composite, SWT.NONE, null);
         detailsPageIndex = addPage(composite);
         setPageText(detailsPageIndex, "Details");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createMetricsTab() {
      try {
         Composite composite = AtsLib.createCommonPageComposite(getContainer());
         createToolBar(composite);
         metricsComposite = new AtsMetricsComposite(this, composite, SWT.NONE);
         metricsPageIndex = addPage(composite);
         setPageText(metricsPageIndex, "Metrics");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

   }

   private void createAttributesTab() {
      try {
         if (!AtsPlugin.isAtsAdmin()) return;

         // Create Attributes tab
         Composite composite = AtsLib.createCommonPageComposite(getContainer());
         ToolBar toolBar = createToolBar(composite);

         ToolItem item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(ImageManager.getImage(FrameworkImage.SAVED));
         item.setToolTipText("Save attributes changes only");
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               try {
                  smaMgr.getSma().persistAttributes();
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

         Label label = new Label(composite, SWT.NONE);
         label.setText("  NOTE: Changes made on this page MUST be saved through save icon on this page");
         label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

         attributesComposite = new AttributesComposite(this, composite, SWT.NONE, smaMgr.getSma());
         attributesPageIndex = addPage(composite);
         setPageText(attributesPageIndex, "Attributes");
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void createRelationsTab() {
      try {
         // Create Relations tab
         Composite composite = AtsLib.createCommonPageComposite(getContainer());
         ToolBar toolBar = createToolBar(composite);

         if (AtsPlugin.isAtsAdmin()) {
            final ToolItem showAllRelationsItem = new ToolItem(toolBar, SWT.CHECK);

            showAllRelationsItem.setImage(ImageManager.getImage(FrameworkImage.RELATION));
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
         item.setImage(ImageManager.getImage(FrameworkImage.REFRESH));
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
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

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

   private ToolBar createToolBar(Composite parent) {
      ToolBar toolBar = AtsLib.createCommonToolBar(parent);
      ToolItem item;

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, EDITOR_ID, "ATS Editor");

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(FrameworkImage.EDIT_BLUE));
      item.setToolTipText("Show this artifact in the Resource History");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               HistoryView.open(smaMgr.getSma());
            } catch (OseeArgumentException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(FrameworkImage.AUTHENTICATED));
      item.setToolTipText("Access Control");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            PolicyDialog pd = new PolicyDialog(Display.getCurrent().getActiveShell(), smaMgr.getSma());
            pd.open();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(FrameworkImage.DIRTY));
      item.setToolTipText("Show what attribute or relation making editor dirty.");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            Result result = smaMgr.getEditor().isDirtyResult();
            AWorkbench.popup("Dirty Report", result.isFalse() ? "Not Dirty" : "Dirty -> " + result.getText());
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      Text artifactInfoLabel = new Text(toolBar.getParent(), SWT.END);
      artifactInfoLabel.setEditable(false);
      artifactInfoLabel.setText("Type: \"" + smaMgr.getSma().getArtifactTypeName() + "\"   HRID: " + smaMgr.getSma().getHumanReadableId());
      artifactInfoLabel.setToolTipText("The human readable id and database id for this artifact");

      return toolBar;
   }

   public void refreshPages() throws OseeCoreException {
      if (getContainer() == null || getContainer().isDisposed()) return;
      if (workFlowTab != null) workFlowTab.refresh();
      if (historyComposite != null) historyComposite.refresh();
      if (relationsComposite != null) relationsComposite.refreshArtifact(smaMgr.getSma());
      if (attributesComposite != null) attributesComposite.refreshArtifact(smaMgr.getSma());
      smaMgr.getEditor().onDirtied();
      setPartName(smaMgr.getSma().getEditorTitle());
      setTitleImage(ImageManager.getImage(smaMgr.getSma()));
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

   public static void editArtifact(final StateMachineArtifact sma) {
      editArtifact(sma, false);
   }

   public static void editArtifact(final StateMachineArtifact sma, boolean pend) {
      if (sma.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
                   * @see java.lang.Runnable#run()
                   */
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new SMAEditorInput(sma), EDITOR_ID);
               VisitedItems.addVisited(sma);
            } catch (PartInitException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }, pend);

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

   public static SMAEditor getSmaEditor(StateMachineArtifact artifact) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         IEditorReference editor = editors[j];
         if (editor.getPart(false) instanceof SMAEditor) {
            if (((SMAEditor) editor.getPart(false)).getSmaMgr().getSma().equals(artifact)) {
               return ((SMAEditor) editor.getPart(false));
            }
         }
      }
      return null;
   }

   public void closeEditor() {
      final MultiPageEditorPart editor = this;
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
   public String getCurrentStateName() throws OseeCoreException {
      return smaMgr.getStateMgr().getCurrentStateName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getEditor()
    */
   public IDirtiableEditor getEditor() throws OseeCoreException {
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getParentSmaMgr()
    */
   public SMAManager getParentSmaMgr() throws OseeCoreException {
      return smaMgr;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTabName()
    */
   public String getTabName() throws OseeCoreException {
      return "Tasks";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTaskArtifacts(java.lang.String)
    */
   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException {
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
   public boolean isTaskable() throws OseeCoreException {
      return smaMgr.isTaskable();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isEditable()
    */
   public boolean isTasksEditable() throws OseeCoreException {
      return smaMgr.getSma().isTaskable();
   }

   /**
    * @return the priviledgedEditMode
    */
   public boolean isPriviledgedEditModeEnabled() {
      return priviledgedEditModeEnabled;
   }

   /**
    * @param priviledgedEditMode the priviledgedEditMode to set s * @throws OseeCoreException
    */
   public void setPriviledgedEditMode(boolean enabled) throws OseeCoreException {
      this.priviledgedEditModeEnabled = enabled;
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      smaMgr.getSma().saveSMA(transaction);
      transaction.execute();
      workFlowTab.refresh();
   }

   /**
    * @return the isAccessControlWrite
    * @throws OseeCoreException
    */
   public boolean isAccessControlWrite() throws OseeCoreException {
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
                     OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                  }
               }
            });
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.TransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
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
                  // Only refresh editor for first review that has event
                  break;
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
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IRelationModifiedEventListener#handleRelationModifiedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.relation.RelationModType, org.eclipse.osee.framework.skynet.core.relation.RelationLink, org.eclipse.osee.framework.skynet.core.artifact.Branch, java.lang.String)
    */
   @Override
   public void handleRelationModifiedEvent(Sender sender, RelationModType relationModType, RelationLink link, Branch branch, String relationType) {
      try {
         if (branch.getBranchId() != AtsPlugin.getAtsBranch().getBranchId()) return;
         if (link.getArtifactA().equals(smaMgr.getSma()) || link.getArtifactB().equals(smaMgr.getSma())) {
            onDirtied();
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   @Override
   public String getActionDescription() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getMetricsArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getMetricsArtifacts() throws OseeCoreException {
      return Arrays.asList(smaMgr.getSma());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getMetricsVersionArtifact()
    */
   @Override
   public VersionArtifact getMetricsVersionArtifact() throws OseeCoreException {
      return smaMgr.getSma().getWorldViewTargetedVersion();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#handleRefreshAction()
    */
   @Override
   public void handleRefreshAction() throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isRefreshActionHandled()
    */
   @Override
   public boolean isRefreshActionHandled() throws OseeCoreException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getManDaysPerHour()
    */
   @Override
   public double getManHoursPerDayPreference() throws OseeCoreException {
      return smaMgr.getSma().getManHrsPerDayPreference();
   }

   /**
    * @return the workFlowTab
    */
   public SMAWorkFlowTab getWorkFlowTab() {
      return workFlowTab;
   }

   /**
    * @return the taskComposite
    */
   public SMATaskComposite getTaskComposite() {
      return taskComposite;
   }

   /**
    * @param taskComposite the taskComposite to set
    */
   public void setTaskComposite(SMATaskComposite taskComposite) {
      this.taskComposite = taskComposite;
   }

   public List<XWidget> getXWidgetsFromState(String stateName, Class<?> clazz) {
      return workFlowTab.getXWidgetsFromState(stateName, clazz);
   }
}