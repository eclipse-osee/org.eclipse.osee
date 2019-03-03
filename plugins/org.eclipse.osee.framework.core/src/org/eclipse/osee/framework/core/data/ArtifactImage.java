/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;

/**
 * @author Donald G. Dunne
 */
public class ArtifactImage {

   private ArtifactTypeToken artifactType;
   @JsonSerialize(using = ToStringSerializer.class)
   private Long artifactTypeId;
   private String artifactTypeName;
   private String imageName;
   private String baseUrl;

   public ArtifactImage(ArtifactTypeToken artifactType, String imageName, String baseUrl) {
      this.artifactType = artifactType;
      this.imageName = imageName;
      this.baseUrl = baseUrl;
   }

   @JsonIgnore
   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   public String getImageName() {
      return imageName;
   }

   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   public void setImageName(String imageName) {
      this.imageName = imageName;
   }

   public static ArtifactImage construct(ArtifactTypeToken artifactType, String imageName) {
      return construct(artifactType, imageName, null);
   }

   public static ArtifactImage construct(ArtifactTypeToken artifactType, String imageName, String baseUrl) {
      return new ArtifactImage(artifactType, imageName, baseUrl);
   }

   public String getBaseUrl() {
      return baseUrl;
   }

   public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
   }

   public Long getArtifactTypeId() {
      if (artifactType != null) {
         return artifactType.getId();
      }
      return artifactTypeId;
   }

   public void setArtifactTypeId(Long artifactTypeId) {
      this.artifactTypeId = artifactTypeId;
   }

   public String getArtifactTypeName() {
      if (artifactType != null) {
         return artifactType.getName();
      }
      return artifactTypeName;
   }

   public void setArtifactTypeName(String artifactTypeName) {
      this.artifactTypeName = artifactTypeName;
   }

}
