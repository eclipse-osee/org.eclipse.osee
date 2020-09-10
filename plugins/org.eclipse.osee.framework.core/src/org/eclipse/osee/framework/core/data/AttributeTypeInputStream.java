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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeTypeInputStream extends AttributeTypeGeneric<InputStream> {
   public static final InputStream defaultValue = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

   public AttributeTypeInputStream(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, String fileExtension) {
      super(id, namespace, name, mediaType, description, taggerType, fileExtension, defaultValue);
   }

   @Override
   public boolean isInputStream() {
      return true;
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