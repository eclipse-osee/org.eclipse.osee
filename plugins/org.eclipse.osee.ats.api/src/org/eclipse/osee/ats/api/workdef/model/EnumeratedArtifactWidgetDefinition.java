/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.api.workdef.model;

import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class EnumeratedArtifactWidgetDefinition extends WidgetDefinition {

   public EnumeratedArtifactWidgetDefinition(String name, ArtifactToken enumArtifact, WidgetOption... widgetOptions) {
      this(false, AttributeTypeToken.SENTINEL, enumArtifact, widgetOptions);
   }

   public EnumeratedArtifactWidgetDefinition(boolean isAttrDam, AttributeTypeToken attributeType, ArtifactToken enumArtifact, WidgetOption... widgetOptions) {
      super(attributeType.getUnqualifiedName(), attributeType,
         (isAttrDam ? "XHyperlinkLabelEnumeratedArtDam" : "XHyperlinkLabelEnumeratedArt"), widgetOptions);
      andEnumeratedArt(enumArtifact);
   }

   public EnumeratedArtifactWidgetDefinition addPriorityUrl(String priorityUrl) {
      addParameter("DescUrl", priorityUrl);
      return this;
   }

}
