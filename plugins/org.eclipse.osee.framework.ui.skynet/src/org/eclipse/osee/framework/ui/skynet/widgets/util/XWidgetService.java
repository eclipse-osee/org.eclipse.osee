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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
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
@Component(service = XWidgetService.class, immediate = true)
public final class XWidgetService {

   private static XWidgetService instance = new XWidgetService();
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
      validateWidget(widget);
      widgetIdToClass.put(widget.getWidgetId(), widget.getClass());
      // How XWidgets register themselves as providers for a certain attribute
      for (AttributeTypeToken attrType : widget.getWidData().getProviderFor()) {
         AttributeXWidgetProviderIde.registerXWidget(attrType, widget);
      }
   }

   private void validateWidget(XWidget widget) {
      if (!widget.getClass().getSimpleName().equals(widget.getWidgetId().getName())) {
         OseeLog.log(getClass(), Level.SEVERE,
            String.format("XWidget Error: Widget Class [%s] must match WidgetId.Name [%s]",
               widget.getClass().getSimpleName(), widget.getWidgetId().getName()));
      }
   }

   public void removeWidget(XWidget widget) {
      widgetIdToClass.remove(widget.getWidgetId());
   }

   // NOTE: Default constructor needs to be after the bind methods
   public XWidgetService() {
      // for osgi
      instance = this;
   }

   public static XWidgetService get() {
      return instance;
   }

   public XWidgetData getDefaultAttrXWidgetData(ArtifactTypeToken artType, AttributeTypeToken attributeType) {
      return AttributeXWidgetProviderIde.get().getDefaultXWidgetData(artType, attributeType);
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
         OseeLog.logf(XWidgetService.class, Level.SEVERE, "Error instantiating XWidget %s: %s", clazz.getSimpleName(),
            ex.getLocalizedMessage());
      }
      return null;
   }

   /**
    * Gets a snapshot of the XWidget classes currently registered through OSGi, indexed by widget id.
    *
    * @return an unmodifiable snapshot of the registered widget classes
    */
   public Map<WidgetId, Class<? extends XWidget>> getRegisteredWidgetClasses() {
      return Collections.unmodifiableMap(new HashMap<>(widgetIdToClass));
   }

   public static Set<WidgetId> getErrorwidgetids() {
      return errorWidgetIds;
   }

}
