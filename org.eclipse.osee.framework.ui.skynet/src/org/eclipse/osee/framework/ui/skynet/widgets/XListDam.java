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
import java.util.ArrayList;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XListDam extends XList implements IDamWidget {

   private Artifact artifact;
   private String attrName;

   /**
    * @param displayLabel
    */
   public XListDam(String displayLabel) {
      super(displayLabel);
   }

   public DynamicAttributeManager getUdat() throws SQLException {
      return artifact.getAttributeManager(attrName);
   }

   public void setArtifact(Artifact artifact, String attrName) {
      this.artifact = artifact;
      this.attrName = attrName;

      setSelected(getStoredNames());
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
         ArrayList<String> selectedNames = new ArrayList<String>();
         for (XListItem item : getSelected())
            selectedNames.add(item.getName());

         artifact.setDamAttributes(attrName, selectedNames);
      }
   }

   public ArrayList<String> getStoredNames() {
      ArrayList<String> storedNames = new ArrayList<String>();
      try {
         for (Attribute attr : getUdat().getAttributes()) {
            storedNames.add(attr.getStringData());
         }
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      return storedNames;
   }

   public String getStoredStr() {
      StringBuffer sb = new StringBuffer();
      for (String item : getStoredNames())
         sb.append(item + ", ");
      return sb.toString().replaceFirst(", $", "");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XList#createWidgets(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {
      super.createWidgets(parent, horizontalSpan);
      super.addXModifiedListener(modifyListener);
   }

   @Override
   public boolean isDirty() {
      return (!getStoredStr().equals(getSelectedStr()));
   }

}
