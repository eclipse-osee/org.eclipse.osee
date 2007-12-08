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

public class XTextAttribute extends XText {

   private Attribute attribute;

   public XTextAttribute(String displayLabel) {
      super(displayLabel);
   }

   public Attribute getAttribute() {
      return attribute;
   }

   public void setAttribute(Attribute attribute) {
      this.attribute = attribute;
      super.set(attribute.getStringData());
   }

   @Override
   public void set(String text) {
      super.set(text);
   }

   @Override
   public void save() {
      if (isDirty()) {
         attribute.setStringData(text);
      }
   }

   @Override
   public boolean isDirty() {
      if (attribute == null || attribute.getStringData() == null) return false;
      return (!attribute.getStringData().equals(get()));
   }
}
