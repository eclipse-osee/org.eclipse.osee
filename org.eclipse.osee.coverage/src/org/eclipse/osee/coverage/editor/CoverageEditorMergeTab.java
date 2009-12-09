/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.action.LinkWithImportItemAction;
import org.eclipse.osee.coverage.editor.params.CoverageParameters;
import org.eclipse.osee.coverage.editor.params.CoverageParametersComposite;
import org.eclipse.osee.coverage.editor.params.CoverageParametersTextFilter;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer.TableType;
import org.eclipse.osee.coverage.editor.xmerge.CoverageMergeXViewerFactoryImport;
import org.eclipse.osee.coverage.editor.xmerge.CoverageMergeXViewerFactoryPackage;
import org.eclipse.osee.coverage.editor.xmerge.XCoverageMergeViewer;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.merge.MergeImportManager;
import org.eclipse.osee.coverage.merge.IMergeItem;
import org.eclipse.osee.coverage.merge.MergeManager;
import org.eclipse.osee.coverage.merge.MessageMergeItem;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoverageStore;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.coverage.util.NotSaveable;
import org.eclipse.osee.coverage.util.widget.XHyperlabelCoverageMethodSelection;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorMergeTab extends FormPage implements ISaveable {

   private XCoverageMergeViewer xPackageViewer1;
   private final CoveragePackage coveragePackage;
   private XCoverageMergeViewer xImportViewer2;
   private final CoverageImport coverageImport;
   private ScrolledForm scrolledForm;
   private Label titleLabel1, titleLabel2;
   private final CoverageEditor coverageEditor;
   private CoverageParametersComposite coverageParametersComposite;
   private CoverageParameters coverageParameters;
   LinkWithImportItemAction linkWithImportItemAction;
   private MergeManager mergeManager;
   private boolean loading = false;
   private CoverageParametersTextFilter parametersFilter;

   public CoverageEditorMergeTab(String name, CoverageEditor coverageEditor, CoveragePackage provider1, CoverageImport provider2) {
      super(coverageEditor, name, name);
      this.coverageEditor = coverageEditor;
      this.coveragePackage = provider1;
      this.coverageImport = provider2;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      scrolledForm.setText("Merge of " + coverageImport.getName());
      scrolledForm.setImage(ImageManager.getImage(CoverageImage.COVERAGE_PACKAGE));

      scrolledForm.getBody().setLayout(new GridLayout(2, false));
      Composite mainComp = scrolledForm.getBody();
      coverageEditor.getToolkit().adapt(mainComp);
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      coverageParameters = new CoverageParameters(coveragePackage);
      coverageParametersComposite =
            new CoverageParametersComposite(mainComp, managedForm, coverageEditor, coverageParameters,
                  new SelectionAdapter() {
                     @Override
                     public void widgetSelected(SelectionEvent e) {
                        handleSearchButtonPressed();
                     }
                  });

      Composite tableComp = coverageEditor.getToolkit().createComposite(mainComp, SWT.NONE);
      tableComp.setLayout(ALayout.getZeroMarginLayout(3, false));
      GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
      tableData.horizontalSpan = 2;
      tableComp.setLayoutData(tableData);
      coverageEditor.getToolkit().adapt(tableComp);

      SashForm sashForm = new SashForm(tableComp, SWT.NONE);
      sashForm.setLayout(new GridLayout());
      sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      managedForm.getToolkit().adapt(sashForm);

      Composite leftComp = coverageEditor.getToolkit().createComposite(sashForm, SWT.NONE);
      leftComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      leftComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Composite rightComp = coverageEditor.getToolkit().createComposite(sashForm, SWT.NONE);
      rightComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      rightComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createLeftComposite(managedForm, leftComp);
      createRightComposite(managedForm, rightComp);

      createEditorToolbar();
      updateTitles();
   }

   private void handleImportSelected() {
      Collection<IMergeItem> mergeItems = getSelectedMergeItems();
      if (mergeItems.size() == 0) {
         AWorkbench.popup("Select Items to Import via Import Column");
         return;
      }
      try {
         XResultData rd = new MergeImportManager(mergeManager).importItems(this, mergeItems);
         rd.report("Import");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      handleSearchButtonPressed();
      updateTitles();
   }

   private void updateTitles() {
      titleLabel1.setText(coveragePackage.getName());
      titleLabel2.setText(coverageImport.getName());
   }

   private Collection<IMergeItem> getSelectedMergeItems() {
      Collection<IMergeItem> selected = new ArrayList<IMergeItem>();
      for (Object obj : (Collection<?>) xImportViewer2.getXViewer().getInput()) {
         if (obj instanceof IMergeItem && ((IMergeItem) obj).isChecked()) {
            selected.add((IMergeItem) obj);
         }
      }
      return selected;
   }

   public void createLeftComposite(IManagedForm managedForm, Composite leftComp) {
      // Fill LEFT Composite
      titleLabel1 = managedForm.getToolkit().createLabel(leftComp, coveragePackage.getName());

      ToolBar leftToolBar = new ToolBar(leftComp, SWT.FLAT | SWT.RIGHT);
      leftToolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      managedForm.getToolkit().adapt(leftToolBar);

      xPackageViewer1 =
            new XCoverageMergeViewer(null, this, new CoverageMergeXViewerFactoryPackage(), TableType.Package,
                  TableType.Merge);
      xPackageViewer1.setDisplayLabel(false);
      xPackageViewer1.createWidgets(managedForm, leftComp, 1);
      xPackageViewer1.getXViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      linkWithImportItemAction = new LinkWithImportItemAction();
      linkWithImportItemAction.setPackageXViewer(xPackageViewer1, coveragePackage);
      new ActionContributionItem(linkWithImportItemAction).fill(leftToolBar, 0);
      new ActionContributionItem(new RefreshAction(xPackageViewer1)).fill(leftToolBar, 0);
      new ActionContributionItem(xPackageViewer1.getXViewer().getCustomizeAction()).fill(leftToolBar, 0);
      new ActionContributionItem(new CollapseAllAction(xPackageViewer1.getXViewer())).fill(leftToolBar, 0);
      new ActionContributionItem(new ExpandAllAction(xPackageViewer1.getXViewer())).fill(leftToolBar, 0);
   }

   public void createRightComposite(IManagedForm managedForm, Composite rightComp) {
      // Fill RIGHT Composite
      titleLabel2 = managedForm.getToolkit().createLabel(rightComp, coverageImport.getName());

      ToolBar rightToolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      rightToolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      managedForm.getToolkit().adapt(rightToolBar);

      xImportViewer2 =
            new XCoverageMergeViewer(mergeManager, new NotSaveable(), new CoverageMergeXViewerFactoryImport(),
                  TableType.Import, TableType.Merge);
      xImportViewer2.setDisplayLabel(false);
      xImportViewer2.createWidgets(managedForm, rightComp, 1);
      xImportViewer2.getXViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      linkWithImportItemAction.setImportXViewer(xImportViewer2);
      new ActionContributionItem(reloadAction).fill(rightToolBar, 0);
      new ActionContributionItem(new RefreshAction(xPackageViewer1)).fill(rightToolBar, 0);
      new ActionContributionItem(xImportViewer2.getXViewer().getCustomizeAction()).fill(rightToolBar, 0);
      new ActionContributionItem(new CollapseAllAction(xImportViewer2.getXViewer())).fill(rightToolBar, 0);
      new ActionContributionItem(new ExpandAllAction(xImportViewer2.getXViewer())).fill(rightToolBar, 0);
      new ActionContributionItem(importAction).fill(rightToolBar, 0);

      loadImportViewer(false);
   }

   private class ImportJobChangeListener implements IJobChangeListener {

      @Override
      public void aboutToRun(IJobChangeEvent event) {
      }

      @Override
      public void awake(IJobChangeEvent event) {
      }

      @Override
      public void done(IJobChangeEvent event) {
         showBusy(false);
         loading = false;
         coverageEditor.getCoverageEditorOverviewTab().refreshHtml();
      }

      @Override
      public void running(IJobChangeEvent event) {
      }

      @Override
      public void scheduled(IJobChangeEvent event) {
         showBusy(true);
      }

      @Override
      public void sleeping(IJobChangeEvent event) {
      }

   }

   private void loadImportViewer(boolean force) {
      if (loading) {
         AWorkbench.popup("Already Loading");
         return;
      }
      if (force || mergeManager == null) {
         mergeManager = new MergeManager(coveragePackage, coverageImport);
      }
      xImportViewer2.getXViewer().setInput(new MessageMergeItem("Loading..."));
      xImportViewer2.getXViewer().refresh();
      loading = true;
      Operations.executeAsJob(new LoadImportViewerJob(), true, Job.LONG, new ImportJobChangeListener());
   }

   public class LoadImportViewerJob extends AbstractOperation {

      public LoadImportViewerJob() {
         super("Loading Coverage Import Viewer", Activator.PLUGIN_ID);
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         final List<IMergeItem> mergeItems = mergeManager.getMergeItems();
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               xImportViewer2.getXViewer().setInput(mergeItems);
            }
         });
      }
   }

   private final Action reloadAction = new Action() {
      @Override
      public void run() {
         loadImportViewer(true);
      }

      @Override
      public ImageDescriptor getImageDescriptor() {
         return ImageManager.getImageDescriptor(FrameworkImage.LOAD);
      }

      @Override
      public String getToolTipText() {
         return "Re-Load";
      }

   };

   private final Action importAction = new Action() {

      @Override
      public void run() {
         try {
            Artifact artifact = ((CoverageEditorInput) coverageEditor.getEditorInput()).getCoveragePackageArtifact();
            if (artifact != null) {
               CoverageUtil.setBranch(artifact.getBranch());
            } else {
               if (!CoverageUtil.getBranchFromUser(true)) {
                  return;
               }
            }
            handleImportSelected();
            loadImportViewer(true);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

      @Override
      public org.eclipse.jface.resource.ImageDescriptor getImageDescriptor() {
         return ImageManager.getImageDescriptor(FrameworkImage.ARROW_LEFT_YELLOW);
      }

      @Override
      public String getToolTipText() {
         return "Import Selected Items into Coverage Package";
      };
   };

   public void simulateSearchAll() throws OseeArgumentException {
      XHyperlabelCoverageMethodSelection methodSelectionWidget =
            coverageParametersComposite.getCoverageMethodHyperlinkSelection();
      List<CoverageMethodEnum> values = new ArrayList<CoverageMethodEnum>();
      for (CoverageMethodEnum method : CoverageMethodEnum.values()) {
         values.add(method);
      }
      methodSelectionWidget.setSelectedCoverageMethods(values);
      handleSearchButtonPressed();
   }

   public void createEditorToolbar() {
      CoverageEditor.addToToolBar(scrolledForm.getToolBarManager(), coverageEditor);
      scrolledForm.updateToolBar();
   }

   @Override
   public FormEditor getEditor() {
      return super.getEditor();
   }

   private void handleSearchButtonPressed() {
      if (parametersFilter == null) {
         parametersFilter = new CoverageParametersTextFilter(xPackageViewer1.getXViewer());
         xPackageViewer1.getXViewer().addFilter(parametersFilter);
      }
      CoverageEditorCoverageTab.handleSearchButtonPressed(xPackageViewer1, coverageParameters, parametersFilter);
   }

   @Override
   public Result isEditable() {
      return coveragePackage.isEditable();
   }

   @Override
   public Result save() throws OseeCoreException {
      return OseeCoverageStore.get(coveragePackage).save();
   }

}
