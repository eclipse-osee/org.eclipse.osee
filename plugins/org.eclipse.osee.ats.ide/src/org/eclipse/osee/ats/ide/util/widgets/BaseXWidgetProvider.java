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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

/**
 * @author Donald G. Dunne
 */
public class BaseXWidgetProvider implements IXWidgetProvider {

   private static Map<String, Class<? extends XWidget>> nameToClass = new HashMap<String, Class<? extends XWidget>>();

   public static void register(Class<? extends XWidget> clazz) {
      nameToClass.put(clazz.getSimpleName(), clazz);
   }

   @Override
   public XWidget createXWidget(String widgetName, String name, XWidgetRendererItem rItem) {
      @SuppressWarnings("unchecked")
      Class<XWidget> clazz = (Class<XWidget>) nameToClass.get(widgetName);
      if (clazz != null) {
         try {
            XWidget widget = clazz.getDeclaredConstructor().newInstance();
            if (Strings.isValid(rItem.getToolTip())) {
               widget.setToolTip(rItem.getToolTip());
            }
            widget.setRequiredEntry(rItem.isRequired());
            return widget;
         } catch (Exception ex) {
            OseeLog.log(AtsWidgetProvider.class, Level.SEVERE, ex.toString(), ex);
         }
      }
      return null;
   }

}
