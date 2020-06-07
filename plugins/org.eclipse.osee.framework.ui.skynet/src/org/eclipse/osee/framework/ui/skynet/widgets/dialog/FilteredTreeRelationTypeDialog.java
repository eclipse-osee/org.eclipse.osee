/*********************************************************************
 * Copyright (c) 2016 Boeing
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
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class FilteredTreeRelationTypeDialog extends FilteredTreeDialog {

   private Collection<? extends RelationTypeToken> selectable;

   public FilteredTreeRelationTypeDialog(String title, String message) {
      this(title, message, ServiceUtil.getTokenService().getRelationTypes(), new StringLabelProvider());
   }

   public FilteredTreeRelationTypeDialog(String title, String message, Collection<? extends RelationTypeToken> selectable, ILabelProvider labelProvider) {
      this(title, message, selectable, new ArrayTreeContentProvider(), labelProvider);
   }

   public FilteredTreeRelationTypeDialog(String title, String message, Collection<? extends RelationTypeToken> selectable, ITreeContentProvider contentProvider, ILabelProvider labelProvider) {
      this(title, message, selectable, contentProvider, labelProvider, new StringNameComparator());
   }

   public FilteredTreeRelationTypeDialog(String title, String message, Collection<? extends RelationTypeToken> selectable, ITreeContentProvider contentProvider, ILabelProvider labelProvider, ViewerComparator sorter) {
      super(title, message, contentProvider, labelProvider, sorter);
      this.selectable = selectable;
   }

   public FilteredTreeRelationTypeDialog(String title, Collection<? extends RelationTypeToken> selectable) {
      this(title, title, selectable, new StringLabelProvider());
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
      try {
         if (getSelectedFirst() == null) {
            return new Result("Must select Relation type.");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Result.TrueResult;
   }

   public void setSelectable(Collection<? extends RelationTypeToken> selectable) {
      this.selectable = selectable;
   }

   @Override
   public void setComparator(ViewerComparator comparator) {
      getTreeViewer().getViewer().setComparator(comparator);
   }

}
