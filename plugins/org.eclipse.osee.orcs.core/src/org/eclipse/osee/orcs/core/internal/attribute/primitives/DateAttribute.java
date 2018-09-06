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
package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
@OseeAttribute("DateAttribute")
public class DateAttribute extends CharacterBackedAttribute<Date> {
   private static final DateFormat MMDDYYHHMM = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

   /**
    * Return current date or null if not set
    *
    * @return date or null if not set
    */
   @Override
   public Date getValue() throws OseeCoreException {
      Object value = getDataProxy().getValue();
      return new Date((Long) value);
   }

   @Override
   protected void setToDefaultValue() throws OseeCoreException {
      String defaultValue = getDefaultValueFromMetaData();
      if (Strings.isValid(defaultValue)) {
         subClassSetValue(convertStringToValue(defaultValue));
      } else {
         subClassSetValue(new Date());
      }
   }

   /**
    * Sets date
    *
    * @param value value or null to clear
    */
   @Override
   public boolean subClassSetValue(Date value) throws OseeCoreException {
      return getDataProxy().setValue(value != null ? value.getTime() : "");
   }

   @Override
   public String getDisplayableString() throws OseeCoreException {
      return getAsFormattedString(MMDDYYHHMM);
   }

   @Override
   protected Date convertStringToValue(String value) {
      if (!Strings.isValid(value)) {
         return null;
      }
      return new Date(Long.parseLong(value));
   }

   /**
    * Return date in format given by pattern or "" if not set
    *
    * @param pattern DateAttribute.MMDDYY, etc...
    * @return formated date
    */
   public String getAsFormattedString(DateFormat dateFormat) throws OseeCoreException {
      Date date = getValue();
      return date != null ? dateFormat.format(date) : "";
   }

}