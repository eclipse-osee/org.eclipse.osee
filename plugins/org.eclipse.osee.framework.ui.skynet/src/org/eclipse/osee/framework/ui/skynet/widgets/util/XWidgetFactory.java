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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * This class collects all OSGI annotation registered XWidgets for instantiation as needed.
 *
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
@Component(service = XWidgetFactory.class, immediate = true)
public final class XWidgetFactory {

   private static XWidgetFactory reference = new XWidgetFactory();
   private static Map<WidgetId, Class<? extends XWidget>> widgetIdToClass =
      new HashMap<WidgetId, Class<? extends XWidget>>(300);
   // A thread-safe list to store the references
   private final static Set<WidgetId> errorWidgetIds = new HashSet<WidgetId>();

   @Reference(service = //
   XWidget.class, //
      cardinality = ReferenceCardinality.MULTIPLE, //
      policy = ReferencePolicy.DYNAMIC, //
      policyOption = ReferencePolicyOption.GREEDY, //
      bind = "addWidget", //
      unbind = "removeWidget"//
   )

   public void addWidget(XWidget widget) {
      widgetIdToClass.put(widget.getWidgetId(), widget.getClass());
   }

   public void removeWidget(XWidget widget) {
      widgetIdToClass.remove(widget.getWidgetId());
   }

   // NOTE: Default constructor needs to be after the bind methods
   public XWidgetFactory() {
      // for osgi
      reference = this;
   }

   public static XWidgetFactory getInstance() {
      return reference;
   }

   @SuppressWarnings({"unchecked"})
   public XWidget createXWidget(WidgetId widgetId) {
      XWidget xWidget = null;
      // Create default Widget from WidgetId
      Class<XWidget> clazz = null;
      if (widgetId.isValid()) {
         clazz = (Class<XWidget>) widgetIdToClass.get(widgetId);
      }
      if (clazz != null) {
         xWidget = instantiateXWidget(clazz);
      }
      return xWidget;
   }

   public XWidget instantiateXWidget(Class<? extends XWidget> clazz) {
      try {
         XWidget xWidget = clazz.getDeclaredConstructor().newInstance();
         return xWidget;
      } catch (Exception ex) {
         OseeLog.logf(XWidgetFactory.class, Level.SEVERE, "Error instantiating XWidget %s: %s", clazz.getSimpleName(),
            ex.getLocalizedMessage());
      }
      return null;
   }

   public static Set<WidgetId> getErrorwidgetids() {
      return errorWidgetIds;
   }

}
