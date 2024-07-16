/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.indexer.data;

import com.google.common.io.ByteSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.orcs.core.ds.IndexedResource;
import org.eclipse.osee.orcs.db.internal.resource.ResourceConstants;

/**
 * @author Roberto E. Escobar
 */
public class IndexerDataSourceImpl extends ByteSource implements IndexedResource {

   private final IResourceManager resourceManager;

   private final AttributeId id;
   private final AttributeTypeToken attributeType;
   private final GammaId gammaId;

   private final String value;
   private final String uri;

   public IndexerDataSourceImpl(IResourceManager resourceManager, AttributeId id, AttributeTypeToken attributeType, GammaId gammaId, String value, String uri) {
      this.resourceManager = resourceManager;
      this.id = id;
      this.attributeType = attributeType;
      this.gammaId = gammaId;
      this.value = value;
      this.uri = uri;
   }

   @Override
   public GammaId getGammaId() {
      return gammaId;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   private String getStringValue() {
      return value;
   }

   private String getUri() {
      return uri;
   }

   private boolean isUriValid() {
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
   public InputStream getResourceInput() throws IOException {
      return openStream();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + gammaId.hashCode();
      return result;
   }

   @Override
   public InputStream openStream() throws IOException {
      InputStream toReturn = null;
      if (isUriValid()) {
         try {
            PropertyStore options = new PropertyStore();
            options.put(StandardOptions.DecompressOnAquire.name(), true);
            IResourceLocator locator = resourceManager.getResourceLocator(getUri());
            IResource resource = resourceManager.acquire(locator, options);
            if (resource != null) {
               toReturn = resource.getContent();
            }
         } catch (OseeCoreException ex) {
            throw new IOException(ex);
         }
      } else if (Strings.isValid(getStringValue())) {
         toReturn = new ByteArrayInputStream(getStringValue().getBytes("UTF-8"));
      }
      return toReturn;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (obj instanceof IndexedResource) {
         IndexedResource other = (IndexedResource) obj;
         if (getGammaId().notEqual(other.getGammaId())) {
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "IndexerDataSourceImpl [id=" + id + ", typeUuid=" + attributeType + ", gammaId=" + gammaId + ", uri=" + uri + ", value=" + value + "]";
   }

}
