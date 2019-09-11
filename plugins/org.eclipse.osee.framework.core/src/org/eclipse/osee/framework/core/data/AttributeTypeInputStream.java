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

import java.io.InputStream;
import java.text.SimpleDateFormat;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeTypeInputStream extends AbstractAttributeType<InputStream> {
   public AttributeTypeInputStream(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      super(id, namespace, name, mediaType, description, taggerType);
   }

   @Override
   public InputStream valueFromStorageString(String storedValue) {
      try {
         return Lib.stringToInputStream(storedValue);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public String storageStringFromValue(InputStream value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getDisplayableString(InputStream date) {
      // The SimpleDateFormat is not thread safe because it mutates its internal state for formatting and parsing
      return date != null ? new SimpleDateFormat(DateUtil.MMDDYYHHMM).format(date) : "";
   }
}