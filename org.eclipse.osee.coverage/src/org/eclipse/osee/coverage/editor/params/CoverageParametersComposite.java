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
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.util.widget.XHyperlabelCoverageMethodSelection;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
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

      Composite buttonComp = new Composite(this, SWT.NONE);
      buttonComp.setLayout(new GridLayout(1, false));
      buttonComp.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
      coverageEditor.getToolkit().adapt(buttonComp);

      Button runButton = new Button(buttonComp, SWT.PUSH);
      runButton.setText("Search");
      runButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
      runButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            selectionListener.widgetSelected(e);
         }
      });
      coverageEditor.getToolkit().adapt(runButton, true, true);

      Button clearButton = new Button(buttonComp, SWT.PUSH);
      clearButton.setText("Clear");
      clearButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
      clearButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               getAssigeeCombo().clear();
               getNotesXText().set("");
               getNameXText().set("");
               getNamespaceXText().set("");
               getRationaleXText().set("");
               getCoverageMethodHyperlinkSelection().clear();
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      coverageEditor.getToolkit().adapt(clearButton, true, true);

      Composite paramComp = new Composite(this, SWT.NONE);
      paramComp.setLayout(ALayout.getZeroMarginLayout(1, false));
      paramComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      coverageEditor.getToolkit().adapt(paramComp);

      try {
         page = new WorkPage(getWidgetXml(), new DefaultXWidgetOptionResolver());
         page.createBody(managedForm, paramComp, null, null, true);

         getAssigeeCombo().addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               try {
                  coverageParameters.setAssignee(getAssignee());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
         getNotesXText().addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               try {
                  coverageParameters.setNotes(getNotesStr());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
         getNameXText().addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               try {
                  coverageParameters.setName(getNameXText().get());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
         getNamespaceXText().addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               try {
                  coverageParameters.setNamespace(getNamespaceStr());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
         getRationaleXText().addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               try {
                  coverageParameters.setRationale(getRationaleStr());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
         getCoverageMethodHyperlinkSelection().setCoverageOptionManager(
               coverageEditor.getCoveragePackageBase().getCoverageOptionManager());
         getCoverageMethodHyperlinkSelection().addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               try {
                  coverageParameters.setCoverageMethods(getCoverageMethodHyperlinkSelection().getSelectedCoverageMethods());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public boolean isShowAll() throws OseeArgumentException {
      if (getShowAllCheckbox() == null) {
         return false;
      }
      return getShowAllCheckbox().isSelected();
   }

   public String getNotesStr() throws OseeArgumentException {
      if (getNotesXText() != null) {
         return getNotesXText().get();
      }
      return "";
   }

   public String getNameStr() throws OseeArgumentException {
      if (getNameXText() != null) {
         return getNameXText().get();
      }
      return "";
   }

   public String getNamespaceStr() throws OseeArgumentException {
      if (getNamespaceXText() != null) {
         return getNamespaceXText().get();
      }
      return "";
   }

   public String getRationaleStr() throws OseeArgumentException {
      if (getRationaleXText() != null) {
         return getRationaleXText().get();
      }
      return "";
   }

   public XMembersCombo getAssigeeCombo() throws OseeArgumentException {
      return (XMembersCombo) getXWidget("Coverage Unit Assignee");
   }

   public XText getNotesXText() throws OseeArgumentException {
      return (XText) getXWidget("Coverage Unit Notes");
   }

   public XText getNameXText() throws OseeArgumentException {
      return (XText) getXWidget("Name");
   }

   public XText getNamespaceXText() throws OseeArgumentException {
      return (XText) getXWidget("Namespace");
   }

   public XText getRationaleXText() throws OseeArgumentException {
      return (XText) getXWidget("Rationale");
   }

   public XCheckBox getShowAllCheckbox() throws OseeArgumentException {
      return (XCheckBox) getXWidget("Show All");
   }

   public User getAssignee() throws OseeArgumentException {
      if (getAssigeeCombo() == null) {
         return null;
      }
      return getAssigeeCombo().getUser();
   }

   public Collection<CoverageOption> getSelectedCoverageMethods() throws OseeArgumentException {
      if (getCoverageMethodHyperlinkSelection() == null) {
         return Collections.emptyList();
      }
      return getCoverageMethodHyperlinkSelection().getSelectedCoverageMethods();
   }

   public XWidget getXWidget(String attrName) throws OseeArgumentException {
      if (page == null) {
         throw new IllegalArgumentException("WorkPage == null");
      }
      if (page.getLayoutData(attrName) == null) {
         return null;
      }
      return page.getLayoutData(attrName).getXWidget();
   }

   public XHyperlabelCoverageMethodSelection getCoverageMethodHyperlinkSelection() throws OseeArgumentException {
      return (XHyperlabelCoverageMethodSelection) getXWidget("Coverage Method");
   }

   public String getWidgetXml() {
      StringBuffer sb = new StringBuffer("<xWidgets>");
      sb.append("<XWidget xwidgetType=\"XHyperlabelCoverageMethodSelection\" displayName=\"Coverage Method\" horizontalLabel=\"true\"/>");
      //
      sb.append("<XWidget xwidgetType=\"XText\" beginComposite=\"6\" displayName=\"Name\" horizontalLabel=\"true\"/>");
      //
      sb.append("<XWidget xwidgetType=\"XText\" displayName=\"Namespace\" horizontalLabel=\"true\"/>");
      //
      sb.append("<XWidget xwidgetType=\"XText\" displayName=\"Rationale\" horizontalLabel=\"true\"/>");
      //
      //
      if (coverageParameters.getCoveragePackageBase().isAssignable()) {
         sb.append("<XWidget xwidgetType=\"XMembersCombo\" beginComposite=\"6\" displayName=\"Coverage Unit Assignee\" horizontalLabel=\"true\"/>");
      }
      //
      sb.append("<XWidget xwidgetType=\"XText\" displayName=\"Coverage Unit Notes\" horizontalLabel=\"true\"/>");
      sb.append("</xWidgets>");
      return sb.toString();
   }
}
