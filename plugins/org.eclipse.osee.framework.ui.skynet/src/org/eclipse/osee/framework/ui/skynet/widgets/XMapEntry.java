/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Map;
import org.eclipse.osee.framework.ui.skynet.widgets.util.MapEntryWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Loren K. Ashley
 */

public class XMapEntry extends XWidget {

   private Composite parent;
   private Composite composite;
   private MapEntryWidget mapEntryWidget;

   public XMapEntry() {
      super("");
      this.mapEntryWidget = null;
   }

   @Override
   public Control getControl() {
      return this.mapEntryWidget;
   }

   @Override
   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      super.validate();
   }

   public void setToolTips(String keyToolTip, String valueToolTip) {
      this.mapEntryWidget.setToolTips(keyToolTip, valueToolTip);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      this.setNotificationsAllowed(false);

      try {

         this.parent = parent;

         var gridLayout = new GridLayout();

         gridLayout.numColumns = 1;
         gridLayout.makeColumnsEqualWidth = false;

         var gridData = new GridData();

         gridData.horizontalAlignment = SWT.FILL;
         gridData.grabExcessHorizontalSpace = true;
         gridData.minimumWidth = 64;
         gridData.widthHint = 64;

         gridData.verticalAlignment = SWT.FILL;
         gridData.grabExcessVerticalSpace = true;

         this.composite = new Composite(parent, SWT.NONE);

         this.composite.setLayout(gridLayout);
         this.composite.setLayoutData(gridData);

         //@formatter:off
         var modifyListener =
            new ModifyListener() {
               @Override
               public void modifyText(ModifyEvent modifyEvent) {
                  XMapEntry.this.notifyXModifiedListeners();
               }
         };

         this.mapEntryWidget = new MapEntryWidget(composite, SWT.NONE);
         this.mapEntryWidget.setEnabled(this.isEditable());
         this.mapEntryWidget.setBackground(Displays.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
         this.mapEntryWidget.addModifyListener( modifyListener );

      } finally {

         this.setNotificationsAllowed(true);

      }
   }

   @Override
   public void dispose() {

      if (Widgets.isAccessible(this.mapEntryWidget)) {
         this.mapEntryWidget.dispose();
      }

      if (Widgets.isAccessible(this.composite)) {
         this.composite.dispose();
      }

      if (Widgets.isAccessible(this.parent)) {
         this.parent.layout();
      }

   }

   @Override
   public Object getData() {

      //@formatter:off
      var data =
         Widgets.isAccessible(this.mapEntryWidget)
            ? this.mapEntryWidget.getData()
            : null;
      //@formatter:on
      return data;
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (Widgets.isAccessible(this.mapEntryWidget)) {
         this.mapEntryWidget.setEditable(editable);
      }
   }

   public void setMapEntry(Map.Entry<String, String> mapEntry) {

      if (Widgets.isAccessible(this.mapEntryWidget)) {
         this.mapEntryWidget.setMapEntry(mapEntry);
      }

   }

   @Override
   public void setFocus() {

      if (Widgets.isAccessible(this.mapEntryWidget)) {
         this.mapEntryWidget.setFocus();
      }

   }

   @Override
   public boolean isEmpty() {
      //@formatter:off
      return
            !Widgets.isAccessible( this.mapEntryWidget )
         || this.mapEntryWidget.isEmpty();
   }
}
