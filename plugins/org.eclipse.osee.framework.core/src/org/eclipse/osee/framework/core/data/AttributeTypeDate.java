/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeTypeDate extends AttributeTypeGeneric<Date> {
   public AttributeTypeDate(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      super(id, namespace, name, mediaType, description, taggerType);
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