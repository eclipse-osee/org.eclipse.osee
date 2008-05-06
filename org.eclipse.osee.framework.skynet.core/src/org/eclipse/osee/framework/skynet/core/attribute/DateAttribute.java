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

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.attribute.providers.ICharacterAttributeDataProvider;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class DateAttribute extends CharacterBackedAttribute<Date> {
   public static final SimpleDateFormat MMDDYY = new SimpleDateFormat("MM/dd/yyyy");
   public static final SimpleDateFormat MMDDYYHHMM = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
   public static final SimpleDateFormat HHMM = new SimpleDateFormat("hh:mm");

   private ICharacterAttributeDataProvider dataProvider;

   /**
    * Create a date attribute with a given type, initialized to the current date and time.
    * 
    * @param attributeType The type of the attribute
    */
   public DateAttribute(DynamicAttributeDescriptor attributeType, ICharacterAttributeDataProvider dataProvider) {
      super(attributeType);
      this.dataProvider = dataProvider;
      String defaultValue = attributeType.getDefaultValue();
      if (Strings.isValid(defaultValue) != true) {
         defaultValue = "";
      }
      dataProvider.setValue(defaultValue);
   }

   /**
    * Sets date
    * 
    * @param value value or null to clear
    */
   public void setValue(Date value) {
      String toSet = value != null ? Long.toString(value.getTime()) : "";
      dataProvider.setValue(toSet);
   }

   /**
    * Return current date or null if not set
    * 
    * @return date or null if not set
    */
   public Date getValue() {
      String value = dataProvider.getValueAsString();
      return Strings.isValid(value) ? new Date(Long.parseLong(value)) : null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      return getAsFormattedString(DateAttribute.MMDDYY);
   }

   /**
    * Return date in format given by pattern or "" if not set
    * 
    * @param pattern DateAttribute.MMDDYY, etc...
    * @return formated date
    */
   public String getAsFormattedString(SimpleDateFormat dateFormat) {
      Date date = getValue();
      return date != null ? dateFormat.format(getValue()) : "";
   }

}
