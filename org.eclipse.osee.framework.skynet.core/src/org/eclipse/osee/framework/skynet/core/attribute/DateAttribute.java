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
import org.xml.sax.SAXException;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class DateAttribute extends Attribute {
   public static final String TYPE_NAME = "Date";
   public static String MMDDYY = "MM/dd/yyyy";
   public static String MMDDYYHHMM = "MM/dd/yyyy hh:mm a";
   public static String HHMM = "hh:mm";

   /**
    * Create a date attribute with a given name, initialized to the current date and time.
    * 
    * @param name The name of the attribute
    */
   public DateAttribute(IMediaResolver resolver, String name) {
      this(resolver, name, new Date());
   }

   public DateAttribute(String name) {
      this(new VarcharMediaResolver(), name, new Date());
   }

   /**
    * Create a date attribute with a given name and value.
    * 
    * @param name The name of the attribute
    * @param value The initial value of the attribute
    */
   public DateAttribute(IMediaResolver resolver, String name, Date value) {
      super(resolver, name);
      setValue(value);
   }

   public void setValue(Date value) {
      setStringData(Long.toString(value.getTime()));
      setDirty();
   }

   public Date getDate() {
      return new Date(Long.parseLong(getStringData()));
   }

   /**
    * Return date in format given by pattern
    * 
    * @param pattern DateAttribute.MMDDYY, etc...
    * @return formated date
    */
   public String getStringValue(String pattern) {
      return (new SimpleDateFormat(pattern)).format(getDate());
   }

   @Override
   public String getTypeName() {
      return TYPE_NAME;
   }

   @Override
   public void setValidityXml(String validityXml) throws SAXException {
   }
}
