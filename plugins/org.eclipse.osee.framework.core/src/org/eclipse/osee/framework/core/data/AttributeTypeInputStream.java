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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeTypeInputStream extends AttributeTypeGeneric<InputStream> {
   /**
    * Sentinel default value. This static instance is used only as a type reference / placeholder
    * and should not be read from directly; consumers must not depend on its stream content.
    */
   public static final InputStream defaultValue = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

   public AttributeTypeInputStream(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, String fileExtension) {
      super(id, namespace, name, mediaType, description, taggerType, fileExtension, defaultValue, null);
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
      return "";
   }

   @Override
   public String getDisplayableString(InputStream date) {
      return date != null ? "<binary content>" : "";
   }
}