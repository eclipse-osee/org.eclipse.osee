/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.Map;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.MapEntryWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.component.annotations.Component;

/**
 * @author Loren K. Ashley
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXMapEntryWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetId.XXMapEntryWidget;

   private MapEntryWidget mapEntryWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXMapEntryWidget() {
      this(ID, "");
   }

   public XXMapEntryWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.VAL_MULTI_TEXT);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      comp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      layout.marginHeight = 0;
      comp.setLayout(layout);
      GridData compGd = new GridData(SWT.FILL, SWT.FILL, true, true);
      compGd.horizontalSpan = horizontalSpan;
      compGd.minimumWidth = 64;
      comp.setLayoutData(compGd);
      if (getToolkit() != null) {
         getToolkit().adapt(comp);
      }
      createLabelWidget();
      setNotificationsAllowed(false);
      try {
         mapEntryWidget = new MapEntryWidget(comp, SWT.NONE);
         mapEntryWidget.setEnabled(isEditable());
         mapEntryWidget.setBackground(Displays.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
         mapEntryWidget.addModifyListener(e -> notifyXModifiedListeners());
      } finally {
         setNotificationsAllowed(true);
      }
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getSentinel() {
      return "";
   }

   public void setMapEntry(Map.Entry<String, String> mapEntry) {
      if (Widgets.isAccessible(mapEntryWidget)) {
         mapEntryWidget.setMapEntry(mapEntry);
      }
   }

   @SuppressWarnings("unchecked")
   public Map.Entry<String, String> getMapEntry() {
      if (Widgets.isAccessible(mapEntryWidget)) {
         return (Map.Entry<String, String>) mapEntryWidget.getData();
      }
      return null;
   }

   @Override
   protected boolean handleSelection() {
      return false;
   }

   @Override
   public String getDisplayValue() {
      Map.Entry<String, String> entry = getMapEntry();
      if (entry != null) {
         return entry.getKey() + " = " + entry.getValue();
      }
      return "";
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public void refresh() {
      validate();
   }

   @Override
   public Control getControl() {
      return mapEntryWidget;
   }

   @Override
   public boolean isEmpty() {
      return !Widgets.isAccessible(mapEntryWidget) || mapEntryWidget.isEmpty();
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (Widgets.isAccessible(mapEntryWidget)) {
         mapEntryWidget.setEditable(editable);
      }
   }

   @Override
   public void setFocus() {
      if (Widgets.isAccessible(mapEntryWidget)) {
         mapEntryWidget.setFocus();
      }
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void setToolTips(String keyToolTip, String valueToolTip) {
      if (Widgets.isAccessible(mapEntryWidget)) {
         mapEntryWidget.setToolTips(keyToolTip, valueToolTip);
      }
   }

}
