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

import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
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
public class XLabelDam extends XWidget implements IArtifactWidget {

   private Artifact artifact;
   private String attributeTypeName;
   private Text valueTextWidget;
   private Composite parent;

   public XLabelDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Control getControl() {
      return valueTextWidget;
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
      valueTextWidget = new Text(parent, SWT.NONE);
      valueTextWidget.setEditable(false);

      refresh();
   }

   public void setArtifact(Artifact artifact, String attrName) {
      this.artifact = artifact;
      this.attributeTypeName = attrName;

      refresh();
   }

   @Override
   public void saveToArtifact() {
      // Do nothing cause labelDam is read-only
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   public void refresh() {
      if (artifact != null && valueTextWidget != null && !valueTextWidget.isDisposed()) {
         try {
            valueTextWidget.setText(artifact.getAttributesToString(attributeTypeName));
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   public void dispose() {
      if (labelWidget != null) labelWidget.dispose();
      if (valueTextWidget != null) valueTextWidget.dispose();
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
   public Result isValid() {
      return Result.TrueResult;
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
      return valueTextWidget.getText();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#revert()
    */
   @Override
   public void revert() {
      // Do nothing cause labelDam is read-only
   }

}
