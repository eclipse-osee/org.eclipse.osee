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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer.TableType;
import org.eclipse.osee.coverage.editor.xmerge.CoverageMergeXViewer;
import org.eclipse.osee.coverage.editor.xmerge.CoverageMergeXViewerFactoryImport;
import org.eclipse.osee.coverage.editor.xmerge.CoverageMergeXViewerFactoryPackage;
import org.eclipse.osee.coverage.editor.xmerge.XCoverageMergeViewer;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.util.CoveragePackageImport;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.coverage.util.NotSaveable;
import org.eclipse.osee.coverage.util.widget.XHyperlabelCoverageMethodSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorMergeTab extends FormPage implements ISaveable {

   private XCoverageMergeViewer xCoverageViewer1;
   private final ICoverageTabProvider provider1;
   private XCoverageMergeViewer xCoverageViewer2;
   private final ICoverageTabProvider provider2;
   private ScrolledForm scrolledForm;
   private Label titleLabel1, titleLabel2;
   private final CoverageEditor coverageEditor;
   private CoverageEditorCoverageParameters parameters;

   public CoverageEditorMergeTab(String name, CoverageEditor coverageEditor, ICoverageTabProvider provider1, ICoverageTabProvider provider2) {
      super(coverageEditor, name, name);
      this.coverageEditor = coverageEditor;
      this.provider1 = provider1;
      this.provider2 = provider2;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      scrolledForm.setText("Merge of " + provider2.getName());
      scrolledForm.setImage(ImageManager.getImage(provider1.getTitleImage()));

      scrolledForm.getBody().setLayout(new GridLayout(2, false));
      Composite mainComp = scrolledForm.getBody();
      coverageEditor.getToolkit().adapt(mainComp);
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      parameters =
            new CoverageEditorCoverageParameters(mainComp, managedForm, coverageEditor, true, new SelectionAdapter() {
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

      Composite leftComp = coverageEditor.getToolkit().createComposite(tableComp, SWT.NONE);
      leftComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      leftComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Composite centerComp = coverageEditor.getToolkit().createComposite(tableComp, SWT.NONE);
      centerComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      centerComp.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, false, true));

      Composite rightComp = coverageEditor.getToolkit().createComposite(tableComp, SWT.NONE);
      rightComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      rightComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createLeftComposite(managedForm, leftComp);
      createCenterComposite(managedForm, centerComp);
      createRightComposite(managedForm, rightComp);

      createEditorToolbar();
      updateTitles();
   }

   public void createCenterComposite(IManagedForm managedForm, Composite centerComp) {
      ToolBar centerToolBar = new ToolBar(centerComp, SWT.FLAT | SWT.RIGHT);
      centerToolBar.setLayoutData(new GridData(GridData.FILL_VERTICAL));
      managedForm.getToolkit().adapt(centerToolBar);

      ToolItem toolItem = new ToolItem(centerToolBar, SWT.NONE);
      toolItem.setImage(ImageManager.getImage(FrameworkImage.ARROW_LEFT_YELLOW));
      toolItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleImportSelected();
         }
      });
   }

   private void handleImportSelected() {
      Collection<ICoverageEditorItem> importItems = getSelectedImportItems();
      if (importItems.size() == 0) {
         AWorkbench.popup("Select Items to Import via Import Column");
         return;
      }
      CoveragePackageImport importer =
            new CoveragePackageImport((CoveragePackage) provider1, (CoverageImport) provider2);
      XResultData rd = importer.importItems(this, importItems);
      try {
         rd.report("Import");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      handleSearchButtonPressed();
      updateTitles();
   }

   private void updateTitles() {
      titleLabel1.setText(provider1.getName());
      titleLabel2.setText(provider2.getName());
   }

   private Collection<ICoverageEditorItem> getSelectedImportItems() {
      return ((CoverageMergeXViewer) xCoverageViewer2.getXViewer()).getSelectedImportItems();
   }

   public void createLeftComposite(IManagedForm managedForm, Composite leftComp) {
      // Fill LEFT Composite
      titleLabel1 = managedForm.getToolkit().createLabel(leftComp, provider1.getName());

      ToolBar leftToolBar = new ToolBar(leftComp, SWT.FLAT | SWT.RIGHT);
      leftToolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      managedForm.getToolkit().adapt(leftToolBar);

      xCoverageViewer1 =
            new XCoverageMergeViewer(this, new CoverageMergeXViewerFactoryPackage(), TableType.Package, TableType.Merge);
      xCoverageViewer1.setDisplayLabel(false);
      xCoverageViewer1.createWidgets(managedForm, leftComp, 1);
      xCoverageViewer1.getXViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      (new ActionContributionItem(xCoverageViewer1.getXViewer().getCustomizeAction())).fill(leftToolBar, 0);
      (new ActionContributionItem(new CollapseAllAction(xCoverageViewer1.getXViewer()))).fill(leftToolBar, 0);
      (new ActionContributionItem(new ExpandAllAction(xCoverageViewer1.getXViewer()))).fill(leftToolBar, 0);
   }

   public void createRightComposite(IManagedForm managedForm, Composite rightComp) {
      // Fill RIGHT Composite
      titleLabel2 = managedForm.getToolkit().createLabel(rightComp, provider2.getName());

      ToolBar rightToolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      rightToolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      managedForm.getToolkit().adapt(rightToolBar);

      xCoverageViewer2 =
            new XCoverageMergeViewer(new NotSaveable(), new CoverageMergeXViewerFactoryImport(), TableType.Import,
                  TableType.Merge);
      xCoverageViewer2.setDisplayLabel(false);
      xCoverageViewer2.createWidgets(managedForm, rightComp, 1);
      xCoverageViewer2.getXViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      (new ActionContributionItem(xCoverageViewer2.getXViewer().getCustomizeAction())).fill(rightToolBar, 0);
      (new ActionContributionItem(new CollapseAllAction(xCoverageViewer2.getXViewer()))).fill(rightToolBar, 0);
      (new ActionContributionItem(new ExpandAllAction(xCoverageViewer2.getXViewer()))).fill(rightToolBar, 0);
   }

   public void simulateSearchAll() {
      XHyperlabelCoverageMethodSelection methodSelectionWidget = parameters.getCoverageMethodHyperlinkSelection();
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
      try {
         Result result = parameters.isParameterSelectionValid();
         if (result.isFalse()) {
            result.popup();
            return;
         }
         xCoverageViewer1.loadTable(performSearchGetResults(provider1));
         xCoverageViewer2.loadTable(performSearchGetResults(provider2));
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private Collection<ICoverageEditorItem> performSearchGetResults(ICoverageTabProvider provider) throws OseeCoreException {
      Set<ICoverageEditorItem> items = new HashSet<ICoverageEditorItem>();
      Collection<CoverageMethodEnum> coverageMethods = parameters.getSelectedCoverageMethods();
      User assignee = parameters.getAssignee();
      boolean includeCompleted = parameters.isIncludeCompletedCancelled();
      for (ICoverageEditorItem item : provider.getCoverageEditorItems()) {
         if (assignee != null && item.getAssignees().equals(assignee)) {
            items.add(item);
         }
         if (item instanceof CoverageItem) {
            CoverageItem coverageItem = (CoverageItem) item;
            if (coverageMethods.contains(coverageItem.getCoverageMethod())) {
               items.add(item);
            }
         }
         if ((!includeCompleted && !item.isCompleted()) || includeCompleted) {
            items.add(item);
         }
      }
      return items;
   }

   @Override
   public Result isEditable() {
      if (!(provider1 instanceof ISaveable)) {
         return new Result("Not Editable");
      }
      return ((ISaveable) provider1).isEditable();
   }

   @Override
   public Result save() {
      if (!(provider1 instanceof ISaveable)) {
         return new Result("Not Saveable");
      }
      return ((ISaveable) provider1).save();
   }

}
