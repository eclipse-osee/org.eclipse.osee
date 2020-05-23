/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.branch.BranchLabelProvider;
import org.eclipse.osee.framework.ui.skynet.branch.BranchNameSorter;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class FilteredTreeBranchDialog extends FilteredTreeDialog {

   private Collection<? extends BranchId> selectable;

   public FilteredTreeBranchDialog(String title, String message, Collection<? extends BranchId> selectable, ILabelProvider labelProvider) {
      this(title, message, selectable, new ArrayTreeContentProvider(), labelProvider);
   }

   public FilteredTreeBranchDialog(String title, String message, Collection<? extends BranchId> selectable, ITreeContentProvider contentProvider, ILabelProvider labelProvider) {
      super(title, message, contentProvider, labelProvider, new BranchNameSorter());
      this.selectable = selectable;
   }

   public FilteredTreeBranchDialog(String title, Collection<? extends BranchId> selectable) {
      this(title, title, selectable, new BranchLabelProvider());
   }

   public FilteredTreeBranchDialog(String title, String message, Collection<? extends BranchId> selectable) {
      this(title, message, selectable, new BranchLabelProvider());
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setInput(selectable);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return comp;
   }

   @Override
   protected Result isComplete() {
      return super.isComplete();
   }

   public void setSelectable(Collection<BranchId> selectable) {
      this.selectable = selectable;
   }

}
