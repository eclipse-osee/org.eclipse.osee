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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;

/**
 * @author Donald G. Dunne
 */
public class XRadioButtonViaAttribute extends XRadioButton {

   private BooleanAttribute attribute;

   public XRadioButtonViaAttribute(String displayLabel) {
      this(displayLabel, "");
   }

   public XRadioButtonViaAttribute(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
   }

   public BooleanAttribute getAttribute() {
      return attribute;
   }

   public void setAttribute(BooleanAttribute attribute) {
      this.attribute = attribute;

      super.setSelected(attribute.getValue());
   }

   @Override
   public void setSelected(boolean selected) {
      super.setSelected(selected);
      attribute.setValue(isSelected());
   }

   @Override
   public void save() {
      if (isDirty()) attribute.setValue(isSelected());
   }

   @Override
   public boolean isDirty() {
      return (!attribute.getValue() == isSelected());
   }

}
