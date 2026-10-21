/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.AttributeXWidgetProviderCore;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * IDE extension of AttributeXWidgetProviderCore that adds the XWidget registration cache. XWidgets register via OSGi to
 * provide custom widget implementations for specific attribute types.
 * <p>
 * On the server, use {@link AttributeXWidgetProviderCore} directly — it resolves widgets without the XWidget cache.
 *
 * @author Donald G. Dunne
 */
public class AttributeXWidgetProviderIde extends AttributeXWidgetProviderCore {

   private static AttributeXWidgetProviderIde ideInstance;

   public static AttributeXWidgetProviderIde get() {
      if (ideInstance == null) {
         ideInstance = new AttributeXWidgetProviderIde();
         // Register this IDE instance as the core singleton so code using AttributeXWidgetProviderCore.get()
         // also benefits from the XWidget cache
         AttributeXWidgetProviderCore.setInstance(ideInstance);
      }
      return ideInstance;
   }

   private static final Map<AttributeTypeToken, XWidget> attrTypeToXWidget =
      new HashMap<AttributeTypeToken, XWidget>(50);

   /**
    * Allows an XWidget to register as the widget for that attribute type. Populated upon initial XWidget loading
    * through XWidgetService via OSGi.
    */
   public static void registerXWidget(AttributeTypeToken attrType, XWidget widget) {
      attrTypeToXWidget.put(attrType, widget);
   }

   @Override
   protected XWidgetData resolveFromRegisteredWidget(AttributeTypeToken attributeType) {
      XWidget attrWidget = attrTypeToXWidget.get(attributeType);
      if (attrWidget != null) {
         XWidgetData widData = new XWidgetData();
         widData.setAttributeType(attributeType);
         widData.setWidgetId(attrWidget.getWidgetId());
         if (Strings.isValid(attrWidget.getLabel())) {
            widData.setLabel(attrWidget.getLabel());
         } else {
            widData.setLabel(attributeType.getUnqualifiedName());
         }
         // Copy over the options as they may be specific to this XWidget
         widData.getXOptionHandler().add(attrWidget.getWidData().getXOptionHandler().getXOptions());
         return widData;
      }
      return null;
   }

}
