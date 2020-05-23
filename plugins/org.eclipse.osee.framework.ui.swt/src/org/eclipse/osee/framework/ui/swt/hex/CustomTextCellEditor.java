/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.swt.hex;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Ken J. Aguilar
 */
public class CustomTextCellEditor extends TextCellEditor {

   public CustomTextCellEditor() {
      super();
   }

   public CustomTextCellEditor(Composite parent, int style) {
      super(parent, style);
   }

   public CustomTextCellEditor(Composite parent) {
      super(parent);
   }

   @Override
   public LayoutData getLayoutData() {
      LayoutData data = super.getLayoutData();
      data.minimumWidth = 20;
      return data;
   }

}
