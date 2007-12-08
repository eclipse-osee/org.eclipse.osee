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
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

public class XDateDam extends XDate implements IDamWidget {

   private Artifact artifact;
   private String attrName;

   public XDateDam(String displayLabel) {
      super(displayLabel);
   }

   public DynamicAttributeManager getUdat() throws SQLException {
      return artifact.getAttributeManager(attrName);
   }

   public String getUdatStringFormattedValue() throws SQLException {
      DynamicAttributeManager udat = getUdat();
      if (udat == null) return "";
      String dateStr = getUdatStringValue();
      if (!dateStr.equals("")) return ((DateAttribute) udat.getSoleAttribute()).getStringValue(DateAttribute.MMDDYY);
      return "";
   }

   public String getUdatStringValue() throws SQLException {
      DynamicAttributeManager udat = getUdat();
      if (udat == null) return "";
      return udat.getSoleAttributeValue();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XDate#createWidgets(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {
      super.createWidgets(parent, horizontalSpan);
      super.addXModifiedListener(modifyListener);
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
         if (get().equals("")) {
            if (getUdat() != null) {
               for (Attribute attr : getUdat().getAttributes())
                  attr.delete();
            }
         } else {
            DynamicAttributeManager udat = getUdat();
            udat.setSoleAttributeValue(getDate().getTime() + "");
         }
      }
   }

   public void setArtifact(Artifact artifact, String attrName) throws SQLException {
      this.artifact = artifact;
      this.attrName = attrName;

      if (!getUdatStringValue().equals("")) {
         DynamicAttributeManager udat = getUdat();
         if (udat == null)
            super.setDate(null);
         else
            super.setDate(((DateAttribute) udat.getSoleAttribute()).getDate());
      }

      this.addModifyListener(new ModifyListener() {
         public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
            try {
               String date = get(DateAttribute.MMDDYY);
               if (date.equals("")) {
                  DynamicAttributeManager udat = getUdat();
                  if (udat != null) {
                     if (udat.getAttributes().size() > 0) {
                        udat.removeAttribute(udat.getAttributes().iterator().next());
                     }
                  }
               } else {
                  getUdat().setSoleAttributeValue(getDate().getTime() + "");
               }
            } catch (SQLException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         };
      });
   }

   @Override
   public boolean isDirty() throws SQLException {
      // System.out.println("Stored "+getUdatStringValue()+" Selected "+getDate().getTime());
      if (getDate() == null && getUdatStringValue().equals("")) return false;
      if (getDate() == null && !getUdatStringValue().equals("")) return true;
      return (!getUdatStringValue().equals(getDate().getTime() + ""));
   }

}
