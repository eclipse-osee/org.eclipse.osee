/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.DataResource;
import org.eclipse.osee.framework.resource.management.IResourceManager;

/**
 * @author Ryan D. Brooks
 */
public class Attribute<T> extends BaseId implements IAttribute<T> {
   private final IResourceManager resourceManager;
   private final AttributeTypeToken attributeType;
   private final String uri;
   private T value;

   public Attribute(Long id, AttributeTypeGeneric<T> attributeType, String value, String uri, IResourceManager resourceManager) {
      super(id);
      this.attributeType = attributeType;
      this.uri = uri;
      this.resourceManager = resourceManager;
      if (uri == null && value != null) {
         this.value = attributeType.valueFromStorageString(value);
      }
   }

   private String loadBinaryAttribute() {
      //      AttributeLocatorProvider.seedTo(builder, gammaId)
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

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public T getValue() {
      if (uri != null && value == null) {
         this.value = (T) loadBinaryAttribute();
      }
      return value;
   }
}