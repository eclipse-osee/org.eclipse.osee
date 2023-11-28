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

package org.eclipse.osee.define.rest.api.publisher.publishing;

import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.core.publishing.AttributeOptions;
import org.eclipse.osee.framework.core.publishing.MetadataOptions;
import org.eclipse.osee.framework.core.publishing.OutliningOptions;
import org.eclipse.osee.framework.core.publishing.RendererOptions;

/**
 * @author Loren K. Ashley
 */

public class TemplatePublishingData {

   private final RendererOptions rendererOptions;
   private final PublishingOptions publishingOptions;

   private TemplatePublishingData(RendererOptions rendererOptions, PublishingOptions publishingOptions) {
      this.rendererOptions = rendererOptions;
      this.publishingOptions = publishingOptions;
   }

   public String getElementType() {
      return this.rendererOptions.getElementType();
   }

   public List<AttributeOptions> getAttributeElements() {
      return List.of(this.rendererOptions.getAttributeOptions());
   }

   public List<MetadataOptions> getMetadataElements() {
      return List.of(this.rendererOptions.getMetadataOptions());
   }

   public PublishingOptions getPublishingOptions() {
      return this.publishingOptions;
   }

   public OutliningOptions getOutliningOptions() {
      return this.rendererOptions.getOutliningOptions()[0];
   }

   public static TemplatePublishingData create(RendererOptions rendererOptions, PublishingOptions publishingOptions) {

      if (!"Artifact".equals(rendererOptions.getElementType())) {
         return new TemplatePublishingData(rendererOptions, null);
      }

      publishingOptions = Objects.isNull(publishingOptions) ? new PublishingOptions() : publishingOptions;

      return new TemplatePublishingData(rendererOptions, publishingOptions);
   }

}

/* EOF */
