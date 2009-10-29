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

import java.util.logging.Level;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer;
import org.eclipse.osee.coverage.editor.xcover.XCoverageViewer.TableType;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.action.CollapseAllAction;
import org.eclipse.osee.framework.ui.skynet.action.ExpandAllAction;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorCoverageTab extends FormPage implements ISaveable {

   private XCoverageViewer xCoverageViewer;
   private ScrolledForm scrolledForm;
   private final CoveragePackageBase coveragePackageBase;
   private final CoverageEditor coverageEditor;
   private CoverageEditorCoverageParameters parameters;

   public CoverageEditorCoverageTab(String name, CoverageEditor coverageEditor, CoveragePackageBase provider) {
      super(coverageEditor, name, name);
      this.coverageEditor = coverageEditor;
      this.coveragePackageBase = provider;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      scrolledForm.setText(coveragePackageBase.getName() + " - " + XDate.getDateStr(coveragePackageBase.getDate(),
            XDate.MMDDYYHHMM) + " - " + coveragePackageBase.getCoverageItems().size() + " Coverage Items");
      scrolledForm.setImage(ImageManager.getImage(CoverageUtil.getCoveragePackageBaseImage(coveragePackageBase)));
      scrolledForm.getBody().setLayout(new GridLayout(2, false));
      Composite mainComp = scrolledForm.getBody();
      coverageEditor.getToolkit().adapt(mainComp);
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      parameters =
            new CoverageEditorCoverageParameters(mainComp, managedForm, coverageEditor, coveragePackageBase,
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

      xCoverageViewer =
            new XCoverageViewer(this,
                  coveragePackageBase instanceof CoverageImport ? TableType.Import : TableType.Package);
      xCoverageViewer.setDisplayLabel(false);
      xCoverageViewer.createWidgets(managedForm, tableComp, 1);
      xCoverageViewer.getXViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      createToolbar();

   }

   private void handleSearchButtonPressed() {
      try {
         Result result = parameters.isParameterSelectionValid();
         if (result.isFalse()) {
            result.popup();
            return;
         }
         xCoverageViewer.loadTable(parameters.performSearchGetResults(coveragePackageBase));
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public void createToolbar() {
      IToolBarManager toolBarManager = scrolledForm.getToolBarManager();
      toolBarManager.add(new CollapseAllAction(xCoverageViewer.getXViewer()));
      toolBarManager.add(new ExpandAllAction(xCoverageViewer.getXViewer()));
      toolBarManager.add(xCoverageViewer.getXViewer().getCustomizeAction());
      CoverageEditor.addToToolBar(scrolledForm.getToolBarManager(), coverageEditor);
      scrolledForm.updateToolBar();
   }

   @Override
   public FormEditor getEditor() {
      return super.getEditor();
   }

   @Override
   public Result isEditable() {
      if (!(coveragePackageBase instanceof ISaveable)) {
         return new Result("Not Editable");
      }
      return ((ISaveable) coveragePackageBase).isEditable();
   }

   @Override
   public Result save() throws OseeCoreException {
      if (!(coveragePackageBase instanceof ISaveable)) {
         return new Result("Not Saveable");
      }
      return ((ISaveable) coveragePackageBase).save();
   }

}
