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

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;

/**
 * @author Donald G. Dunne
 */
public class EnumArtWidgetDef extends AttrWidgetDef {

   public EnumArtWidgetDef(String name, ArtifactToken enumArtifact, XOption... widgetOptions) {
      this(AttributeTypeToken.SENTINEL, enumArtifact, widgetOptions);
   }

   public EnumArtWidgetDef(AttributeTypeToken attributeType, ArtifactToken enumArtifact, XOption... widgetOptions) {
      super(attributeType.getUnqualifiedName(), attributeType, WidgetId.XXStringsEnumArtWidget, widgetOptions);
      andEnumArt(enumArtifact);
   }

   public EnumArtWidgetDef addPriorityUrl(String priorityUrl) {
      addParameter("DescUrl", priorityUrl);
      return this;
   }

}
