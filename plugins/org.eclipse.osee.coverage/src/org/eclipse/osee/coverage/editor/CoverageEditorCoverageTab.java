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

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.coverage.action.IRefreshable;
import org.eclipse.osee.coverage.editor.params.CoverageParameters;
import org.eclipse.osee.coverage.editor.params.CoverageParametersComposite;
import org.eclipse.osee.coverage.editor.params.CoverageParametersTextFilter;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer.TableType;
import org.eclipse.osee.coverage.help.ui.CoverageHelpContext;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.IWorkProductTaskProvider;
import org.eclipse.osee.coverage.model.MessageCoverageItem;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorCoverageTab extends FormPage implements ISaveable, IRefreshable {

   private XCoverageViewer xCoverageViewer;
   private ScrolledForm scrolledForm;
   private final CoveragePackageBase coveragePackageBase;
   private final CoverageEditor coverageEditor;
   private CoverageParameters coverageParameters;
   private CoverageParametersTextFilter parametersFilter;
   public static String PAGE_ID = "coverage.items";

   public CoverageEditorCoverageTab(String name, CoverageEditor coverageEditor, CoveragePackageBase provider) {
      super(coverageEditor, name, name);
      this.coverageEditor = coverageEditor;
      this.coveragePackageBase = provider;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      scrolledForm.setText(coveragePackageBase.getName() + " - " + DateUtil.getMMDDYYHHMM(coveragePackageBase.getDate()) + " - " + coveragePackageBase.getCoverageItems().size() + " Coverage Items");
      scrolledForm.setImage(ImageManager.getImage(CoverageUtil.getCoveragePackageBaseImage(coveragePackageBase)));
      scrolledForm.getBody().setLayout(new GridLayout(2, false));
      Composite mainComp = scrolledForm.getBody();
      coverageEditor.getToolkit().adapt(mainComp);
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      createBranchLabel(coverageEditor, mainComp);

      coverageParameters = new CoverageParameters(coveragePackageBase);
      new CoverageParametersComposite(mainComp, managedForm, coverageEditor, coverageParameters,
         new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               handleSearchButtonPressed();
            }
         });

      Composite tableComp = new Composite(mainComp, SWT.NONE);
      tableComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      coverageEditor.getToolkit().adapt(tableComp);
      GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
      tableData.horizontalSpan = 2;
      tableComp.setLayoutData(tableData);
      coverageEditor.getToolkit().adapt(tableComp);

      IWorkProductTaskProvider provider = null;
      if (coveragePackageBase instanceof CoveragePackage) {
         provider = ((CoveragePackage) coveragePackageBase).getWorkProductTaskProvider();
      }
      xCoverageViewer =
         new XCoverageViewer(this, coveragePackageBase.getCoverageOptionManager(), provider,
            coveragePackageBase instanceof CoverageImport ? TableType.Import : TableType.Package);
      xCoverageViewer.setDisplayLabel(false);
      xCoverageViewer.createWidgets(managedForm, tableComp, 1);
      xCoverageViewer.getXViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      createToolbar();

      HelpUtil.setHelp(xCoverageViewer.getControl(), CoverageHelpContext.EDITOR__COVERAGE_TAB);
      HelpUtil.setHelp(tableComp, CoverageHelpContext.EDITOR__COVERAGE_TAB);

   }

   public static void createBranchLabel(CoverageEditor coverageEditor, Composite mainComp) {
      Composite branchComp = new Composite(mainComp, SWT.NONE);
      coverageEditor.getToolkit().adapt(branchComp);
      GridData gd = new GridData(SWT.NONE, SWT.NONE, false, false);
      gd.horizontalSpan = 2;
      gd.verticalIndent = 0;
      branchComp.setLayoutData(gd);
      GridLayout layout = new GridLayout(3, false);
      layout.marginHeight = 5;
      branchComp.setLayout(layout);

      // branch label
      Label label = new Label(branchComp, SWT.NONE);
      label.setText("Branch: ");
      label.setFont(FontManager.getDefaultLabelFont());
      coverageEditor.getToolkit().adapt(label, false, false);

      // read-only label
      try {
         if (coverageEditor.getCoveragePackageBase().isEditable().isFalse()) {
            label = new Label(branchComp, SWT.NONE);
            label.setText(" -- READ-ONLY -- ");
            if (Strings.isValid(coverageEditor.getCoveragePackageBase().getEditableReason())) {
               label.setToolTipText(coverageEditor.getCoveragePackageBase().getEditableReason());
            }
            label.setFont(FontManager.getDefaultLabelFont());
            coverageEditor.getToolkit().adapt(label, false, false);
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
         }
      } catch (OseeCoreException ex1) {
         OseeLog.log(Activator.class, Level.SEVERE, ex1);
      }

      // show branch name
      try {
         label = new Label(branchComp, SWT.NONE);
         if (coverageEditor.getBranch() != null) {
            label.setText(Strings.truncate(coverageEditor.getBranch().getName(), 200, true));
         }
         coverageEditor.getToolkit().adapt(label, false, false);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void handleSearchButtonPressed() {
      if (parametersFilter == null) {
         parametersFilter = new CoverageParametersTextFilter(xCoverageViewer.getXViewer());
         xCoverageViewer.getXViewer().addFilter(parametersFilter);
      }
      handleSearchButtonPressed(xCoverageViewer, coverageParameters, parametersFilter);
   }

   public static void handleSearchButtonPressed(XCoverageViewer xCoverageViewer, CoverageParameters coverageParameters, CoverageParametersTextFilter parametersFilter) {
      try {
         Result result = coverageParameters.isParameterSelectionValid();
         if (result.isFalse()) {
            AWorkbench.popup(result);
            return;
         }
         Pair<Set<ICoverage>, Set<ICoverage>> itemsAndParents = coverageParameters.performSearchGetResults();
         if (itemsAndParents.getSecond().size() != 0) {
            xCoverageViewer.loadTable(itemsAndParents.getSecond());
         } else {
            xCoverageViewer.getXViewer().setInput(new MessageCoverageItem("No Match"));
         }
         xCoverageViewer.refresh();
         parametersFilter.setShowAll(coverageParameters.isShowAll());
         parametersFilter.setShownCoverages(itemsAndParents.getFirst());
         xCoverageViewer.getXViewer().refresh();

         if (!coverageParameters.isShowAll()) {
            // Don't reveal too low cause it's too much, just reveal to first non folder Coverage Unit
            for (ICoverage coverage : CoverageUtil.getFirstNonFolderCoverageUnits(itemsAndParents.getFirst())) {
               xCoverageViewer.getXViewer().setSelection(new StructuredSelection(coverage));
               xCoverageViewer.getXViewer().reveal(new StructuredSelection(coverage));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void createToolbar() {
      IToolBarManager toolBarManager = scrolledForm.getToolBarManager();
      toolBarManager.add(new CollapseAllAction(xCoverageViewer.getXViewer()));
      toolBarManager.add(new ExpandAllAction(xCoverageViewer.getXViewer()));
      toolBarManager.add(xCoverageViewer.getXViewer().getCustomizeAction());
      scrolledForm.updateToolBar();
   }

   @Override
   public FormEditor getEditor() {
      return super.getEditor();
   }

   @Override
   public Result isEditable() {
      return coveragePackageBase.isEditable();
   }

   @Override
   public Result save(String saveName, CoverageOptionManager coverageOptionManager) throws OseeCoreException {
      return OseeCoveragePackageStore.get((CoveragePackage) coveragePackageBase, coverageEditor.getBranch()).save(
         saveName, coverageOptionManager);
   }

   @Override
   public Result save(Collection<ICoverage> coverages, String saveName) throws OseeCoreException {
      return OseeCoveragePackageStore.get((CoveragePackage) coveragePackageBase, coverageEditor.getBranch()).save(
         coverages, saveName);
   }

   @Override
   public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) {
      return new Result(false, "Not valid for this tab.");
   }

   @Override
   public IOseeBranch getBranch() throws OseeCoreException {
      return coverageEditor.getBranch();
   }

   @Override
   public String getId() {
      return PAGE_ID;
   }

   @Override
   public void refresh(Object element) {
      if (xCoverageViewer != null && xCoverageViewer.getXViewer() != null && !xCoverageViewer.getXViewer().getTree().isDisposed()) {
         xCoverageViewer.getXViewer().refresh(element);
      }
   }

   @Override
   public void update(Object element) {
      xCoverageViewer.getXViewer().update(element);
   }

   @Override
   public void remove(Object element) {
      // do nothing
   }

   public void refresh() {
      xCoverageViewer.getXViewer().refresh();
   }
}
