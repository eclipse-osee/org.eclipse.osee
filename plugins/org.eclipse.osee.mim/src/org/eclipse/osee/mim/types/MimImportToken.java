/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim.types;

import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan Baldwin
 */
public class MimImportToken extends PLGenericDBObject {
   public static final MimImportToken SENTINEL = new MimImportToken();

   private String url;
   private String transportType;
   private boolean connectionRequired;

   public MimImportToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public MimImportToken(ArtifactReadable art) {
      this();
      this.setId(art.getId());
      this.setName(art.getName());
      this.setUrl(art.getSoleAttributeValue(CoreAttributeTypes.EndpointUrl, ""));
      this.setTransportType(art.getSoleAttributeValue(CoreAttributeTypes.ImportTransportType, ""));
      this.setConnectionRequired(art.getSoleAttributeValue(CoreAttributeTypes.ConnectionRequired, false));
   }

   /**
    * @param id
    * @param name
    */
   public MimImportToken(Long id, String name) {
      super(id, name);
   }

   public MimImportToken() {
      super();
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   /**
    * @return the transportType
    */
   public String getTransportType() {
      return transportType;
   }

   /**
    * @param transportType the transportType to set
    */
   public void setTransportType(String transportType) {
      this.transportType = transportType;
   }

   public boolean isConnectionRequired() {
      return connectionRequired;
   }

   public void setConnectionRequired(boolean connectionRequired) {
      this.connectionRequired = connectionRequired;
   }

}
