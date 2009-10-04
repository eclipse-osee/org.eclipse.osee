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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.coverage.editor.xmerge.XCoverageMergeViewer;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.util.widget.XHyperlabelCoverageMethodSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorMergeTab extends FormPage {

   private WorkPage page;
   private XCoverageMergeViewer xCoverageViewer1;
   private final ICoverageTabProvider provider1;
   private XCoverageMergeViewer xCoverageViewer2;
   private final ICoverageTabProvider provider2;
   private ScrolledForm scrolledForm;
   private final CoverageEditor coverageEditor;

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

      Button runButton = new Button(mainComp, SWT.PUSH);
      runButton.setText("Search");
      GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
      runButton.setLayoutData(gridData);
      runButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleSearchButtonPressed();
         }
      });
      runButton.setLayoutData(gridData);
      coverageEditor.getToolkit().adapt(runButton, true, true);

      Composite paramComp = new Composite(mainComp, SWT.NONE);
      paramComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      paramComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      coverageEditor.getToolkit().adapt(paramComp);

      try {
         page = new WorkPage(getWidgetXml(), new DefaultXWidgetOptionResolver());
         page.createBody(getManagedForm(), paramComp, null, null, true);
         widgetsCreated();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      Composite tableComp = new Composite(mainComp, SWT.NONE);
      tableComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      coverageEditor.getToolkit().adapt(tableComp);
      GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
      tableData.horizontalSpan = 2;
      tableComp.setLayoutData(tableData);
      coverageEditor.getToolkit().adapt(tableComp);

      managedForm.getToolkit().createLabel(tableComp, "Coverage Package");
      managedForm.getToolkit().createLabel(tableComp, "Coverage Import");

      xCoverageViewer1 = new XCoverageMergeViewer();
      xCoverageViewer1.setDisplayLabel(false);
      xCoverageViewer1.createWidgets(managedForm, tableComp, 1);
      xCoverageViewer1.getXViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      xCoverageViewer2 = new XCoverageMergeViewer();
      xCoverageViewer2.setDisplayLabel(false);
      xCoverageViewer2.createWidgets(managedForm, tableComp, 1);
      xCoverageViewer2.getXViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      createToolbar();

   }

   public void createToolbar() {
      IToolBarManager toolBarManager = scrolledForm.getToolBarManager();
      toolBarManager.add(new CollapseAllAction(xCoverageViewer1.getXViewer()));
      toolBarManager.add(xCoverageViewer1.getXViewer().getCustomizeAction());
      toolBarManager.add(new Separator());
      toolBarManager.add(new CollapseAllAction(xCoverageViewer2.getXViewer()));
      toolBarManager.add(xCoverageViewer2.getXViewer().getCustomizeAction());
      CoverageEditor.addToToolBar(scrolledForm.getToolBarManager(), coverageEditor);
      scrolledForm.updateToolBar();
   }

   @Override
   public FormEditor getEditor() {
      return super.getEditor();
   }

   private void handleSearchButtonPressed() {
      try {
         Result result = isParameterSelectionValid();
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
      Collection<CoverageMethodEnum> coverageMethods = getSelectedCoverageMethods();
      User assignee = getSelectedUser();
      boolean includeCompleted = isIncludeCompletedCancelledCheckbox();
      for (ICoverageEditorItem item : provider.getCoverageEditorItems()) {
         if (assignee != null && item.getUser().equals(assignee)) {
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

   public String getSelectedName(/*SearchType searchType*/) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      if (getSelectedUser() != null) {
         sb.append(" - Assignee: " + getSelectedUser());
      }
      if (isIncludeCompletedCancelledCheckbox()) {
         sb.append(" - Include Completed/Cancelled");
      }
      if (getSelectedCoverageMethods().size() > 1) {
         sb.append(" - Coverage Method: " + org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ",
               getSelectedCoverageMethods()));
      }
      return "Coverage Items " + sb.toString();
   }

   private boolean isIncludeCompletedCancelledCheckbox() {
      if (getIncludeCompletedCancelledCheckbox() == null) {
         return false;
      }
      return getIncludeCompletedCancelledCheckbox().isSelected();
   }

   public XMembersCombo getAssigeeCombo() {
      return (XMembersCombo) getXWidget("Assignee");
   }

   public XCheckBox getIncludeCompletedCancelledCheckbox() {
      return (XCheckBox) getXWidget("Include Completed/Cancelled");
   }

   public void widgetsCreated() throws OseeCoreException {
      if (getIncludeCompletedCancelledCheckbox() != null) {
         getIncludeCompletedCancelledCheckbox().set(true);
      }
   }

   private User getSelectedUser() {
      if (getAssigeeCombo() == null) {
         return null;
      }
      return getAssigeeCombo().getUser();
   }

   private Collection<CoverageMethodEnum> getSelectedCoverageMethods() {
      if (getCoverageMethodHyperlinkSelection() == null) {
         return Collections.emptyList();
      }
      return getCoverageMethodHyperlinkSelection().getSelectedCoverageMethods();
   }

   public XWidget getXWidget(String attrName) {
      if (page == null) {
         throw new IllegalArgumentException("WorkPage == null");
      }
      if (page.getLayoutData(attrName) == null) {
         return null;
      }
      return page.getLayoutData(attrName).getXWidget();
   }

   public XHyperlabelCoverageMethodSelection getCoverageMethodHyperlinkSelection() {
      return (XHyperlabelCoverageMethodSelection) getXWidget("Coverage Method");
   }

   public Result isParameterSelectionValid() throws OseeCoreException {
      try {
         if (getSelectedCoverageMethods().size() == 0) {
            return new Result("You must select at least one Coverage Method");
         }
         return Result.TrueResult;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result("Exception: " + ex.getLocalizedMessage());
      }
   }

   public String getWidgetXml() {
      StringBuffer sb =
            new StringBuffer(
                  "<xWidgets>" +
                  //
                  "<XWidget xwidgetType=\"XHyperlabelCoverageMethodSelection\" displayName=\"Coverage Method\" horizontalLabel=\"true\"/>");
      if (provider1.isAssignable()) {
         sb.append("" +
         //
         "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" beginComposite=\"4\" horizontalLabel=\"true\"/>" +
         //
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed/Cancelled\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      }
      sb.append("</xWidgets>");
      return sb.toString();
   }
}
