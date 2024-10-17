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

import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan Baldwin
 */
public class MimImportToken extends ArtifactAccessorResultWithoutGammas {
   public static final MimImportToken SENTINEL = new MimImportToken();

   private String url;
   private boolean connectionRequired;
   private boolean transportTypeRequired;

   public MimImportToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public MimImportToken(ArtifactReadable art) {
      this();
      this.setId(art.getId());
      this.setName(art.getName());
      this.setUrl(art.getSoleAttributeValue(CoreAttributeTypes.EndpointUrl, ""));
      this.setConnectionRequired(art.getSoleAttributeValue(CoreAttributeTypes.ConnectionRequired, false));
      this.setTransportTypeRequired(art.getSoleAttributeValue(CoreAttributeTypes.TransportTypeRequired, false));
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

   public boolean isConnectionRequired() {
      return connectionRequired;
   }

   public void setConnectionRequired(boolean connectionRequired) {
      this.connectionRequired = connectionRequired;
   }

   public boolean isTransportTypeRequired() {
      return transportTypeRequired;
   }

   public void setTransportTypeRequired(boolean transportTypeRequired) {
      this.transportTypeRequired = transportTypeRequired;
   }

}
