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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class DateAttribute extends CharacterBackedAttribute<Date> {
   public static final DateFormat MMDDYY = new SimpleDateFormat("MM/dd/yyyy");
   public static final DateFormat MMDDYYHHMM = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
   public static final DateFormat HHMM = new SimpleDateFormat("hh:mm");
   public static final DateFormat MMDDYYYYHHMMSSAMPM = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a");
   public static final DateFormat ALLDATETIME = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

   private static final DateFormat[] legacyDateFormats = new DateFormat[] {MMDDYYYYHHMMSSAMPM, ALLDATETIME, MMDDYYHHMM};

   /**
    * Create a date attribute with a given type, initialized to the current date and time.
    * 
    * @param attributeType The type of the attribute
    */
   public DateAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);
   }

   /**
    * Return current date or null if not set
    * 
    * @return date or null if not set
    * @throws OseeCoreException
    */
   public Date getValue() throws OseeCoreException {
      Date toReturn = null;
      String value = getAttributeDataProvider().getValueAsString();
      if (Strings.isValid(value) != false) {
         //TODO Added for backward compatibility with inconsistent date formats;
         try {
            toReturn = new Date(Long.parseLong(value));
         } catch (Exception ex) {
            // We have a legacy date - need to figure out how to parse it
            toReturn = handleLegacyDates(value);
         }
      }
      return toReturn;
   }

   private Date handleLegacyDates(String rawValue) {
      Date toReturn = null;
      for (DateFormat format : legacyDateFormats) {
         try {
            toReturn = format.parse(rawValue);
            break;
         } catch (ParseException ex) {
         }
      }
      return toReturn;
   }

   /**
    * Sets date
    * 
    * @param value value or null to clear
    * @throws OseeCoreException
    */
   @Override
   public boolean subClassSetValue(Date value) throws OseeCoreException {
      String toSet = value != null ? Long.toString(value.getTime()) : "";
      return getAttributeDataProvider().setValue(toSet);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getDisplayableString()
    */
   @Override
   public String getDisplayableString() throws OseeCoreException {
      return getAsFormattedString(DateAttribute.MMDDYYHHMM);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#convertStringToValue(java.lang.String)
    */
   @Override
   protected Date convertStringToValue(String value) {
      if (value == null || value.equals("")) {
         return null;
      }
      return new Date(Long.parseLong(value));
   }

   /**
    * Return date in format given by pattern or "" if not set
    * 
    * @param pattern DateAttribute.MMDDYY, etc...
    * @return formated date
    * @throws OseeCoreException
    */
   public String getAsFormattedString(DateFormat dateFormat) throws OseeCoreException {
      Date date = getValue();
      return date != null ? dateFormat.format(getValue()) : "";
   }
}