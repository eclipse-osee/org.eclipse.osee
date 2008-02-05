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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizeDefaults;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.XViewerCustomize;

/**
 * @author Donald G. Dunne
 */
public class SkynetCustomizations implements IXViewerCustomizations {

   private Artifact globalCustomizationsArtifact;
   private List<CustomizeData> custDatas = new ArrayList<CustomizeData>();
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(XViewerCustomize.class);
   private final IXViewerCustomizeDefaults xViewerDefaults;
   private static String CUSTOMIZATION_ATTRIBUTE_NAME = "XViewer Customization";
   private final XViewer xViewer;

   public SkynetCustomizations(XViewer xViewer, IXViewerCustomizeDefaults xViewerDefaults) {
      this.xViewer = xViewer;
      this.xViewerDefaults = xViewerDefaults;
      try {
         globalCustomizationsArtifact = XViewerCustomizationArtifact.getAtsCustArtifact();
      } catch (Throwable ex) {
         logger.log(Level.SEVERE, "Unable to get the ATS Custom Artifact", ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#getCustDatas()
    */
   public List<CustomizeData> getCustDatas() {
      loadCustomizationData();
      return custDatas;
   }

   private static void saveCustomization(CustomizeData custData, Artifact saveArt) throws SQLException {
      boolean found = false;
      DynamicAttributeManager dam = saveArt.getAttributeManager(CUSTOMIZATION_ATTRIBUTE_NAME);
      for (Attribute attr : dam.getAttributes()) {
         if (attr.getStringData().contains("namespace=\"" + custData.getNameSpace() + "\"") && attr.getStringData().contains(
               "name=\"" + custData.getName() + "\"")) {
            attr.setStringData(custData.getXml());
            found = true;
            break;
         }
      }
      if (!found) dam.getNewAttribute().setStringData(custData.getXml());
      saveArt.persistAttributes();
   }

   public void saveCustomization(CustomizeData custData) throws SQLException {
      if (custData.isPersonal())
         saveCustomization(custData, SkynetAuthentication.getInstance().getAuthenticatedUser());
      else
         saveCustomization(custData, globalCustomizationsArtifact);
   }

   public void loadCustomizationData() {
      custDatas.clear();
      User user = SkynetAuthentication.getInstance().getAuthenticatedUser();
      if (user != null) custDatas.addAll(getArtifactCustomizations(user));
      for (CustomizeData custData : custDatas)
         custData.setPersonal(true);
      custDatas.addAll(getArtifactCustomizations(getGlobalCustomizationsArtifact()));
   }

   /**
    * @return Returns the defaultCustomizationsArtifact.
    */
   public Artifact getGlobalCustomizationsArtifact() {
      return globalCustomizationsArtifact;
   }

   /**
    * Artifact that holds default customizations for this XTreeViewer. These will be selectable to everyone, but only
    * writable to Developer/AtsAdmin. Users will be able to save their own customizations separately. If no defaults are
    * necessary, don't set this artifact upon creation.
    * 
    * @param defaultCustomizationsArtifact The defaultCustomizationsArtifact to set.
    */
   public void setGlobalCustomizationsArtifact(Artifact defaultCustomizationsArtifact) {
      this.globalCustomizationsArtifact = defaultCustomizationsArtifact;
   }

   public void deleteCustomization(CustomizeData custData) throws SQLException {
      Artifact deleteArt = null;
      if (custData.isPersonal())
         deleteArt = SkynetAuthentication.getInstance().getAuthenticatedUser();
      else
         deleteArt = getGlobalCustomizationsArtifact();
      deleteCustomization(custData, deleteArt);
      // Remove item as default if set
      if (xViewerDefaults.isDefaultCustomization(custData)) {
         xViewerDefaults.removeDefaultCustomization(custData);
         xViewerDefaults.save();
      }

   }

   public void deleteCustomization(CustomizeData custData, Artifact deleteArt) throws SQLException {
      DynamicAttributeManager dam = deleteArt.getAttributeManager(CUSTOMIZATION_ATTRIBUTE_NAME);
      for (Attribute attr : dam.getAttributes()) {
         String str = attr.getStringData();
         Matcher m = Pattern.compile("name=\"(.*?)\".*?namespace=\"" + custData.getNameSpace() + "\"").matcher(str);
         if (m.find() && m.group(1).equals(custData.getName())) {
            attr.delete();
            deleteArt.persistAttributes();
            break;
         }
      }
   }

   public CustomizeData getUserDefaultCustData() {
      for (CustomizeData custData : getCustDatas()) {
         if (xViewerDefaults.isDefaultCustomization(custData)) return custData;
      }
      return null;
   }

   public boolean isCustomizationUserDefault(CustomizeData custData) {
      return (getUserDefaultCustData() != null && getUserDefaultCustData().getGuid().equals(custData.getGuid()));
   }

   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) {
      // Remove old defaults
      for (CustomizeData custData : getCustDatas()) {
         if (xViewerDefaults.isDefaultCustomization(custData)) {
            xViewerDefaults.removeDefaultCustomization(custData);
         }
      }
      // Add new default
      if (set) xViewerDefaults.setDefaultCustomization(newCustData);
      // persist
      xViewerDefaults.save();
   }

   private List<CustomizeData> getArtifactCustomizations(Artifact customizationArtifact) {
      List<CustomizeData> custDatas = new ArrayList<CustomizeData>();
      if (customizationArtifact != null) {

         try {
            DynamicAttributeManager dam = customizationArtifact.getAttributeManager(CUSTOMIZATION_ATTRIBUTE_NAME);
            for (Attribute attr : dam.getAttributes()) {
               String str = attr.getStringData();
               Matcher m =
                     Pattern.compile("name=\"(.*?)\".*?namespace=\"" + xViewer.getViewerNamespace() + "\"").matcher(str);
               if (m.find()) {
                  CustomizeData custData = new CustomizeData(str, xViewer.getXViewerFactory());
                  custDatas.add(custData);
               }
            }
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
      }
      return custDatas;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#isCustomizationPersistAvailable()
    */
   public boolean isCustomizationPersistAvailable() {
      return true;
   }

}
