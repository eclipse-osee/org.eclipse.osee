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
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer;
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
public class CoverageEditorCoverageTab extends FormPage {

   private WorkPage page;
   private XCoverageViewer xCoverageViewer;
   private ScrolledForm scrolledForm;
   private final ICoverageTabProvider provider;
   private final CoverageEditor coverageEditor;

   public CoverageEditorCoverageTab(String name, CoverageEditor coverageEditor, ICoverageTabProvider provider) {
      super(coverageEditor, name, name);
      this.coverageEditor = coverageEditor;
      this.provider = provider;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      scrolledForm.setText(provider.getName());
      scrolledForm.setImage(ImageManager.getImage(provider.getTitleImage()));

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
         page = new WorkPage(WIDGET_XML, new DefaultXWidgetOptionResolver());
         page.createBody(getManagedForm(), paramComp, null, null, true);
         widgetsCreated();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      Composite tableComp = new Composite(mainComp, SWT.NONE);
      tableComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      coverageEditor.getToolkit().adapt(tableComp);
      GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
      tableData.horizontalSpan = 2;
      tableComp.setLayoutData(tableData);
      coverageEditor.getToolkit().adapt(tableComp);

      xCoverageViewer = new XCoverageViewer();
      xCoverageViewer.setDisplayLabel(false);
      xCoverageViewer.createWidgets(managedForm, tableComp, 1);
      xCoverageViewer.getXViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      createToolbar();

   }

   public void createToolbar() {
      IToolBarManager toolBarManager = scrolledForm.getToolBarManager();
      toolBarManager.add(new CollapseAllAction(xCoverageViewer.getXViewer()));
      toolBarManager.add(xCoverageViewer.getXViewer().getCustomizeAction());
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
         xCoverageViewer.loadTable(performSearchGetResults());
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private Collection<ICoverageEditorItem> performSearchGetResults() throws OseeCoreException {
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
      return "Promotion Items " + sb.toString();
   }

   private boolean isIncludeCompletedCancelledCheckbox() {
      return getIncludeCompletedCancelledCheckbox().isSelected();
   }

   public XMembersCombo getAssigeeCombo() {
      return (XMembersCombo) getXWidget("Assignee");
   }

   public XCheckBox getIncludeCompletedCancelledCheckbox() {
      return (XCheckBox) getXWidget("Include Completed/Cancelled");
   }

   public void widgetsCreated() throws OseeCoreException {
      getIncludeCompletedCancelledCheckbox().set(true);

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

   public static String WIDGET_XML =
         "<xWidgets>" +
         //
         "<XWidget xwidgetType=\"XHyperlabelCoverageMethodSelection\" displayName=\"Coverage Method\" horizontalLabel=\"true\"/>" +
         //
         "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" beginComposite=\"4\" horizontalLabel=\"true\"/>" +
         //
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed/Cancelled\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
         //
         "</xWidgets>";

}
