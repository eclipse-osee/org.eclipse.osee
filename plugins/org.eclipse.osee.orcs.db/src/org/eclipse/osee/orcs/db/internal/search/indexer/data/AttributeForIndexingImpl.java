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
package org.eclipse.osee.orcs.db.internal.search.indexer.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;

/**
 * @author Roberto E. Escobar
 */
public class AttributeForIndexingImpl implements AttributeReadable<String> {

   private final IResourceManager resourceManager;

   private final int attrId;
   private final long gammaId;

   private final IAttributeType attributeType;

   private final String value;
   private final String uri;

   public AttributeForIndexingImpl(IResourceManager resourceManager, int attrId, long gammaId, IAttributeType attributeType, String value, String uri) {
      super();
      this.resourceManager = resourceManager;
      this.attrId = attrId;
      this.gammaId = gammaId;
      this.attributeType = attributeType;
      this.value = value;
      this.uri = uri;
   }

   public int getArtId() {
      throw new UnsupportedOperationException();
   }

   public int getBranchId() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ModificationType getModificationType() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getId() {
      return attrId;
   }

   @Override
   public long getGammaId() {
      return gammaId;
   }

   @Override
   public IAttributeType getAttributeType() {
      return attributeType;
   }

   @Override
   public boolean isOfType(IAttributeType otherAttributeType) {
      return getAttributeType().equals(otherAttributeType);
   }

   @Override
   public String getValue() throws OseeCoreException {
      String toReturn = null;
      if (isUriValid()) {
         InputStream inputStream = null;
         try {
            inputStream = getExtendedDataAsStream();
            toReturn = Lib.inputStreamToString(inputStream);
         } catch (IOException ex) {
            OseeExceptions.wrapAndThrow(ex);
         } finally {
            Lib.close(inputStream);
         }
      } else {
         toReturn = getStringValue();
      }
      return toReturn;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (gammaId ^ (gammaId >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (obj instanceof AttributeReadable<?>) {
         AttributeReadable<?> other = (AttributeReadable<?>) obj;
         if (getGammaId() != other.getGammaId()) {
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private String getStringValue() {
      return value;
   }

   private String getUri() {
      return uri;
   }

   private InputStream getExtendedDataAsStream() throws OseeCoreException {
      InputStream toReturn = null;
      if (isUriValid()) {
         PropertyStore options = new PropertyStore();
         options.put(StandardOptions.DecompressOnAquire.name(), true);
         IResourceLocator locator = resourceManager.getResourceLocator(getUri());
         IResource resource = resourceManager.acquire(locator, options);
         toReturn = resource.getContent();
      }
      return toReturn;
   }

   public boolean isUriValid() {
      boolean toReturn = false;
      try {
         String value = getUri();
         if (Strings.isValid(value)) {
            URI uri = new URI(value);
            if (uri.toASCIIString().startsWith(ResourceConstants.ATTRIBUTE_RESOURCE_PROTOCOL)) {
               toReturn = true;
            }
         }
      } catch (Exception ex) {
         // DO NOTHING
      }
      return toReturn;
   }

   @Override
   public String getDisplayableString() throws OseeCoreException {
      return getValue();
   }

   @Override
   public String toString() {
      return String.format("attrId:[%s] gammaId:[%s] uri:[%s] attrType:[%s] isValidUri:[%s]", getId(), getGammaId(),
         getUri(), getAttributeType(), isUriValid());
   }

   @Override
   public boolean isDeleted() {
      return false;
   }
}
