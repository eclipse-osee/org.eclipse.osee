/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.validator;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class AttributeWidget {
   private String widgetName;
   private List<AttributeTypeToken> attrTypes = new ArrayList<>();

   public AttributeWidget(String widgetName, AttributeTypeToken... attrTypes) {
      this.widgetName = widgetName;
      for (AttributeTypeToken type : attrTypes) {
         this.attrTypes.add(type);
      }
   }

   public String getWidgetName() {
      return widgetName;
   }

   public void setWidgetName(String widgetName) {
      this.widgetName = widgetName;
   }

   public List<AttributeTypeToken> getAttrTypes() {
      return attrTypes;
   }

   public void setAttrTypes(List<AttributeTypeToken> attrTypes) {
      this.attrTypes = attrTypes;
   }
}
