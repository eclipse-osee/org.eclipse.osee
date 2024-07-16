/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.engines;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.DataResource;
import org.eclipse.osee.framework.resource.management.IResourceManager;

/**
 * @author Ryan D. Brooks
 */
public class Attribute<T> extends BaseId implements IAttribute<T> {
   private final IResourceManager resourceManager;
   private final AttributeTypeGeneric<T> attributeType;
   private final String uri;
   private T value;

   public Attribute(Long id, AttributeTypeGeneric<T> attributeType, String value, String uri, IResourceManager resourceManager) {
      super(id);
      this.attributeType = attributeType;
      this.uri = uri;
      this.resourceManager = resourceManager;
      if (Strings.isValid(value)) {
         this.value = attributeType.valueFromStorageString(value);
      }
   }

   @Override
   public String getDisplayableString() {
      return attributeType.storageStringFromValue(getValue());
   }

   private String loadBinaryAttribute() {
      try {
         DataResource dataResource = new DataResource();
         dataResource.setLocator(uri);
         byte[] rawData = resourceManager.acquire(dataResource);
         if (rawData == null) {
            return "";
         } else {
            ByteBuffer decompressed = ByteBuffer.wrap(Lib.decompressBytes(new ByteArrayInputStream(rawData)));
            return StandardCharsets.UTF_8.decode(decompressed).toString();
         }
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private InputStream loadBinaryAttributeAsInputStream() {
      try {
         DataResource dataResource = new DataResource();
         dataResource.setLocator(uri);
         byte[] rawData = resourceManager.acquire(dataResource);
         if (rawData == null) {
            return null;
         } else {
            return new ByteArrayInputStream(Lib.decompressBytes(new ByteArrayInputStream(rawData)));
         }
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @SuppressWarnings("unchecked")
   @Override
   public T getValue() {
      if (Strings.isValid(uri) && value == null) {
         if (attributeType.isInputStream()) {
            this.value = (T) loadBinaryAttributeAsInputStream();
         } else {
            this.value = (T) loadBinaryAttribute();
         }
      }
      return value;
   }

   @Override
   public String toString() {
      return attributeType.getName() + ": " + String.valueOf(value);
   }
}