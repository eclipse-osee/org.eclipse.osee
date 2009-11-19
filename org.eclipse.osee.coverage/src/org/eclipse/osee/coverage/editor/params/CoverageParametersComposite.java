/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor.params;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.util.widget.XHyperlabelCoverageMethodSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
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
public class CoverageParametersComposite extends Composite {

   private WorkPage page;
   private final CoverageParameters coverageParameters;

   public CoverageParametersComposite(Composite mainComp, IManagedForm managedForm, CoverageEditor coverageEditor, final CoverageParameters coverageParameters, final SelectionListener selectionListener) {
      super(mainComp, SWT.None);
      this.coverageParameters = coverageParameters;
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
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      getShowAllCheckbox().addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            coverageParameters.setShowAll(isShowAll());
         }
      });
      getAssigeeCombo().addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            coverageParameters.setAssignee(getAssignee());
         }
      });
      getNotesXText().addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            coverageParameters.setNotes(getNotesStr());
         }
      });
      getCoverageMethodHyperlinkSelection().addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            coverageParameters.setCoverageMethods(getCoverageMethodHyperlinkSelection().getSelectedCoverageMethods());
         }
      });
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

   public String getNotesStr() {
      if (getNotesXText() != null) {
         return getNotesXText().get();
      }
      return "";
   }

   public XText getNotesXText() {
      return (XText) getXWidget("Notes");
   }

   public XCheckBox getShowAllCheckbox() {
      return (XCheckBox) getXWidget("Show All");
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

   public String getWidgetXml() {
      StringBuffer sb =
            new StringBuffer(
                  "<xWidgets>" +
                  // 
                  "<XWidget xwidgetType=\"XHyperlabelCoverageMethodSelection\" displayName=\"Coverage Method\" horizontalLabel=\"true\"/>" +
                  //
                  "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Show All\" beginComposite=\"8\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      if (coverageParameters.getCoveragePackageBase().isAssignable()) {
         sb.append("" +
         //
         "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" horizontalLabel=\"true\"/>");
      }
      //
      sb.append("<XWidget xwidgetType=\"XText\" displayName=\"Notes\" horizontalLabel=\"true\"/>");
      sb.append("</xWidgets>");
      return sb.toString();
   }

}
