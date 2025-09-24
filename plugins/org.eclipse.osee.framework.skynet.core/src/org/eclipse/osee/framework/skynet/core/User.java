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

package org.eclipse.osee.framework.skynet.core;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class User extends Artifact implements UserToken {

   private PropertyStore userSettings;
   // Cache branch favorites based on transactionId of this User's artifact
   private TransactionId userModTx = TransactionId.SENTINEL;
   private Set<BranchId> favoriteBranchIds = null;
   private AtomicBoolean showTokenForChangeName;

   public User(Long id, String guid, BranchToken branch) {
      super(id, guid, branch, CoreArtifactTypes.User);
   }

   public User(BranchToken branch) {
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

   @Override
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

   /**
    * Cached for efficiency as this is called thousands of times to sort favorites. Cache is updated when User artifact
    * is changed (eg: transaction in User art is different)
    */
   public boolean isFavoriteBranch(BranchId branch) {
      if (!getTransaction().equals(userModTx)) {
         if (favoriteBranchIds == null) {
            favoriteBranchIds = new HashSet<>();
         } else {
            favoriteBranchIds.clear();
         }
         for (Attribute<Object> attri : getAttributes(CoreAttributeTypes.FavoriteBranch)) {
            favoriteBranchIds.add(BranchId.valueOf((String) attri.getValue()));
         }
         userModTx = getTransaction();
      }
      return favoriteBranchIds.contains(branch);
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
      if (userSettings != null) {
         StringWriter stringWriter = new StringWriter();
         try {
            userSettings.save(stringWriter);
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
         setSoleAttributeFromString(CoreAttributeTypes.UserSettings, stringWriter.toString());
         if (isDirty()) {
            persist("User - Save Settings (IDE)");
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

   public void setBooleanSetting(String key, boolean value) {
      setSetting(key, String.valueOf(value));
   }

   @Override
   public boolean isOseeAdmin() {
      return getRoles().contains(CoreUserGroups.OseeAdmin);
   }

   @Override
   public Collection<IUserGroupArtifactToken> getRoles() {
      return OsgiUtil.getService(UserAdmin.class, OseeClient.class).userService().getMyUserGroups();
   }

   @Override
   public List<String> getLoginIds() {
      return getAttributeValues(CoreAttributeTypes.LoginId);
   }

   @Override
   public ArtifactToken getArtifact() {
      return this;
   }

   @Override
   public void setArtifact(ArtifactToken artifact) {
      // do nothing
   }

   @Override
   public String toStringFull() {
      return toStringWithId();
   }

   public void setShowTokenForChangeName(boolean showTokenForChangeName) {
      OseeApiService.getUserArt().setBooleanSetting(OseeProperties.OSEE_SHOW_TOKEN_FOR_CHANGE_NAME,
         showTokenForChangeName);
   }

   public boolean isShowTokenForChangeName() {
      if (showTokenForChangeName == null) {
         showTokenForChangeName = new AtomicBoolean(false);
         showTokenForChangeName.set(
            OseeApiService.getUserArt().getBooleanSetting(OseeProperties.OSEE_SHOW_TOKEN_FOR_CHANGE_NAME));
      }
      return showTokenForChangeName.get();
   }

}