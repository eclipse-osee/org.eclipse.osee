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

import org.eclipse.osee.framework.skynet.core.attribute.Attribute;

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxViaAttribute extends XCheckBox {

   private Attribute attribute;

   public XCheckBoxViaAttribute(String displayLabel) {
      this(displayLabel, "");
   }

   public XCheckBoxViaAttribute(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
   }

   public Attribute getAttribute() {
      return attribute;
   }

   public void setAttribute(Attribute attribute) {
      this.attribute = attribute;
      super.set(attribute.getStringData().equals("yes") ? true : false);
   }

   @Override
   public void set(boolean selected) {
      super.set(selected);
      attribute.setStringData(get() ? "yes" : "no");
   }

   @Override
   public void save() {
      if (isDirty()) {
         attribute.setStringData(get() ? "yes" : "no");
      }
   }

   @Override
   public boolean isDirty() {
      return (!attribute.getStringData().equals(get() ? "yes" : "no"));
   }

}
