/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.column;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Allows the selection of active work packages with the option to toggle on inActive.
 *
 * @param workPackages contains the valid list of active and inactive Work Packages
 * @author Donald G. Dunne
 */
public class WorkPackageFilterTreeDialog extends FilteredTreeDialog {
   private IAtsWorkPackage selection;
   XCheckBox showAll = new XCheckBox("Show All Work Packages");
   private boolean removeFromWorkPackage;
   private boolean showRemoveCheckbox = true;
   private final IWorkPackageProvider workPackageProvider;

   public WorkPackageFilterTreeDialog(String title, String message, IWorkPackageProvider workPackageProvider) {
      super(title, message, new ArrayTreeContentProvider(), new StringLabelProvider());
      this.workPackageProvider = workPackageProvider;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setComparator(new StringNameComparator());
         getTreeViewer().getViewer().addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
               IStructuredSelection sel = (IStructuredSelection) getTreeViewer().getViewer().getSelection();
               if (sel.isEmpty()) {
                  selection = null;
               } else {
                  selection = (IAtsWorkPackage) sel.getFirstElement();
               }
               updateStatusLabel();
            }
         });
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.heightHint = 500;
         getTreeViewer().getViewer().getTree().setLayoutData(gd);
      } catch (Exception ex) {
         OseeLog.log(org.eclipse.osee.ats.ide.internal.Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      Composite comp1 = new Composite(comp.getParent(), SWT.NONE);
      comp1.setLayout(new GridLayout(2, false));
      comp1.setLayoutData(new GridData(GridData.FILL_BOTH));

      showAll.createWidgets(comp1, 2);
      showAll.set(false);
      showAll.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            setInput();
         };
      });

      if (showRemoveCheckbox) {
         createRemoveCheckbox(comp1);
      }

      return comp1;
   }

   private void createRemoveCheckbox(Composite parent) {

      final XCheckBox checkbox = new XCheckBox("Remove from WorkPackage");
      checkbox.setFillHorizontally(true);
      checkbox.set(removeFromWorkPackage);
      checkbox.createWidgets(parent, 2);

      SelectionListener selectionListener = new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            removeFromWorkPackage = checkbox.isSelected();
            updateStatusLabel();
         }
      };
      checkbox.addSelectionListener(selectionListener);
   }

   @Override
   protected Result isComplete() {
      try {
         if (selection == null && !removeFromWorkPackage) {
            return new Result("A Work Package or \"Remove From WorkPackage\" must be selected.");
         }
      } catch (Exception ex) {
         OseeLog.log(org.eclipse.osee.ats.ide.internal.Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Result.TrueResult;
   }

   /**
    * @return the selection
    */
   public IAtsWorkPackage getSelection() {
      return selection;
   }

   public void setInput() {
      try {
         if (showAll != null && showAll.isChecked()) {
            super.setInput(workPackageProvider.getAllWorkPackages());
         } else {
            super.setInput(workPackageProvider.getActiveWorkPackages());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public boolean isRemoveFromWorkPackage() {
      return removeFromWorkPackage;
   }

   public void setShowRemoveCheckbox(boolean showRemoveCheckbox) {
      this.showRemoveCheckbox = showRemoveCheckbox;
   }

   public static interface IWorkPackageProvider {
      public Collection<IAtsWorkPackage> getActiveWorkPackages();

      public Collection<IAtsWorkPackage> getAllWorkPackages();
   }
}
