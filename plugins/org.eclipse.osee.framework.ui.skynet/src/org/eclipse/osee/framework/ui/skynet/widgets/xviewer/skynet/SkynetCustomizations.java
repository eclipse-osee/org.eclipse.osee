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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.GlobalXViewerSettings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class SkynetCustomizations implements IXViewerCustomizations, IArtifactEventListener {

   // Artifact that stores shared/global customizations
   private static Artifact globalCustomizationsArtifact;
   // Collection of all customizations both from local and global storage
   private static List<CustomizeData> custDatas;
   // Storage mechanism (user's User Artifact) for storage of selected default customizations guids for each XViewer namespace
   private final SkynetUserArtifactCustomizeDefaults userArtifactDefaults;
   // Attribute name for storing customizations both locally and globally
   private final SkynetXViewerFactory skynetXViewerFactory;
   private static SkynetCustomizations instance = new SkynetCustomizations();

   /**
    * Constructor for events only
    */
   private SkynetCustomizations() {
      OseeEventManager.addListener(this);
      userArtifactDefaults = null;
      skynetXViewerFactory = null;
   }

   public SkynetCustomizations(SkynetXViewerFactory skynetXViewerFactory) {
      this.skynetXViewerFactory = skynetXViewerFactory;
      this.userArtifactDefaults = new SkynetUserArtifactCustomizeDefaults(UserManager.getUser());
      globalCustomizationsArtifact = GlobalXViewerSettings.getCustomArtifact();
   }

   @Override
   public List<CustomizeData> getSavedCustDatas() {
      ensurePopulated(false);
      List<CustomizeData> thisCustDatas = new ArrayList<>();
      for (CustomizeData custData : custDatas) {
         if (custData.getNameSpace().equals(this.skynetXViewerFactory.getNamespace())) {
            thisCustDatas.add(custData);
         }
      }
      return thisCustDatas;
   }

   private static void saveCustomization(CustomizeData custData, Artifact saveArt) {
      boolean found = false;
      Collection<Attribute<String>> attributes = saveArt.getAttributes(CoreAttributeTypes.XViewerCustomization);
      for (Attribute<String> attribute : attributes) {
         if (attribute.getDisplayableString().contains(
            "namespace=\"" + custData.getNameSpace() + "\"") && attribute.getDisplayableString().contains(
               "name=\"" + custData.getName() + "\"")) {
            attribute.setValue(custData.getXml(true));
            found = true;
            break;
         }
      }
      if (!found) {
         saveArt.addAttribute(CoreAttributeTypes.XViewerCustomization, custData.getXml(true));
      }
      saveArt.persist(SkynetCustomizations.class.getSimpleName());
   }

   @Override
   public void saveCustomization(CustomizeData custData) {
      if (custData.isPersonal()) {
         saveCustomization(custData, UserManager.getUser());
      } else {
         saveCustomization(custData, globalCustomizationsArtifact);
      }
      ensurePopulated(true);
   }

   /**
    * Load and cache all customizations
    */
   public static synchronized void ensurePopulated(boolean force) {
      if (custDatas == null || force) {
         if (custDatas == null) {
            custDatas = Collections.synchronizedList(new ArrayList<CustomizeData>());
         }
         if (force) {
            custDatas.clear();
         }
         User user = UserManager.getUser();
         if (user != null) {
            for (CustomizeData custData : getArtifactCustomizations(user)) {
               custData.setPersonal(true);
               custDatas.add(custData);
            }
         }
         custDatas.addAll(getArtifactCustomizations(getGlobalCustomizationsArtifact()));
      }
   }

   public static Artifact getGlobalCustomizationsArtifact() {
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
      SkynetCustomizations.globalCustomizationsArtifact = defaultCustomizationsArtifact;
   }

   @Override
   public void deleteCustomization(CustomizeData custData) {
      Artifact deleteArt = null;
      if (custData.isPersonal()) {
         deleteArt = UserManager.getUser();
      } else {
         deleteArt = getGlobalCustomizationsArtifact();
      }
      deleteCustomization(custData, deleteArt);
      // Remove item as default if set
      if (userArtifactDefaults.isDefaultCustomization(custData)) {
         userArtifactDefaults.removeDefaultCustomization(custData);
         userArtifactDefaults.save();
      }

   }

   public void deleteCustomization(CustomizeData custData, Artifact deleteArt) {
      Pattern pattern = Pattern.compile("name=\"(.*?)\".*?namespace=\"" + custData.getNameSpace() + "\"");
      for (Attribute<?> attribute : deleteArt.getAttributes(CoreAttributeTypes.XViewerCustomization)) {
         String str = attribute.getDisplayableString();
         Matcher m = pattern.matcher(str);
         if (m.find() && m.group(1).equals(custData.getName())) {
            attribute.delete();
            deleteArt.persist(getClass().getSimpleName());
            break;
         }
      }
   }

   @Override
   public CustomizeData getUserDefaultCustData() throws XViewerException {
      try {
         for (CustomizeData custData : getSavedCustDatas()) {
            if (userArtifactDefaults.isDefaultCustomization(custData)) {
               return custData;
            }
         }
         return null;
      } catch (OseeCoreException ex) {
         throw new XViewerException(ex);
      }
   }

   @Override
   public boolean isCustomizationUserDefault(CustomizeData custData) {
      try {
         return getUserDefaultCustData() != null && getUserDefaultCustData().getGuid().equals(custData.getGuid());
      } catch (Exception ex) {
         return false;
      }
   }

   @Override
   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) throws Exception {
      // Remove old defaults
      for (CustomizeData custData : getSavedCustDatas()) {
         if (userArtifactDefaults.isDefaultCustomization(custData)) {
            userArtifactDefaults.removeDefaultCustomization(custData);
         }
      }
      // Add new default
      if (set) {
         userArtifactDefaults.setDefaultCustomization(newCustData);
      }
      // persist
      userArtifactDefaults.save();
   }

   private static List<CustomizeData> getArtifactCustomizations(Artifact customizationArtifact) {
      //      ElapsedTime time = new ElapsedTime("getArtifactCustomizations");
      List<CustomizeData> custDatas = new ArrayList<>();
      if (customizationArtifact != null) {

         Collection<Attribute<String>> attributes =
            customizationArtifact.getAttributes(CoreAttributeTypes.XViewerCustomization);
         for (Attribute<String> attr : attributes) {
            CustomizeData custData = new CustomizeData(attr.getValue());
            custDatas.add(custData);
         }
      }
      //      time.end();
      return custDatas;
   }

   @Override
   public boolean isCustomizationPersistAvailable() {
      return true;
   }

   public static SkynetCustomizations getInstance() {
      return instance;
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(EventUtil.getCommonBranchFilter());
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      final Collection<Artifact> modifiedArts =
         artifactEvent.getCacheArtifacts(EventModType.Modified, EventModType.Reloaded);
      try {
         if (!modifiedArts.isEmpty()) {
            if (modifiedArts.contains(getGlobalCustomizationsArtifact()) || modifiedArts.contains(
               UserManager.getUser())) {
               ensurePopulated(true);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

}
