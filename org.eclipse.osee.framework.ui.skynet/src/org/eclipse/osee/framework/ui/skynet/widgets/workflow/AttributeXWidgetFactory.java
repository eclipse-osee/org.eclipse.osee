/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class AttributeXWidgetFactory {

   private static List<IAttributeXWidgetProvider> widgetProviders;

   public static IAttributeXWidgetProvider getAttributeXWidgetProvider(AttributeType attributeType) {
      for (IAttributeXWidgetProvider provider : getAttributeXWidgetProviders()) {
         DynamicXWidgetLayoutData data = provider.getDynamicXWidgetLayoutData(attributeType);
         if (data != null) return provider;
      }
      return new DefaultAttributeXWidgetProvider();
   }

   public static List<IAttributeXWidgetProvider> getAttributeXWidgetProviders() {
      widgetProviders = new ArrayList<IAttributeXWidgetProvider>();
      for (IConfigurationElement el : ExtensionPoints.getExtensionElements(
            "org.eclipse.osee.framework.ui.skynet.AttributeXWidgetProvider", "AttributeXWidgetProvider")) {
         String classname = null;
         String bundleName = null;
         if (el.getName().equals("AttributeXWidgetProvider")) {
            classname = el.getAttribute("classname");
            bundleName = el.getContributor().getName();
            if (classname != null && bundleName != null) {
               Bundle bundle = Platform.getBundle(bundleName);
               try {
                  Class<?> taskClass = bundle.loadClass(classname);
                  Object obj = taskClass.newInstance();
                  widgetProviders.add((IAttributeXWidgetProvider) obj);
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                        "Error loading AttributeXWidgetProvider extension", ex);
               }
            }

         }
      }
      return widgetProviders;
   }

}