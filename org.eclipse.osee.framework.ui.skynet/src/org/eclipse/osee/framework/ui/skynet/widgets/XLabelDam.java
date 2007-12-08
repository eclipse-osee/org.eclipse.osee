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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Provided to show the contents of an attribute without any ability to edit
 * 
 * @author Donald G. Dunne
 */
public class XLabelDam extends XWidget implements IDamWidget {

   private Artifact artifact;
   private String attrName;
   private String data = "";
   private Text valueLabel;
   private Composite parent;

   public XLabelDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Control getControl() {
      return valueLabel;
   }

   public void createWidgets(Composite parent, int horizontalSpan) {

      this.parent = parent;
      if (horizontalSpan < 2) horizontalSpan = 2;
      // Create Data Widgets
      if (!label.equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }
      valueLabel = new Text(parent, SWT.NONE);
      valueLabel.setText(data);
      valueLabel.setEditable(false);

      refresh();
   }

   public void setArtifact(Artifact artifact, String attrName) {
      this.artifact = artifact;
      this.attrName = attrName;

      refresh();
   }

   @Override
   public void save() {
   }

   @Override
   public boolean isDirty() {
      return false;
   }

   public void refresh() {
      if (artifact != null && valueLabel != null && !valueLabel.isDisposed()) {
         try {
            valueLabel.setText(artifact.getAttributesToString(attrName));
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
      }
   }

   public void dispose() {
      if (labelWidget != null) labelWidget.dispose();
      if (valueLabel != null) valueLabel.dispose();
      if (parent != null && !parent.isDisposed()) parent.layout();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getReportData()
    */
   @Override
   public String getReportData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getXmlData()
    */
   @Override
   public String getXmlData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#isValid()
    */
   @Override
   public boolean isValid() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#setFocus()
    */
   @Override
   public void setFocus() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#setXmlData(java.lang.String)
    */
   @Override
   public void setXmlData(String str) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#toHTML(java.lang.String)
    */
   @Override
   public String toHTML(String labelFont) {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return valueLabel.getText();
   }

}
