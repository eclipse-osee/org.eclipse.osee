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
public class MimReportToken extends PLGenericDBObject {
   public static final MimReportToken SENTINEL = new MimReportToken();

   private String url;
   private String httpMethod;
   private String fileExtension;
   private String fileNamePrefix;
   private String producesMediaType;
   private boolean diffAvailable;

   public MimReportToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public MimReportToken(ArtifactReadable art) {
      this();
      this.setId(art.getId());
      this.setName(art.getName());
      this.setUrl(art.getSoleAttributeValue(CoreAttributeTypes.EndpointUrl, ""));
      this.setFileNamePrefix(art.getSoleAttributeValue(CoreAttributeTypes.FileNamePrefix, ""));
      this.setHttpMethod(art.getSoleAttributeAsString(CoreAttributeTypes.HttpMethod, ""));
      this.setFileExtension(art.getSoleAttributeAsString(CoreAttributeTypes.FileExtension, ""));
      this.setProducesMediaType(art.getSoleAttributeAsString(CoreAttributeTypes.ProducesMediaType, ""));
      this.setDiffAvailable(art.getSoleAttributeValue(CoreAttributeTypes.DiffAvailable, false));
   }

   /**
    * @param id
    * @param name
    */
   public MimReportToken(Long id, String name) {
      super(id, name);
   }

   public MimReportToken() {
      super();
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getHttpMethod() {
      return httpMethod;
   }

   public void setHttpMethod(String httpMethod) {
      this.httpMethod = httpMethod;
   }

   public String getFileExtension() {
      return fileExtension;
   }

   public void setFileExtension(String fileExtension) {
      this.fileExtension = fileExtension;
   }

   public String getFileNamePrefix() {
      return fileNamePrefix;
   }

   public void setFileNamePrefix(String fileNamePrefix) {
      this.fileNamePrefix = fileNamePrefix;
   }

   public String getProducesMediaType() {
      return producesMediaType;
   }

   public void setProducesMediaType(String producesMediaType) {
      this.producesMediaType = producesMediaType;
   }

   public boolean isDiffAvailable() {
      return diffAvailable;
   }

   public void setDiffAvailable(boolean diffAvailable) {
      this.diffAvailable = diffAvailable;
   }

}
