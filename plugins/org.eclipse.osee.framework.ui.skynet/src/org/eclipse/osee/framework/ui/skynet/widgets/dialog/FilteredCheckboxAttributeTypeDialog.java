/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.AttributeTypeNameComparator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class FilteredCheckboxAttributeTypeDialog extends FilteredCheckboxTreeDialog<AttributeTypeToken> {

   public FilteredCheckboxAttributeTypeDialog(String title, String message) {
      this(title, message, AttributeTypeManager.getAllTypes(), new ArrayTreeContentProvider(),
         new AttributeTypeLabelProvider());
   }

   public FilteredCheckboxAttributeTypeDialog(String title, String message, Collection<? extends AttributeTypeToken> selectable) {
      this(title, message, selectable, new ArrayTreeContentProvider(), new AttributeTypeLabelProvider());
   }

   public FilteredCheckboxAttributeTypeDialog(String title, String message, Collection<? extends AttributeTypeToken> selectable, ILabelProvider labelProvider) {
      this(title, message, selectable, new ArrayTreeContentProvider(), labelProvider);
   }

   public FilteredCheckboxAttributeTypeDialog(String title, String message, Collection<? extends AttributeTypeToken> selectable, ITreeContentProvider contentProvider, ILabelProvider labelProvider) {
      super(title, message, contentProvider, labelProvider, new AttributeTypeNameComparator());
      this.selectables.addAll(selectables);
   }

   public FilteredCheckboxAttributeTypeDialog(String title, Collection<? extends AttributeTypeToken> selectable) {
      this(title, title, selectable, new AttributeTypeLabelProvider());
   }

   @SuppressWarnings("unchecked")
   @Override
   public Collection<AttributeTypeToken> getChecked() {
      if (super.getTreeViewer() == null) {
         return Collections.emptyList();
      }
      Set<AttributeTypeToken> checked = new HashSet<>();
      for (Object obj : getResult()) {
         checked.add((AttributeTypeToken) obj);
      }
      return checked;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setInput(selectables);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return comp;
   }

   @Override
   protected Result isComplete() {
      return super.isComplete();
   }

   public Collection<? extends AttributeTypeToken> getSelectable() {
      return selectables;
   }

   public void setSelectable(Collection<? extends AttributeTypeToken> selectable) {
      this.selectables.addAll(selectable);
   }
}