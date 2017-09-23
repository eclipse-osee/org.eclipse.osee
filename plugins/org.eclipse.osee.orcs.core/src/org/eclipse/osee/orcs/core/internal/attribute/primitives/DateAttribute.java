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
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
@OseeAttribute("DateAttribute")
public class DateAttribute extends CharacterBackedAttribute<Date> {
   public static final String NAME = DateAttribute.class.getSimpleName();
   private static final DateFormat MMDDYYHHMM = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

   @Override
   public String convertToStorageString(Date rawValue) {
      return String.valueOf(rawValue.getTime());
   }

   @Override
   public String getDisplayableString() throws OseeCoreException {
      return getAsFormattedString(MMDDYYHHMM);
   }

   @Override
   public Date convertStringToValue(String value) {
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