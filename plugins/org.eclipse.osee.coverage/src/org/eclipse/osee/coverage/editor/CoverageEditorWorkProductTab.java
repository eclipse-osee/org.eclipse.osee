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
import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.coverage.action.IRefreshable;
import org.eclipse.osee.coverage.action.OpenMultipleWorkProductsAction;
import org.eclipse.osee.coverage.action.RemoveRelatedWorkProductAction;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.coverage.util.TeamWorkflowLabelProvider;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ISelectedArtifacts;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ArtifactContentProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.widgets.XListViewer;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorWorkProductTab extends FormPage implements ISaveable, ISelectedArtifacts, IRefreshable {

   private ScrolledForm scrolledForm;
   private final CoveragePackageBase coveragePackageBase;
   private final CoverageEditor coverageEditor;
   private XListViewer actionListViewer;

   public CoverageEditorWorkProductTab(String name, CoverageEditor coverageEditor, CoveragePackageBase provider) {
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
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createActionListViewer(mainComp);
      addDoubleClickSupport();
      addDropSupportToListViewer();

      createToolbar();
      refresh();
   }

   private void addDoubleClickSupport() {
      actionListViewer.getTable().addListener(SWT.MouseDoubleClick, new Listener() {

         @Override
         public void handleEvent(Event event) {
            IOseeCmService cmService = SkynetGuiPlugin.getInstance().getOseeCmService();
            cmService.openArtifact(getSelectedArtifactItems().iterator().next(), OseeCmEditor.CmPcrEditor);
         }
      });
   }

   public ArrayList<Artifact> getSelectedArtifactItems() {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      TableItem items[] = actionListViewer.getTable().getSelection();
      if (items.length > 0) {
         for (TableItem item : items) {
            arts.add((Artifact) item.getData());
         }
      }
      return arts;
   }

   private void addDropSupportToListViewer() {
      new SkynetDragAndDrop(null, actionListViewer.getTable(), ArtifactEditor.EDITOR_ID) {
         @Override
         public void performArtifactDrop(Artifact[] dropArtifacts) {
            super.performArtifactDrop(dropArtifacts);
            System.out.println("Dropped " + dropArtifacts);
            for (Artifact artifact : dropArtifacts) {
               if (!artifact.isOfType(CoverageArtifactTypes.TeamWorkflow)) {
                  AWorkbench.popup("Related artifact must be a Team Workflow");
                  return;
               }
            }
            try {
               if (!(coveragePackageBase instanceof CoveragePackage)) {
                  AWorkbench.popup("Must be coverage package");
                  return;
               }
               if (coverageEditor.getBranch() == null) {
                  AWorkbench.popup("Coverage Package must have imports before work package applied");
                  return;
               }
               CoveragePackage coveragePackage = (CoveragePackage) coveragePackageBase;
               OseeCoveragePackageStore store =
                  OseeCoveragePackageStore.get(coveragePackage, coverageEditor.getBranch());
               for (Artifact artifact : dropArtifacts) {
                  store.getArtifact(false).addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, artifact);
               }
               store.getArtifact(false).persist("Relate Coverage work product Actions");

               refresh();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

         @Override
         public Artifact[] getArtifacts() throws Exception {
            return new Artifact[] {};
         }
      };
   }

   public Collection<Artifact> getWorkProductArtifacts() {
      try {
         CoveragePackage coveragePackage = (CoveragePackage) coveragePackageBase;
         OseeCoveragePackageStore store = OseeCoveragePackageStore.get(coveragePackage, coverageEditor.getBranch());
         Artifact art = store.getArtifact(false);
         if (art != null) {
            return art.getRelatedArtifacts(CoreRelationTypes.SupportingInfo_SupportingInfo);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return Collections.emptyList();
   }

   private void refresh() {
      actionListViewer.setInput(getWorkProductArtifacts());
   }

   private XListViewer createActionListViewer(Composite parent) {
      actionListViewer = new XListViewer("Drag in Actions related to changing work products");
      actionListViewer.setContentProvider(new ArtifactContentProvider());
      actionListViewer.setLabelProvider(new TeamWorkflowLabelProvider());
      actionListViewer.createWidgets(parent, 2);
      coverageEditor.getToolkit().adapt(actionListViewer.getTable());
      coverageEditor.getToolkit().adapt(actionListViewer.getLabelWidget(), true, true);
      GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
      tableData.horizontalSpan = 2;
      actionListViewer.getTable().setLayoutData(tableData);
      return actionListViewer;
   }

   public void createToolbar() {
      IToolBarManager toolBarManager = scrolledForm.getToolBarManager();
      toolBarManager.add(new OpenMultipleWorkProductsAction(coverageEditor, this));
      toolBarManager.add(new RemoveRelatedWorkProductAction(coverageEditor, this, this));
      CoverageEditor.addToToolBar(scrolledForm.getToolBarManager(), coverageEditor);
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
   public Result save() throws OseeCoreException {
      return OseeCoveragePackageStore.get((CoveragePackage) coveragePackageBase, coverageEditor.getBranch()).save();
   }

   @Override
   public Result save(Collection<ICoverage> coverages) throws OseeCoreException {
      return OseeCoveragePackageStore.get((CoveragePackage) coveragePackageBase, coverageEditor.getBranch()).save(
         coverages);
   }

   @Override
   public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) {
      return new Result(false, "Not valid for this tab.");
   }

   @Override
   public Branch getBranch() throws OseeCoreException {
      return coverageEditor.getBranch();
   }

   @Override
   public Collection<Artifact> getSelectedArtifacts() {
      return getSelectedArtifactItems();
   }

   @Override
   public void refresh(Object element) {
      refresh();
   }

   @Override
   public void update(Object element) {
      // do nothing
   }

   @Override
   public void remove(Object element) {
      // do nothing
   }

   public XListViewer getActionListViewer() {
      return actionListViewer;
   }

}
