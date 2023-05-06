/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.model.dto;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Murshed Alam
 */
public class DiffReportEndpointDto {

   private ArtifactId id = ArtifactId.SENTINEL;
   private String name = "";
   private String endpointUrl = "";

   public DiffReportEndpointDto() {
   }

   public DiffReportEndpointDto(ArtifactReadable art) {
      if (art.isValid()) {
         setId(ArtifactId.valueOf(art.getId()));
         setName(art.getName());
         setEndpointUrl(art.getSoleAttributeAsString(CoreAttributeTypes.EndpointUrl, ""));
      }
   }

   public ArtifactId getId() {
      return id;
   }

   public void setId(ArtifactId id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof DiffReportEndpointDto) {
         return this.getId().equals(((DiffReportEndpointDto) obj).getId());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   public String getEndpointUrl() {
      return endpointUrl;
   }

   public void setEndpointUrl(String endpointUrl) {
      this.endpointUrl = endpointUrl;
   }

}