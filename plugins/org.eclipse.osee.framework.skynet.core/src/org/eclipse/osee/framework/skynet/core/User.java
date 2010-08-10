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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class User extends Artifact {
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
      return getSoleAttributeValue(CoreAttributeTypes.UserId, "");
   }

   public void setUserID(String userId) throws OseeCoreException {
      setSoleAttributeValue(CoreAttributeTypes.UserId, userId);
   }

   public String getEmail() throws OseeCoreException {
      return getSoleAttributeValue(CoreAttributeTypes.Email, "");
   }

   public void setEmail(String email) throws OseeCoreException {
      setSoleAttributeValue(CoreAttributeTypes.Email, email);
   }

   public String getPhone() throws OseeCoreException {
      return getSoleAttributeValue(CoreAttributeTypes.Phone, "");
   }

   public void setPhone(String phone) throws OseeCoreException {
      setSoleAttributeValue(CoreAttributeTypes.Phone, phone);
   }

   public Boolean isActive() throws OseeCoreException {
      return getSoleAttributeValue(CoreAttributeTypes.Active);
   }

   public void setActive(boolean active) throws OseeCoreException {
      setSoleAttributeValue(CoreAttributeTypes.Active, active);
   }

   public void toggleFavoriteBranch(Branch favoriteBranch) throws OseeCoreException {
      HashSet<String> branchGuids = new HashSet<String>();
      for (Branch branch : BranchManager.getBranches(BranchArchivedState.UNARCHIVED, BranchType.WORKING,
         BranchType.BASELINE)) {
         branchGuids.add(branch.getGuid());
      }

      boolean found = false;
      Collection<Attribute<String>> attributes = getAttributes(CoreAttributeTypes.FavoriteBranch);
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
         addAttribute(CoreAttributeTypes.FavoriteBranch, favoriteBranch.getGuid());
      }
      setSetting(CoreAttributeTypes.FavoriteBranch.getName(), favoriteBranch.getGuid());
   }

   public boolean isFavoriteBranch(Branch branch) throws OseeCoreException {
      Collection<Attribute<String>> attributes = getAttributes(CoreAttributeTypes.FavoriteBranch);
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
            OseeExceptions.wrapAndThrow(ex);
         }
         setSoleAttributeFromString("User Settings", stringWriter.toString());
         persist();
      }
   }

   private void ensureUserSettingsAreLoaded() throws OseeCoreException {
      if (userSettings == null) {
         PropertyStore store = new PropertyStore(getGuid());
         try {
            String settings = getSoleAttributeValue("User Settings", null);
            if (settings != null) {
               store.load(new StringReader(settings));
            }
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
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
