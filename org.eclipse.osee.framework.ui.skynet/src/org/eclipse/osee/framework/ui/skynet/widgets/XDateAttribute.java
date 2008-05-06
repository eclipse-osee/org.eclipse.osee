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

import java.util.Date;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;

public class XDateAttribute extends XDate {

   private DateAttribute attribute;

   public XDateAttribute() {
      super();
   }

   public XDateAttribute(String displayLabel) {
      super(displayLabel);
   }

   public DateAttribute getAttribute() {
      return attribute;
   }

   public void setAttribute(DateAttribute attribute) {
      this.attribute = attribute;
      super.setDate(attribute.getValue());
   }

   @Override
   public void setDate(Date date) {
      super.setDate(date);
      attribute.setValue(date);
   }

   @Override
   public void setDateToNow() {
      super.setDateToNow();
      attribute.setValue(getDate());
   }

   @Override
   public void save() {
      if (isDirty()) {
         attribute.setValue(getDate());
      }
   }

   @Override
   public boolean isDirty() {
      Date date1 = attribute.getValue();
      Date date2 = getDate();
      return date1 != date2;
   }

}
