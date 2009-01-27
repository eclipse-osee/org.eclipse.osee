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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.db.connection.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * This class provides the functionality necessary to store the user's defaults in their User Artifact
 * 
 * @author Donald G. Dunne
 */
public class SkynetUserArtifactCustomizeDefaults {

   // XViewer.getViewerNamespace, CustomizeData.getName
   Set<String> defaultGuids = new HashSet<String>();
   private static String XVIEWER_DEFAULT_ATTRIBUTE = "XViewer Defaults";
   private static String DEFAULT_CUST_GUID_TAG = "defaultCustGuid";
   private final User user;

   public SkynetUserArtifactCustomizeDefaults(User user) {
      this.user = user;
      loadCustomizeDefaults();
   }

   public int size() {
      return defaultGuids.size();
   }

   private void setDefaultCustomizationsFromXml(String xml) {
      defaultGuids.clear();
      Matcher m = Pattern.compile("<" + DEFAULT_CUST_GUID_TAG + ">(.*?)</" + DEFAULT_CUST_GUID_TAG + ">").matcher(xml);
      while (m.find()) {
         defaultGuids.add(m.group(1));
      }
   }

   public void setDefaultCustomization(CustomizeData custData) {
      defaultGuids.add(custData.getGuid());
   }

   public void removeDefaultCustomization(CustomizeData custData) {
      defaultGuids.remove(custData.getGuid());
   }

   public boolean isDefaultCustomization(CustomizeData custData) {
      return defaultGuids.contains(custData.getGuid());
   }

   private void loadCustomizeDefaults() {
      String xml = "";
      if (user != null) {
         try {
            xml = user.getSoleAttributeValue(XVIEWER_DEFAULT_ATTRIBUTE);
            if (xml == null) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "Invalid null for XViewerDefaults for user " + user);
               xml = "";
            }
         } catch (AttributeDoesNotExist ex) {
            xml = "";
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            xml = "";
         }
      }
      setDefaultCustomizationsFromXml(xml);
   }

   public void save() {
      try {
         if (defaultGuids.size() == 0) {
            user.deleteSoleAttribute(XVIEWER_DEFAULT_ATTRIBUTE);
         } else {
            user.setSoleAttributeValue(XVIEWER_DEFAULT_ATTRIBUTE, getDefaultCustomizationXml());
         }
         user.persistAttributes();
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public Set<String> getGuids() {
      return defaultGuids;
   }

   public void setGuids(Collection<String> defaultGuids) {
      this.defaultGuids.clear();
      this.defaultGuids.addAll(defaultGuids);
   }

   private String getDefaultCustomizationXml() {
      StringBuffer sb = new StringBuffer();
      for (String guid : defaultGuids)
         sb.append(AXml.addTagData(DEFAULT_CUST_GUID_TAG, guid));
      return sb.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizeDefaults#isSaveDefaultsEnabled()
    */
   public boolean isSaveDefaultsEnabled() {
      return user != null;
   }

}