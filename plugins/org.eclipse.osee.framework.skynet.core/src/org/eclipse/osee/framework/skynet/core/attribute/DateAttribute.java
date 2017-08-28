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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class DateAttribute extends CharacterBackedAttribute<Date> {
   public static final DateFormat MMDDYY = new SimpleDateFormat("MM/dd/yyyy");
   public static final DateFormat HHMM = new SimpleDateFormat("hh:mm");
   public final DateFormat MMDDYYHHMM = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
   public final DateFormat MMDDYYYYHHMMSSAMPM = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a");
   public final DateFormat ALLDATETIME = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

   @Override
   public Date getValue() throws OseeCoreException {
      Object value = getAttributeDataProvider().getValue();
      return new Date((Long) value);
   }

   @Override
   protected void setToDefaultValue() throws OseeCoreException {
      String defaultValue = getAttributeType().getDefaultValue();
      if (Strings.isValid(defaultValue)) {
         setFromStringNoDirty(defaultValue);
      } else {
         subClassSetValue(new Date());
      }
   }

   @Override
   public boolean subClassSetValue(Date value) throws OseeCoreException {
      if (value == null) {
         OseeLog.log(this.getClass(), Level.SEVERE,
            String.format("AttributeType [%s] GammId [%s] - DateAttribute.subClassSetValue had a null value",
               getAttributeType(), getGammaId()));
         return false;
      }
      return getAttributeDataProvider().setValue(value.getTime());
   }

   @Override
   public String getDisplayableString() throws OseeCoreException {
      return getAsFormattedString(MMDDYYHHMM);
   }

   @Override
   public Date convertStringToValue(String value) {
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
      return dateFormat.format(getValue());
   }

}