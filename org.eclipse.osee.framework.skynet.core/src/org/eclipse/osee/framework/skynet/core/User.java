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

package org.eclipse.osee.framework.skynet.core;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;

/**
 * @author Donald G. Dunne
 */
public class User extends Artifact implements Serializable {
   private static final long serialVersionUID = 834749078806388387L;
   public static final String userIdAttributeName = "User Id";

   public static enum Attributes {
      Phone, Email, Active, Policy
   };

   private PropertyStore userSettings;

   @Override
   public void onBirth() throws OseeCoreException {
      super.onBirth();
      SystemGroup.Everyone.addMember(this);
   }

   public User(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public void setFieldsBasedon(User u) throws Exception {
      setName(u.getName());
      setPhone(u.getPhone());
      setEmail(u.getEmail());
      setUserID(u.getUserId());
      setActive(u.isActive());
   }

   @Override
   public String toString() {
      try {
         return String.format("%s (%s)", getName(), getUserId());
      } catch (Exception ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   public boolean isMe() {
      try {
         return getUserId().equals(UserManager.getUser().getUserId());
      } catch (Exception ex) {
         return false;
      }
   }

   public String getUserId() throws OseeCoreException {
      return getSoleAttributeValue(userIdAttributeName, "");
   }

   public void setUserID(String userId) throws OseeCoreException {
      setSoleAttributeValue(userIdAttributeName, userId);
   }

   public String getEmail() throws OseeCoreException {
      return getSoleAttributeValue(Attributes.Email.toString(), "");
   }

   public void setEmail(String email) throws OseeCoreException {
      setSoleAttributeValue(Attributes.Email.toString(), email);
   }

   public String getPhone() throws OseeCoreException {
      return getSoleAttributeValue(Attributes.Phone.toString(), "");
   }

   public void setPhone(String phone) throws OseeCoreException {
      setSoleAttributeValue(Attributes.Phone.toString(), phone);
   }

   public Boolean isActive() throws OseeCoreException {
      return getSoleAttributeValue(Attributes.Active.toString());
   }

   public void setActive(boolean active) throws OseeCoreException {
      setSoleAttributeValue(Attributes.Active.toString(), active);
   }

   /**
    * @param favoriteBranch
    * @throws OseeCoreException
    */
   public void toggleFavoriteBranch(Branch favoriteBranch) throws OseeCoreException {
      HashSet<String> branchGuids = new HashSet<String>();
      for (Branch branch : BranchManager.getNormalBranches()) {
         branchGuids.add(branch.getGuid());
      }

      boolean found = false;
      Collection<Attribute<String>> attributes = getAttributes(CoreAttributes.FAVORITE_BRANCH.getName());
      for (Attribute<String> attribute : attributes) {
         // Remove attributes that are no longer valid
         if (!branchGuids.contains(attribute.getValue())) {
            attribute.delete();
         } else if (favoriteBranch.getGuid().equals(attribute.getValue())) {
            attribute.delete();
            found = true;
            break;
         }
      }

      if (!found) {
         addAttribute(CoreAttributes.FAVORITE_BRANCH.getName(), favoriteBranch.getGuid());
      }
      setSetting(CoreAttributes.FAVORITE_BRANCH.getName(), favoriteBranch.getGuid());
   }

   public boolean isFavoriteBranch(Branch branch) throws OseeCoreException {
      Collection<Attribute<String>> attributes = getAttributes(CoreAttributes.FAVORITE_BRANCH.getName());
      for (Attribute<String> attribute : attributes) {
         if (branch.getGuid().equals(attribute.getValue())) {
            return true;
         }
      }
      return false;
   }

   public String getSetting(String key) throws OseeCoreException {
      ensureUserSettingsAreLoaded();
      return userSettings.get(key);
   }

   public boolean getBooleanSetting(String key) throws OseeCoreException {
      return Boolean.parseBoolean(getSetting(key));
   }

   public void setSetting(String key, String value) throws OseeCoreException {
      ensureUserSettingsAreLoaded();
      userSettings.put(key, value);

   }

   public void saveSettings() throws OseeCoreException {
      if (userSettings != null) {
         StringWriter stringWriter = new StringWriter();
         try {
            userSettings.save(stringWriter);
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
         setSoleAttributeFromString("User Settings", stringWriter.toString());
         persist();
      }
   }

   private void ensureUserSettingsAreLoaded() throws OseeWrappedException {
      if (userSettings == null) {
         PropertyStore store = new PropertyStore(getGuid());
         try {
            String settings = getSoleAttributeValue("User Settings", null);
            if (settings != null) {
               store.load(new StringReader(settings));
            }
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
         userSettings = store;
      }
   }

   public boolean isSystemUser() throws OseeCoreException {
      if (this.equals(UserManager.getUser(SystemUser.OseeSystem)) || this.equals(UserManager.getUser(SystemUser.UnAssigned)) || this.equals(UserManager.getUser(SystemUser.Guest))) {
         return true;
      }
      return false;
   }

}
