/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.change.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiData;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeXViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.ChangeXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.XChangeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xchange.XChangeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class ChangeReportTable implements EditorSection.IWidget, IOseeTreeReportProvider {

   private ChangeXViewer xChangeViewer;
   private final ChangeUiData changeData;

   public ChangeReportTable(ChangeUiData changeData) {
      this.changeData = changeData;
   }

   public ChangeXViewer getXViewer() {
      return xChangeViewer;
   }

   @Override
   public void onCreate(IManagedForm managedForm, Composite parent) {

      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {

         FormToolkit toolkit = managedForm.getToolkit();
         ScrolledForm form = managedForm.getForm();
         form.getBody().setLayout(new GridLayout());
         form.getBody().setBackground(parent.getBackground());

         Composite composite = toolkit.createComposite(parent, SWT.BORDER);

         GridLayout layout = new GridLayout();
         layout.marginBottom = 5;
         layout.marginHeight = 0;
         layout.marginWidth = 0;
         composite.setLayout(layout);
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 300;
         composite.setLayoutData(gd);
         toolkit.paintBordersFor(composite);

         int viewerStyle = SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION;
         xChangeViewer = new ChangeXViewer(composite, viewerStyle, new ChangeXViewerFactory(this));
         xChangeViewer.setContentProvider(new XChangeContentProvider());
         xChangeViewer.setLabelProvider(new XChangeLabelProvider(xChangeViewer));

         Tree tree = xChangeViewer.getTree();
         GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
         gridData.heightHint = 100;
         tree.setLayout(ALayout.getZeroMarginLayout());
         tree.setLayoutData(gridData);
         tree.setHeaderVisible(true);
         tree.setLinesVisible(true);

         adaptAll(toolkit, composite);

         new ChangeDragAndDrop(tree, ChangeXViewerFactory.NAMESPACE);
         onUpdate();
      }
   }

   private void adaptAll(FormToolkit toolkit, Composite composite) {
      toolkit.adapt(composite);
      for (Control control : composite.getChildren()) {
         if (Widgets.isAccessible(control)) {
            toolkit.adapt(control, false, false);
            if (control instanceof Composite) {
               adaptAll(toolkit, (Composite) control);
            }
         }
      }
   }

   @Override
   public void onLoading() {
      xChangeViewer.setInput(Arrays.asList("Loading..."));
   }

   @Override
   public void onUpdate() {
      Collection<?> input;
      if (changeData.isLoaded()) {
         if (changeData.getChanges().isEmpty()) {
            input = Arrays.asList("No changes were found");
         } else {
            input = changeData.getChanges();
         }
      } else {
         input = Arrays.asList("Not Loaded");
      }
      xChangeViewer.refreshColumnsWithPreCompute(input);
   }

   private final class ChangeDragAndDrop extends SkynetDragAndDrop {

      public ChangeDragAndDrop(Tree tree, String viewId) {
         super(tree, viewId);
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         event.detail = DND.DROP_NONE;
      }

      @Override
      public Artifact[] getArtifacts() {
         IStructuredSelection selection = (IStructuredSelection) xChangeViewer.getSelection();
         ArrayList<Artifact> artifacts = new ArrayList<>();

         if (selection != null && !selection.isEmpty()) {
            for (Object object : selection.toArray()) {

               if (object instanceof IAdaptable) {
                  Artifact artifact = ((IAdaptable) object).getAdapter(Artifact.class);

                  if (artifact != null) {
                     artifacts.add(artifact);
                  }
               }
            }
         }
         return artifacts.toArray(new Artifact[artifacts.size()]);
      }
   }

   @Override
   public String getEditorTitle() {
      try {
         if (changeData.getAssociatedArtifact() != null) {
            return String.format("Table Report - Change Report - %s", changeData.getAssociatedArtifact());
         } else if (!changeData.getChanges().isEmpty()) {
            BranchId branchId = changeData.getChanges().iterator().next().getBranch();
            if (branchId != null) {
               IOseeBranch branch = BranchManager.getBranch(branchId);
               return String.format("Table Report - Change Report - %s", branch.getName());
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
      return "Table Report - Change Report";
   }

   @Override
   public String getReportTitle() {
      return getEditorTitle();
   }

}
