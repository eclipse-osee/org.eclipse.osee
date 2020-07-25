/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.osee.orcs.core.annotations.OseeAttribute;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
@OseeAttribute("DateAttribute")
public class DateAttribute extends CharacterBackedAttribute<Date> {
   private static final DateFormat MMDDYYHHMM = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

   public DateAttribute(Long id) {
      super(id);
   }

   @Override
   public String convertToStorageString(Date rawValue) {
      return String.valueOf(rawValue.getTime());
   }

   @Override
   public String getDisplayableString() {
      return getAsFormattedString(MMDDYYHHMM);
   }

   @Override
   Date subclassConvertStringToValue(String value) {
      return new Date(Long.parseLong(value));
   }

   /**
    * Return date in format given by pattern or "" if not set
    *
    * @param pattern DateAttribute.MMDDYY, etc...
    * @return formated date
    */
   public String getAsFormattedString(DateFormat dateFormat) {
      Date date = getValue();
      return date != null ? dateFormat.format(date) : "";
   }
}