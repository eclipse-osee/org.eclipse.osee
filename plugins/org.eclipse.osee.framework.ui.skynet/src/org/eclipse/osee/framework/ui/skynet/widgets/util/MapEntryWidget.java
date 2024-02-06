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

package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class MapEntryWidget extends Composite {

   private static int MAXIMUM_SIZE_FOR_DISPLAY = 2048 - 1;

   private Text keyLabel;
   private Text keyText;
   private Text valueLabel;
   private Text valueText;
   private String largeText;

   public MapEntryWidget(Composite parent, int style) {

      super(parent, SWT.NONE);

      var gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      gridLayout.makeColumnsEqualWidth = false;
      gridLayout.horizontalSpacing = 0;

      //@formatter:off
      var gridData =
         new GridData
                (
                   SWT.FILL,    /* Horizontal Position          */
                   SWT.FILL,    /* Vertical   Position          */
                   true,        /* Grab excess horizontal space */
                   false        /* Grab excess vertical space   */
                );
      //@formatter:on

      this.setLayout(gridLayout);
      this.setLayoutData(gridData);

      this.createControl(this);
   }

   public void addModifyListener(ModifyListener modifyListener) {

      this.keyText.addModifyListener(modifyListener);
      this.valueText.addModifyListener(modifyListener);
   }

   private void createControl(final Composite parent) {

      var gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      gridLayout.makeColumnsEqualWidth = false;
      gridLayout.horizontalSpacing = 10;
      gridLayout.verticalSpacing = 10;

      var gridData = new GridData();

      gridData.horizontalAlignment = SWT.FILL;
      gridData.grabExcessHorizontalSpace = true;
      gridData.minimumWidth = 64;
      gridData.widthHint = 64;

      gridData.verticalAlignment = SWT.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.minimumHeight = 256;
      gridData.heightHint = 256;

      var composite = new Composite(parent, SWT.NONE);
      composite.setLayout(gridLayout);
      composite.setLayoutData(gridData);

      this.keyLabel = new Text(composite, SWT.BOLD | SWT.SINGLE);
      this.keyLabel.setText("Key:");
      this.keyLabel.setEditable(false);

      var textGridData = new GridData();

      textGridData.horizontalAlignment = SWT.FILL;
      textGridData.grabExcessHorizontalSpace = true;
      textGridData.minimumWidth = 64;
      textGridData.widthHint = 64;

      this.keyText = new Text(composite, SWT.SINGLE | SWT.BORDER);
      this.keyText.setEditable(true);
      this.keyText.setLayoutData(textGridData);

      this.valueLabel = new Text(composite, SWT.BOLD | SWT.SINGLE);
      this.valueLabel.setText("Value:");
      this.valueLabel.setEditable(false);

      var valueGridData = new GridData();

      valueGridData.horizontalAlignment = SWT.FILL;
      valueGridData.grabExcessHorizontalSpace = true;
      valueGridData.minimumWidth = 64;
      valueGridData.widthHint = 64;

      valueGridData.verticalAlignment = SWT.FILL;
      valueGridData.grabExcessVerticalSpace = true;
      valueGridData.minimumHeight = SWT.DEFAULT;
      valueGridData.heightHint = 64;

      this.valueText = new Text(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      this.valueText.setEditable(true);
      this.valueText.setLayoutData(valueGridData);
      this.largeText = null;

   }

   public void setToolTips(String keyToolTip, String valueToolTip) {

      this.checkWidget();

      if (Widgets.isAccessible(this.keyLabel)) {
         this.keyLabel.setToolTipText(keyToolTip);
      }

      if (Widgets.isAccessible(this.valueLabel)) {
         this.valueLabel.setToolTipText(valueToolTip);
      }

   }

   @Override
   public void dispose() {

      if (Widgets.isAccessible(this.keyLabel)) {
         this.keyLabel.dispose();
      }

      if (Widgets.isAccessible(this.keyText)) {
         this.keyText.dispose();
      }

      if (Widgets.isAccessible(this.valueLabel)) {
         this.valueLabel.dispose();
      }

      if (Widgets.isAccessible(this.valueText)) {
         this.valueText.dispose();
      }

      super.dispose();

   }

   @Override
   public Object getData() {

      this.checkWidget();

      return this.getMapEntry();

   }

   public Map.Entry<String, String> getMapEntry() {

      this.checkWidget();

      if (!this.isAccessible()) {
         return null;
      }

      var key = this.keyText.getText();
      var value = Objects.nonNull(this.largeText) ? this.largeText : this.valueText.getText();

      var mapEntry = Map.entry(key, value);

      return mapEntry;
   }

   private boolean isAccessible() {
      //@formatter:off
      return
            Widgets.isAccessible( this.keyText )
         && Widgets.isAccessible( this.valueText );
      //@formatter:on
   }

   public boolean isEmpty() {

      this.checkWidget();

      //@formatter:off
      if( !this.isAccessible() ) {
         return true;
      }

      var key   = this.keyText.getText();

      var value = Objects.nonNull( this.largeText )
                     ? this.largeText
                     : this.valueText.getText();

      return
            Strings.isInvalidOrBlank( key   )
         && Strings.isInvalidOrBlank( value );
      //@formatter:on
   }

   public void setEditable(boolean editable) {

      this.checkWidget();

      if (Widgets.isAccessible(this.keyLabel)) {
         this.keyLabel.setEditable(false);
      }

      if (Widgets.isAccessible(this.valueLabel)) {
         this.valueLabel.setEditable(false);
      }

      if (this.isAccessible()) {
         if (Objects.isNull(this.largeText)) {
            this.keyText.setEditable(editable);
            this.valueText.setEditable(editable);
         } else {
            this.keyText.setEditable(false);
            this.valueText.setEditable(false);
         }
      }

   }

   public void setMapEntry(Map.Entry<String, String> mapEntry) {

      this.checkWidget();

      if (!this.isAccessible()) {
         return;
      }

      var key = mapEntry.getKey();
      var value = mapEntry.getValue();

      this.keyText.setText(key);

      if (value.length() <= MapEntryWidget.MAXIMUM_SIZE_FOR_DISPLAY) {
         this.valueText.setText(value);
         this.largeText = null;
         this.keyText.setEditable(true);
         this.valueText.setEditable(true);
      } else {
         this.valueText.setText("--to-large--");
         this.largeText = value;
         this.keyText.setEditable(false);
         this.valueText.setEditable(false);
      }

   }

   @Override
   public String toString() {
      //@formatter:off
      return
         new Message()
                .title( this.getClass().getName() )
                .indentInc()
                .segment( "Large Text", Objects.nonNull( this.largeText ) ? "IS SET" : "NOT SET" )
                .segment( "Key", this.keyText.getText() )
                .toString();
      //@formatter:on

   }

}
