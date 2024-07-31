/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeTypeDate extends AttributeTypeGeneric<Date> {
   public AttributeTypeDate(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      super(id, namespace, name, mediaType, description, taggerType, "", new Date(0L), null);
   }

   @Override
   public boolean isDate() {
      return true;
   }

   @Override
   public Date valueFromStorageString(String storedValue) {
      return new Date(Long.parseLong(storedValue));
   }

   @Override
   public String storageStringFromValue(Date date) {
      return String.valueOf(date.getTime());
   }

   @Override
   public String getDisplayableString(Date date) {
      // The SimpleDateFormat is not thread safe because it mutates its internal state for formatting and parsing
      return date != null ? new SimpleDateFormat(DateUtil.MMDDYYHHMM).format(date) : "";
   }
}