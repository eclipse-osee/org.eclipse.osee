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
import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.util.widget.XHyperlabelCoverageMethodSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorCoverageParameters extends Composite {

   private WorkPage page;
   private final boolean isAssignable;

   public CoverageEditorCoverageParameters(Composite mainComp, IManagedForm managedForm, CoverageEditor coverageEditor, boolean isAssignable, final SelectionListener selectionListener) {
      super(mainComp, SWT.None);
      this.isAssignable = isAssignable;
      setLayout(new GridLayout(2, false));
      coverageEditor.getToolkit().adapt(this);
      setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      Button runButton = new Button(this, SWT.PUSH);
      runButton.setText("Search");
      GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
      runButton.setLayoutData(gridData);
      runButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            selectionListener.widgetSelected(e);
         }
      });
      runButton.setLayoutData(gridData);
      coverageEditor.getToolkit().adapt(runButton, true, true);

      Composite paramComp = new Composite(this, SWT.NONE);
      paramComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      paramComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      coverageEditor.getToolkit().adapt(paramComp);

      try {
         page = new WorkPage(getWidgetXml(), new DefaultXWidgetOptionResolver());
         page.createBody(managedForm, paramComp, null, null, true);
         widgetsCreated();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      getShowAllCheckbox().getCheckButton().addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (getIncludeCompletedCancelledCheckbox() != null) {
               if (isIncludeCompletedCancelled()) {
                  getIncludeCompletedCancelledCheckbox().set(false);
               }
            }
         }
      });

   }

   public String getSelectedName(/*SearchType searchType*/) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      if (isShowAll()) {
         sb.append(" - Show All");
      }
      if (getAssignee() != null) {
         sb.append(" - Assignee: " + getAssignee());
      }
      if (isIncludeCompletedCancelled()) {
         sb.append(" - Include Completed/Cancelled");
      }
      if (getSelectedCoverageMethods().size() > 1) {
         sb.append(" - Coverage Method: " + org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ",
               getSelectedCoverageMethods()));
      }
      return "Coverage Items " + sb.toString();
   }

   public boolean isIncludeCompletedCancelled() {
      if (getIncludeCompletedCancelledCheckbox() == null) {
         return false;
      }
      return getIncludeCompletedCancelledCheckbox().isSelected();
   }

   public boolean isShowAll() {
      if (getShowAllCheckbox() == null) {
         return false;
      }
      return getShowAllCheckbox().isSelected();
   }

   public XMembersCombo getAssigeeCombo() {
      return (XMembersCombo) getXWidget("Assignee");
   }

   public XCheckBox getIncludeCompletedCancelledCheckbox() {
      return (XCheckBox) getXWidget("Include Completed/Cancelled");
   }

   public XCheckBox getShowAllCheckbox() {
      return (XCheckBox) getXWidget("Show All");
   }

   public void widgetsCreated() throws OseeCoreException {
      if (getIncludeCompletedCancelledCheckbox() != null) {
         getIncludeCompletedCancelledCheckbox().set(true);
      }
   }

   public User getAssignee() {
      if (getAssigeeCombo() == null) {
         return null;
      }
      return getAssigeeCombo().getUser();
   }

   public Collection<CoverageMethodEnum> getSelectedCoverageMethods() {
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
         if (isShowAll()) {
            if (getSelectedCoverageMethods().size() > 0) {
               return new Result("Can't have Show All and Coverage Methods");
            }
            if (isIncludeCompletedCancelled()) {
               return new Result("Can't have Show All and Include Completed/Cancelled selected");
            }
            if (getAssignee() != null) {
               return new Result("Can't have Show All and Assignee selected");
            }
         }
         if (!isShowAll()) {
            if (getSelectedCoverageMethods().size() == 0) {
               return new Result("You must select at least one Coverage Method");
            }
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
                  "<XWidget xwidgetType=\"XHyperlabelCoverageMethodSelection\" displayName=\"Coverage Method\" horizontalLabel=\"true\"/>" +
                  //
                  "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Show All\" beginComposite=\"6\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      if (isAssignable) {
         sb.append("" +
         //
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed/Cancelled\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
         //
         "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" horizontalLabel=\"true\"/>");

      }
      sb.append("</xWidgets>");
      return sb.toString();
   }

}
