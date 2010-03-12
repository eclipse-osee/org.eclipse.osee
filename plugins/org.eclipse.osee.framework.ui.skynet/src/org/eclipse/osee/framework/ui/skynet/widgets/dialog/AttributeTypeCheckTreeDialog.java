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
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class AttributeTypeCheckTreeDialog extends CheckedTreeSelectionDialog {

   public AttributeTypeCheckTreeDialog(Collection<? extends AttributeType> attributeTypes) {
      this(attributeTypes, new AttributeTypeLabelProvider());
   }

   public AttributeTypeCheckTreeDialog(Collection<? extends AttributeType> attributeTypes, ILabelProvider iLabelProvider) {
      super(Display.getCurrent().getActiveShell(), iLabelProvider, new ArrayTreeContentProvider());
      if (attributeTypes != null) setInput(attributeTypes);
   }

   public AttributeTypeCheckTreeDialog() {
      this(null);
   }

   public Collection<AttributeType> getSelection() {
      ArrayList<AttributeType> arts = new ArrayList<AttributeType>();
      for (Object obj : getResult())
         arts.add((AttributeType) obj);
      return arts;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTreeViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            AttributeType type1 = (AttributeType) e1;
            AttributeType type2 = (AttributeType) e2;
            return getComparator().compare(type1.getName(), type2.getName());
         }
      });
      return c;
   }

   public void setAttributeType(Collection<? extends AttributeType> attributeTypes) {
      setInput(attributeTypes);
   }

}
