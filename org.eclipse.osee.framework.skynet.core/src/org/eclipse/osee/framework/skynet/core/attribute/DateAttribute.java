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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class DateAttribute extends Attribute<Date> {
   public static final SimpleDateFormat MMDDYY = new SimpleDateFormat("MM/dd/yyyy");
   public static final SimpleDateFormat MMDDYYHHMM = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
   public static final SimpleDateFormat HHMM = new SimpleDateFormat("hh:mm");

   /**
    * Create a date attribute with a given type, initialized to the current date and time.
    * 
    * @param attributeType The type of the attribute
    */
   // TODO: handle default String value
   public DateAttribute(DynamicAttributeDescriptor attributeType, String defaultValue) throws IllegalArgumentException {
      super(attributeType);
      if (defaultValue.equals("")) throw new IllegalArgumentException("defaultValue can not be \"\"");
      if (defaultValue == null) {
         setRawStringValue("");
      } else {
         setRawStringValue(defaultValue);
      }
   }

   /**
    * Sets date
    * 
    * @param value value or null to clear
    */
   public void setValue(Date value) throws IllegalArgumentException {
      if (value.equals("")) throw new IllegalArgumentException("defaultValue can not be \"\"");
      if (value == null)
         setRawStringValue("");
      else
         setRawStringValue(Long.toString(value.getTime()));
   }

   /**
    * Return current date or null if not set
    * 
    * @return date or null if not set
    */
   public Date getValue() {
      if (getRawStringValue().equals("")) return null;
      return new Date(Long.parseLong(getRawStringValue()));
   }

   /**
    * Return date in format given by pattern or "" if not set
    * 
    * @param pattern DateAttribute.MMDDYY, etc...
    * @return formated date
    */
   public String getAsFormattedString(SimpleDateFormat dateFormat) {
      if (getValue() == null) return "";
      return dateFormat.format(getValue());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValueFromInputStream(java.io.InputStream)
    */
   @Override
   public void setValueFromInputStream(InputStream value) throws IOException {
      throw new UnsupportedOperationException();
   }
}
