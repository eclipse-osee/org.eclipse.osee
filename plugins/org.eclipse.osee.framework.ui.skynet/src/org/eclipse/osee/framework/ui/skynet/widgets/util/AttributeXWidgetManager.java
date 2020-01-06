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

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class AttributeXWidgetManager {
   private static final String EXTENSION_NAME = "AttributeXWidgetProvider";
   private static final String EXTENSION_POINT = Activator.PLUGIN_ID + "." + EXTENSION_NAME;
   private static final String CLASS_NAME_ATTRIBUTE = "classname";

   private final static AttributeXWidgetManager instance = new AttributeXWidgetManager();

   private final ExtensionDefinedObjects<IAttributeXWidgetProvider> extensionObjects;

   private AttributeXWidgetManager() {
      this.extensionObjects =
         new ExtensionDefinedObjects<>(EXTENSION_POINT, EXTENSION_NAME, CLASS_NAME_ATTRIBUTE, true);
   }

   private List<IAttributeXWidgetProvider> getProviders() {
      return extensionObjects.getObjects();
   }

   public static IAttributeXWidgetProvider getAttributeXWidgetProvider(ArtifactTypeToken artType, AttributeTypeToken attributeType) {
      for (IAttributeXWidgetProvider provider : instance.getProviders()) {
         List<XWidgetRendererItem> datas = provider.getDynamicXWidgetLayoutData(artType, attributeType);
         if (!datas.isEmpty()) {
            return provider;
         }
      }
      return new DefaultAttributeXWidgetProvider();
   }
}