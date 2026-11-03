/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;

/**
 * @author Donald G. Dunne
 */
public class AttrWidgetDef extends WidgetDef {

   /**
    * Widget fully defined by the default widget provider and attrType hints. Most of the WidgetDefinitions with
    * attrType should migrate to this
    */
   public AttrWidgetDef(AttributeTypeToken attrType) {
      this(attrType.getUnqualifiedName(), attrType);
   }

   public AttrWidgetDef(String name, AttributeTypeToken attrType) {
      super(name);
      overrideWidData.setAttributeType(attrType);
   }

   public AttrWidgetDef(AttributeTypeToken attrType, WidgetId widgetId, XOption... options) {
      this(attrType);
      overrideWidData.setWidgetId(widgetId);
      for (XOption opt : options) {
         set(opt);
      }
   }

   public AttrWidgetDef(String name, AttributeTypeToken attributeType, WidgetId widgetId, //
      XOption... widgetOptions) {
      this(attributeType, widgetId, widgetOptions);
      setName(name);
   }

   public AttrWidgetDef(AttributeTypeToken attrType, XOption... options) {
      this(attrType);
      for (XOption opt : options) {
         set(opt);
      }
   }

   public void internalSetXWidgetData(XWidgetData widData) {
      this.overrideWidData = widData;
   }

   @Override
   public void setAttributeType(AttributeTypeToken attributeType) {
      this.overrideWidData.setAttributeType(attributeType);
   }

   @Override
   public void setAttributeType2(AttributeTypeToken attributeType2) {
      this.overrideWidData.setAttributeType2(attributeType2);
   }

}
