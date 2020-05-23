/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * This class provides the functionality necessary to store the user's defaults in their User Artifact
 * 
 * @author Donald G. Dunne
 */
public class SkynetUserArtifactCustomizeDefaults {
   Set<String> defaultGuids = new HashSet<>();
   private static String DEFAULT_CUST_GUID_TAG = "defaultCustGuid";
   private final User user;

   public SkynetUserArtifactCustomizeDefaults(User user) {
      this.user = user;
      loadCustomizeDefaults();
   }

   public int size() {
      return defaultGuids.size();
   }

   private static Pattern pattern =
      Pattern.compile("<" + DEFAULT_CUST_GUID_TAG + ">(.*?)</" + DEFAULT_CUST_GUID_TAG + ">");

   private void setDefaultCustomizationsFromXml(String xml) {
      defaultGuids.clear();
      Matcher m = pattern.matcher(xml);
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
            xml = user.getSoleAttributeValue(CoreAttributeTypes.XViewerDefaults, "");
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            xml = "";
         }
      }
      setDefaultCustomizationsFromXml(xml);
   }

   public void save() {
      try {
         if (defaultGuids.isEmpty()) {
            user.deleteSoleAttribute(CoreAttributeTypes.XViewerDefaults);
         } else {
            user.setSoleAttributeValue(CoreAttributeTypes.XViewerDefaults, getDefaultCustomizationXml());
         }
         user.persist(getClass().getSimpleName());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
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
      for (String guid : defaultGuids) {
         sb.append(AXml.addTagData(DEFAULT_CUST_GUID_TAG, guid));
      }
      return sb.toString();
   }

   public boolean isSaveDefaultsEnabled() {
      return user != null;
   }

}