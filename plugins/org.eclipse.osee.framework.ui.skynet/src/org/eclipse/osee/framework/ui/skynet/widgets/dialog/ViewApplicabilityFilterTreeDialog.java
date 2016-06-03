/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.StringNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.orcs.rest.model.ApplicabilityId;
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
 * @param allValidViewApplicabilities contains the valid list of active and inactive View Applicabilities
 * @author Donald G. Dunne
 */
public class ViewApplicabilityFilterTreeDialog extends FilteredTreeDialog {
   private ApplicabilityId selection;
   XCheckBox showAll = new XCheckBox("Show All View Applicabilities");
   private final Collection<ApplicabilityId> allValidApplicabilities;
   private boolean removeViewApplicability;

   public ViewApplicabilityFilterTreeDialog(String title, String message, Collection<ApplicabilityId> allValidViewApplicabilities) {
      super(title, message, new ArrayTreeContentProvider(), new StringLabelProvider());
      this.allValidApplicabilities = allValidViewApplicabilities;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setSorter(new StringNameSorter());
         getTreeViewer().getViewer().addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
               IStructuredSelection sel = (IStructuredSelection) getTreeViewer().getViewer().getSelection();
               if (sel.isEmpty()) {
                  selection = null;
               } else {
                  selection = (ApplicabilityId) sel.getFirstElement();
               }
               updateStatusLabel();
            }
         });
         GridData gd = new GridData(GridData.FILL_BOTH);
         gd.heightHint = 500;
         getTreeViewer().getViewer().getTree().setLayoutData(gd);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
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

      createRemoveCheckbox(comp1);

      return comp1;
   }

   private void createRemoveCheckbox(Composite parent) {

      final XCheckBox checkbox = new XCheckBox("Remove View Applicability");
      checkbox.setFillHorizontally(true);
      checkbox.set(removeViewApplicability);
      checkbox.createWidgets(parent, 2);

      SelectionListener selectionListener = new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            removeViewApplicability = checkbox.isSelected();
            updateStatusLabel();
         }
      };
      checkbox.addSelectionListener(selectionListener);
   }

   @Override
   protected Result isComplete() {
      try {
         if (selection == null && !removeViewApplicability) {
            return new Result("A View Applicability or \"Remove View Applicability\" must be selected.");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Result.TrueResult;
   }

   public ApplicabilityId getSelection() {
      return selection;
   }

   public void setInput() {
      try {
         super.setInput(filterInput(allValidApplicabilities));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private Collection<? extends ApplicabilityId> filterInput(Collection<? extends ApplicabilityId> input2) throws OseeCoreException {
      List<ApplicabilityId> filtered = new ArrayList<>();
      boolean all = false;
      if (showAll != null && showAll.isChecked()) {
         all = true;
      }
      for (ApplicabilityId appl : input2) {
         if (all || isActive(appl)) {
            filtered.add(appl);
         }
      }
      return filtered;
   }

   /**
    * @return true until ApplicabilityId provides active flag (TBD)
    */
   private boolean isActive(ApplicabilityId appl) {
      return true;
   }

   public boolean isRemoveViewApplicability() {
      return removeViewApplicability;
   }
}
