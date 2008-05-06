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
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Composite;

public class XIntegerDam extends XInteger implements IDamWidget {

   private int minValue = 0;
   private boolean minValueSet = false;
   private int maxValue = 0;
   private boolean maxValueSet = false;
   private Artifact artifact;
   private String attrName;

   public XIntegerDam(String displayLabel) {
      super(displayLabel);
   }

   private Attribute<Integer> getAttribute() throws Exception {
      return artifact.getSoleAttributeValue(attrName);
   }

   public void setArtifact(Artifact artifact, String attrName) throws SQLException {
      this.artifact = artifact;
      this.attrName = attrName;

      super.set(getUdatStringValue());
   }

   private void setFromString(String value) {
      try {
         getAttribute().setValue(new Integer(text));
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   @Override
   public void set(String text) {
      super.set(text);
      setFromString(text);
   }

   public String getUdatStringValue() throws SQLException {
      String toReturn = null;
      try {
         toReturn = getAttribute().toString();
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      return toReturn != null ? toReturn : "";
   }

   public void setMinValue(int minValue) {
      minValueSet = true;
      this.minValue = minValue;
   }

   @Override
   public boolean isDirty() throws SQLException {
      return (!getUdatStringValue().equals(get()));
   }

   XModifiedListener modifyListener = new XModifiedListener() {
      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener#widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget)
       */
      public void widgetModified(XWidget widget) {
         try {
            save();
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   };

   @Override
   public void save() throws SQLException {
      if (isDirty()) {
         setFromString(get());
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XText#createWidgets(org.eclipse.swt.widgets.Composite, int, boolean)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan, boolean fillText) {
      super.createWidgets(parent, horizontalSpan, fillText);
      super.addXModifiedListener(modifyListener);
   }

   public void setMaxValue(int maxValue) {
      maxValueSet = false;
      this.maxValue = maxValue;
   }

   public boolean isValid() {
      return isValidResult().isTrue();
   }

   public Result isValidResult() {
      if (super.requiredEntry() || (super.get().compareTo("") != 0)) {
         if (!this.isInteger()) {
            return new Result("Must be an Integer");
         } else if (minValueSet && (this.getInteger() < minValue)) {
            return new Result("Must be >= " + minValue);
         } else if (maxValueSet && (this.getInteger() > maxValue)) {
            return new Result("Must be <= " + maxValue);
         }
      }
      return Result.TrueResult;
   }

}
