/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class DynamicImage {

   private String imageUrl;
   private String artifactTypeName;
   private String artifactTypeUuid;

   public String getImageUrl() {
      return imageUrl;
   }

   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   public String getArtifactTypeName() {
      return artifactTypeName;
   }

   public void setArtifactTypeName(String artifactTypeName) {
      this.artifactTypeName = artifactTypeName;
   }

   public String getArtifactTypeUuid() {
      return artifactTypeUuid;
   }

   public void setArtifactTypeUuid(String artifactTypeUuid) {
      this.artifactTypeUuid = artifactTypeUuid;
   }

}
