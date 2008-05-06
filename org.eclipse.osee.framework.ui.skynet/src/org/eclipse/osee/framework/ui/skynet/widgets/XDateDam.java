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
import java.util.Date;
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

   private DateAttribute getAttribute() throws Exception {
      return (DateAttribute) getAttributeManager().getSoleAttribute();
   }

   private DynamicAttributeManager getAttributeManager() throws Exception {
      return artifact.getAttributeManager(attrName);
   }

   public String getUdatStringValue() {
      String toReturn = null;
      try {
         toReturn = getAttribute().getAsFormattedString(DateAttribute.MMDDYY);
      } catch (Exception ex) {
      }
      return toReturn != null ? toReturn : "";
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
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   };

   @Override
   public void save() throws Exception {
      if (isDirty()) {
         if (get().equals("")) {
            DynamicAttributeManager attrManager = getAttributeManager();
            if (attrManager != null) {
               for (Attribute attr : attrManager.getAttributes()) {
                  attr.delete();
               }
            }
         } else {
            artifact.setSoleDateAttributeValue(attrName, getDate());
         }
      }
   }

   public void setArtifact(Artifact artifact, String attrName) throws SQLException {
      this.artifact = artifact;
      this.attrName = attrName;

      if (!getUdatStringValue().equals("")) {
         Date date = null;
         try {
            date = getAttribute().getValue();
         } catch (Exception ex) {
            // Do Nothing
         }
         super.setDate(date);
      }

      this.addModifyListener(new ModifyListener() {
         public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
            try {
               String date = get(DateAttribute.MMDDYY);
               if (date.equals("")) {
                  DynamicAttributeManager udat = getAttributeManager();
                  if (udat != null) {
                     if (udat.getAttributes().size() > 0) {
                        udat.removeAttribute(udat.getAttributes().iterator().next());
                     }
                  }
               } else {
                  getAttribute().setValue(getDate());
               }
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         };
      });
   }

   @Override
   public boolean isDirty() throws Exception {
      if (getDate() == null && getUdatStringValue().equals("")) return false;
      if (getDate() == null && !getUdatStringValue().equals("")) return true;
      if (getDate() == null && getAttribute() != null && getAttribute().getValue() != null) return true;
      return getAttribute() != null && getDate().equals(getAttribute().getValue());
   }

}
