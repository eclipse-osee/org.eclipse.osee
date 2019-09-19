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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class User extends Artifact implements UserToken {
   private PropertyStore userSettings;

   public User(Long id, String guid, BranchId branch) {
      super(id, guid, branch, CoreArtifactTypes.User);
   }

   public User(BranchId branch) {
      super(branch, CoreArtifactTypes.User);
   }

   public void setFieldsBasedon(User u) throws Exception {
      setName(u.getName());
      setPhone(u.getPhone());
      setEmail(u.getEmail());
      setUserID(u.getUserId());
      setActive(u.isActive());
   }

   @Override
   public String getUserId() {
      return getSoleAttributeValue(CoreAttributeTypes.UserId, "");
   }

   public void setUserID(String userId) {
      setSoleAttributeValue(CoreAttributeTypes.UserId, userId);
   }

   @Override
   public String getEmail() {
      return getSoleAttributeValue(CoreAttributeTypes.Email, "");
   }

   public void setEmail(String email) {
      setSoleAttributeValue(CoreAttributeTypes.Email, email);
   }

   public String getPhone() {
      return getSoleAttributeValue(CoreAttributeTypes.Phone, "");
   }

   public void setPhone(String phone) {
      setSoleAttributeValue(CoreAttributeTypes.Phone, phone);
   }

   @Override
   public boolean isActive() {
      return getSoleAttributeValue(CoreAttributeTypes.Active);
   }

   public void setActive(boolean active) {
      setSoleAttributeValue(CoreAttributeTypes.Active, active);
   }

   public void toggleFavoriteBranch(BranchId favoriteBranch) {
      Conditions.checkNotNull(favoriteBranch, "Branch");
      HashSet<BranchId> branches = new HashSet<>(
         BranchManager.getBranches(BranchArchivedState.UNARCHIVED, BranchType.WORKING, BranchType.BASELINE));

      boolean found = false;
      @SuppressWarnings("deprecation")
      Collection<Attribute<String>> attributes = getAttributes(CoreAttributeTypes.FavoriteBranch);
      for (Attribute<String> attribute : attributes) {
         // Remove attributes that are no longer valid
         BranchId branch;
         try {
            branch = BranchId.valueOf(attribute.getValue());
         } catch (Exception ex) {
            continue;
         }
         if (!branches.contains(branch)) {
            attribute.delete();
         } else if (favoriteBranch.equals(branch)) {
            attribute.delete();
            found = true;
            // Do not break here in case there are multiples of same branch
         }
      }

      if (!found) {
         addAttribute(CoreAttributeTypes.FavoriteBranch, favoriteBranch.getIdString());
      }

      setSetting(CoreAttributeTypes.FavoriteBranch.getName(), favoriteBranch.getIdString());
      saveSettings();
   }

   public boolean isFavoriteBranch(BranchId branch) {
      Collection<String> attributes = getAttributesToStringList(CoreAttributeTypes.FavoriteBranch);
      for (String value : attributes) {
         try {
            if (branch.equals(BranchId.valueOf(value))) {
               return true;
            }
         } catch (Exception ex) {
            // do nothing
         }
      }
      return false;
   }

   public String getSetting(String key) {
      ensureUserSettingsAreLoaded();
      return userSettings.get(key);
   }

   public boolean getBooleanSetting(String key) {
      return Boolean.parseBoolean(getSetting(key));
   }

   public void setSetting(String key, String value) {
      ensureUserSettingsAreLoaded();
      userSettings.put(key, value);

   }

   public void setSetting(String key, Long value) {
      ensureUserSettingsAreLoaded();
      userSettings.put(key, value);

   }

   public void saveSettings() {
      saveSettings(null);
   }

   public void saveSettings(SkynetTransaction transaction) {
      if (userSettings != null) {
         StringWriter stringWriter = new StringWriter();
         try {
            userSettings.save(stringWriter);
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
         setSoleAttributeFromString(CoreAttributeTypes.UserSettings, stringWriter.toString());
         if (transaction == null) {
            persist("User - Save Settings");
         } else {
            persist(transaction);
         }
      }
   }

   private void ensureUserSettingsAreLoaded() {
      if (userSettings == null) {
         PropertyStore store = new PropertyStore(getGuid());
         try {
            String settings = getSoleAttributeValue(CoreAttributeTypes.UserSettings, null);
            if (settings != null) {
               store.load(new StringReader(settings));
            }
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
         userSettings = store;
      }
   }

   public boolean isSystemUser() {
      return SystemUser.isSystemUser(this);
   }

   public void setBooleanSetting(String key, boolean value) {
      setSetting(key, String.valueOf(value));
   }

   @Override
   public boolean isOseeAdmin() {
      return UserGroupService.getOseeAdmin().isCurrentUserMember();
   }

   @Override
   public boolean isCreationRequired() {
      return true;
   }

   @Override
   public Collection<ArtifactToken> getRoles() {
      return Collections.castAll(UserGroupService.getUserGrps());
   }

}
