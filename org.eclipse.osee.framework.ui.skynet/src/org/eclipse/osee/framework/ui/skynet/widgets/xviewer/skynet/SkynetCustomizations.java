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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations;

/**
 * @author Donald G. Dunne
 */
public class SkynetCustomizations implements IXViewerCustomizations {

   // Artifact that stores shared/global customizations
   private Artifact globalCustomizationsArtifact;
   // Collection of all customizations both from local and global storage
   private final List<CustomizeData> custDatas = new ArrayList<CustomizeData>();
   // Storage mechanism (user's User Artifact) for storage of selected default customizations guids for each XViewer namespace
   private final SkynetUserArtifactCustomizeDefaults userArtifactDefaults;
   // Attribute name for storing customizations both locally and globally
   private static String CUSTOMIZATION_ATTRIBUTE_NAME = "XViewer Customization";
   private final SkynetXViewerFactory skynetXViewerFactory;

   public SkynetCustomizations(SkynetXViewerFactory skynetXViewerFactory) throws OseeCoreException {
      this.skynetXViewerFactory = skynetXViewerFactory;
      this.userArtifactDefaults = new SkynetUserArtifactCustomizeDefaults(UserManager.getUser());
      globalCustomizationsArtifact = XViewerCustomizationArtifact.getAtsCustArtifact();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#getCustDatas()
    */
   public List<CustomizeData> getSavedCustDatas() throws OseeCoreException {
      loadCustomizationData();
      return custDatas;
   }

   private static void saveCustomization(CustomizeData custData, Artifact saveArt) throws OseeCoreException {
      boolean found = false;
      Collection<Attribute<String>> attributes = saveArt.getAttributes(CUSTOMIZATION_ATTRIBUTE_NAME);
      for (Attribute<String> attribute : attributes) {
         if (attribute.getDisplayableString().contains("namespace=\"" + custData.getNameSpace() + "\"") && attribute.getDisplayableString().contains(
               "name=\"" + custData.getName() + "\"")) {
            attribute.setValue(custData.getXml(true));
            found = true;
            break;
         }
      }
      if (!found) {
         saveArt.addAttribute(CUSTOMIZATION_ATTRIBUTE_NAME, custData.getXml(true));
      }
      saveArt.persistAttributes();
   }

   public void saveCustomization(CustomizeData custData) throws OseeCoreException {
      if (custData.isPersonal())
         saveCustomization(custData, UserManager.getUser());
      else
         saveCustomization(custData, globalCustomizationsArtifact);
   }

   public void loadCustomizationData() throws OseeCoreException {
      custDatas.clear();
      User user = UserManager.getUser();
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

   public void deleteCustomization(CustomizeData custData) throws OseeCoreException {
      Artifact deleteArt = null;
      if (custData.isPersonal())
         deleteArt = UserManager.getUser();
      else
         deleteArt = getGlobalCustomizationsArtifact();
      deleteCustomization(custData, deleteArt);
      // Remove item as default if set
      if (userArtifactDefaults.isDefaultCustomization(custData)) {
         userArtifactDefaults.removeDefaultCustomization(custData);
         userArtifactDefaults.save();
      }

   }

   public void deleteCustomization(CustomizeData custData, Artifact deleteArt) throws OseeCoreException {
      Pattern pattern = Pattern.compile("name=\"(.*?)\".*?namespace=\"" + custData.getNameSpace() + "\"");
      for (Attribute<?> attribute : deleteArt.getAttributes(CUSTOMIZATION_ATTRIBUTE_NAME)) {
         String str = attribute.getDisplayableString();
         Matcher m = pattern.matcher(str);
         if (m.find() && m.group(1).equals(custData.getName())) {
            attribute.delete();
            deleteArt.persistAttributes();
            break;
         }
      }
   }

   public CustomizeData getUserDefaultCustData() throws OseeCoreException {
      for (CustomizeData custData : getSavedCustDatas()) {
         if (userArtifactDefaults.isDefaultCustomization(custData)) {
            return custData;
         }
      }
      return null;
   }

   public boolean isCustomizationUserDefault(CustomizeData custData) {
      try {
         return (getUserDefaultCustData() != null && getUserDefaultCustData().getGuid().equals(custData.getGuid()));
      } catch (Exception ex) {
         return false;
      }
   }

   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) throws Exception {
      // Remove old defaults
      for (CustomizeData custData : getSavedCustDatas()) {
         if (userArtifactDefaults.isDefaultCustomization(custData)) {
            userArtifactDefaults.removeDefaultCustomization(custData);
         }
      }
      // Add new default
      if (set) userArtifactDefaults.setDefaultCustomization(newCustData);
      // persist
      userArtifactDefaults.save();
   }

   private List<CustomizeData> getArtifactCustomizations(Artifact customizationArtifact) throws OseeCoreException {
      List<CustomizeData> custDatas = new ArrayList<CustomizeData>();
      if (customizationArtifact != null) {

         Collection<Attribute<String>> attributes = customizationArtifact.getAttributes(CUSTOMIZATION_ATTRIBUTE_NAME);
         for (Attribute<String> attr : attributes) {
            String str = attr.getValue();
            Matcher m =
                  Pattern.compile("name=\"(.*?)\".*?namespace=\"" + skynetXViewerFactory.getNamespace() + "\"").matcher(
                        str);
            if (m.find()) {
               CustomizeData custData = new CustomizeData(str);
               custDatas.add(custData);
            }
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
