/*
 * Created on Feb 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.coverage.editor.xcover.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
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
   private final CoverageEditor coverageEditor;

   public CoverageEditorCoverageTab(CoverageEditor coverageEditor, String id, String title) {
      super(coverageEditor, id, title);
      this.coverageEditor = coverageEditor;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      final ScrolledForm form = managedForm.getForm();

      form.getBody().setLayout(new GridLayout(2, false));
      CoverageEditor.addToToolBar(form.getToolBarManager(), coverageEditor);
      Composite mainComp = form.getBody();
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

      Composite paramComp = new Composite(mainComp, SWT.NONE);
      paramComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      paramComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      try {
         page = new WorkPage(WIDGET_XML, ATSXWidgetOptionResolver.getInstance());
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

      xCoverageViewer = new XCoverageViewer(coverageEditor);
      xCoverageViewer.createWidgets(managedForm, tableComp, 1);
      xCoverageViewer.getXViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
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
      for (ICoverageEditorItem item : coverageEditor.getCoverageEditorProvider().getCoverageEditorItems()) {
         if (coverageItemMatchesSearchCriteria(item)) {
            items.add(item);
         }
      }
      return items;
   }

   private boolean coverageItemMatchesSearchCriteria(ICoverageEditorItem item) throws OseeCoreException {
      if (getSelectedUser() != null) {
         if (!getSelectedUser().equals(item.getUser())) return false;
      }

      return true;
   }

   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      if (getSelectedUser() != null) {
         sb.append(" - Assignee: " + getSelectedUser());
      }
      if (isIncludeCompletedCancelledCheckbox()) {
         sb.append(" - Include Completed/Cancelled");
      }
      if (getSelectedCoverageMethod() != null) {
         sb.append(" - Coverage Method: " + getSelectedCoverageMethod());
      }
      return "Promotion Items " + sb.toString();
   }

   private boolean isIncludeCompletedCancelledCheckbox() {
      return getIncludeCompletedCancelledCheckbox().isSelected();
   }

   public XMembersCombo getAssigeeCombo() {
      return ((XMembersCombo) getXWidget("Assignee"));
   }

   public XCheckBox getIncludeCompletedCancelledCheckbox() {
      return ((XCheckBox) getXWidget("Include Completed/Cancelled"));
   }

   public void widgetsCreated() throws OseeCoreException {
      getIncludeCompletedCancelledCheckbox().set(true);

      final XCombo coverageMethodCombo = getCoverageMethodCombo();
      coverageMethodCombo.getComboBox().setVisibleItemCount(25);

   }

   private User getSelectedUser() {
      if (getAssigeeCombo() == null) return null;
      return getAssigeeCombo().getUser();
   }

   private CoverageMethodEnum getSelectedCoverageMethod() {
      if (getCoverageMethodCombo() == null) return null;
      if (!Strings.isValid(getCoverageMethodCombo().get())) {
         return null;
      }
      return CoverageMethodEnum.valueOf(getCoverageMethodCombo().get());
   }

   public XWidget getXWidget(String attrName) {
      if (page == null) throw new IllegalArgumentException("WorkPage == null");
      return page.getLayoutData(attrName).getXWidget();
   }

   public XCombo getCoverageMethodCombo() {
      return ((XCombo) getXWidget("Coverage Method"));
   }

   public Result isParameterSelectionValid() throws OseeCoreException {
      try {
         boolean selected = false;
         User user = getSelectedUser();
         if (user != null) {
            selected = true;
         }
         if (!selected) {
            return new Result("You must select at least Team, Version or Assignee.");
         }
         return Result.TrueResult;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result("Exception: " + ex.getLocalizedMessage());
      }
   }

   public static String WIDGET_XML =
         "<xWidgets>" +
         //
         "<XWidget xwidgetType=\"XCombo(" + Collections.toString(",", (Object[]) CoverageMethodEnum.values()) + ")\" beginComposite=\"6\" displayName=\"Coverage Method\" horizontalLabel=\"true\"/>" +
         //
         "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" horizontalLabel=\"true\"/>" +
         //
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed/Cancelled\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
         //
         "</xWidgets>";

}
