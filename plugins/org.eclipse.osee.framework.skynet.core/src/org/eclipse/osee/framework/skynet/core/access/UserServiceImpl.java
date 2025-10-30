/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.framework.skynet.core.access;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.framework.core.ApiKeyApi;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.OseeUserArtifact;
import org.eclipse.osee.framework.skynet.core.internal.OseeUserImpl;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;

/**
 * Client Implementation with OseeUser loaded from server
 *
 * @author Donald G. Dunne
 */

public class UserServiceImpl implements UserService {

   private static UserServiceImpl instance;
   private List<IUserGroupArtifactToken> userGrps;
   private OseeUser currentUser;
   private List<OseeUser> users = new ArrayList<>();
   private final Map<Long, OseeUser> idToUser = new HashMap<>();
   private final Map<String, OseeUser> userIdToUser = new HashMap<>();
   private boolean beforeUserCreation = false;
   private boolean loading = false;
   // settings for current user
   private PropertyStore userSettings;
   private AtomicBoolean showTokenForChangeName;

   public UserServiceImpl() {
      // For Jax-Rs
      if (instance == null) {
         instance = this;
      }
   }

   public static UserService getInstance() {
      if (instance == null) {
         instance = new UserServiceImpl();
      }
      return instance;
   }

   @Override
   public void clearCaches() {
      userGrps = null;
      users.clear();
      idToUser.clear();
      userIdToUser.clear();
   }

   /**
    * @return User Groups for current user
    */
   public List<IUserGroupArtifactToken> getUserGrps() {
      if (userGrps == null) {
         userGrps = new ArrayList<>();
         Artifact userArt = OseeApiService.userArt();
         for (Artifact userGrp : userArt.getRelatedArtifacts(CoreRelationTypes.Users_Artifact)) {
            userGrps.add(new UserGroupImpl(userGrp));
         }
      }
      return userGrps;
   }

   @Override
   public Collection<UserToken> getUsers() {
      ensureLoaded();
      return Collections.castAll(users);
   }

   @Override
   public TransactionId createUsers(Iterable<UserToken> users, String comment) {
      DatastoreEndpoint datastoreEndpoint = ServiceUtil.getOseeClient().getDatastoreEndpoint();
      return datastoreEndpoint.createUsers(users);
   }

   @Override
   public TransactionId createUsers(Iterable<UserToken> users, UserToken superUser, String string) {
      throw new UnsupportedOperationException();
   }

   // Not used in client since jwt is server side only currently.
   @Override
   public String getLoginKey() {
      return "";
   }

   @Override
   public Collection<IUserGroupArtifactToken> getMyUserGroups() {
      return getUserGrps();
   }

   @Override
   public OseeUser getUser() {
      ensureLoaded();
      if (loading) {
         return OseeUser.SENTINEL;
      }
      if (currentUser == null) {
         ClientSessionManager.ensureSessionCreated();
         if (ClientSessionManager.isSessionValid()) {
            UserToken currentUserToken = ClientSessionManager.getCurrentUserToken();
            try {
               currentUser = getUser(currentUserToken.getId());
            } catch (Exception ex) {
               System.err.println("Error loading users: " + Lib.exceptionToString(ex));
            }
         }
      }
      return currentUser;
   }

   private void ensureLoaded() {
      if (!loading && users.isEmpty()) {
         loading = true;
         users = new ArrayList<>();
         for (UserToken tok : ServiceUtil.getOseeClient().getOrcsUserEndpoint().get().getUsers()) {
            OseeUser user = new OseeUserImpl(tok);
            users.add(user);
            idToUser.put(user.getId(), user);
            userIdToUser.put(tok.getUserId(), user);
         }
         loading = false;
      }
   }

   @Override
   public OseeUser getUser(UserId userTok) {
      ensureLoaded();
      return idToUser.get(userTok.getId());
   }

   @Override
   public OseeUser getUser(Long accountId) {
      ensureLoaded();
      return idToUser.get(accountId);
   }

   @Override
   public OseeUser getUserByUserId(String userId) {
      ensureLoaded();
      return userIdToUser.get(userId);
   }

   @Override
   public IUserGroup getUserGroup(ArtifactToken userGroupArt) {
      return new UserGroupImpl(userGroupArt);
   }

   @Override
   public IUserGroup getUserGroup(IUserGroupArtifactToken userGroup) {
      IUserGroup group = getUserGroupOrNull(userGroup);
      if (group != null) {
         return group;
      } else {
         throw new OseeArgumentException("parameter must be artifact");
      }
   }

   @Override
   public IUserGroup getUserGroupOrNull(IUserGroupArtifactToken userGroup) {
      Artifact userGroupArt = null;
      if (userGroup instanceof Artifact) {
         userGroupArt = (Artifact) userGroup;
      }
      if (userGroupArt == null) {
         userGroupArt = ArtifactQuery.getArtifactOrNull(userGroup, CoreBranches.COMMON, DeletionFlag.EXCLUDE_DELETED);
         if (userGroupArt != null) {
            return new UserGroupImpl(userGroupArt);
         }
      }
      return null;
   }

   @Override
   public OseeUser getUserIfLoaded() {
      OseeUser user = OseeUser.SENTINEL;
      if (!loading && !isBeforeUserCreation()) {
         user = getUser();
      }
      return user;
   }

   @Override
   public OseeUser getUserIfLoaded(Long accountId) {
      OseeUser user = OseeUser.SENTINEL;
      if (!loading) {
         user = getUser(accountId);
      }
      return user;
   }

   @Override
   public Collection<UserToken> getUsers(IUserGroupArtifactToken userGroup) {
      List<UserToken> users = new ArrayList<>();
      Artifact userGrpArt = ArtifactQuery.getArtifactFromToken(userGroup);
      if (userGrpArt != null && userGrpArt.isValid()) {
         List<Artifact> list = userGrpArt.getRelatedArtifacts(CoreRelationTypes.Users_User);
         for (Artifact art : list) {
            OseeUser user = getUser(art.getId());
            if (user != null) {
               users.add(user);
            }
         }
      }

      return users;
   }

   @Override
   public boolean isBeforeUserCreation() {
      return beforeUserCreation;
   }

   @Override
   public boolean isUserMember(IUserGroupArtifactToken userGroup, Long id) {
      ArtifactToken art = ArtifactQuery.getArtifactTokenFromId(CoreBranches.COMMON, userGroup);
      if (art.isInvalid()) {
         return false;
      }
      return getUserGroup(userGroup).isMember(id);
   }

   @Override
   public void setBeforeUserCreation(boolean beforeUserCreation) {
      this.beforeUserCreation = beforeUserCreation;
      if (!OseeProperties.isInDbInit()) {
         throw new OseeStateException("No user creation outside of dbinit");
      }
   }

   @Override
   public void setUserForCurrentThread(String loginId) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setUserForCurrentThread(UserId accountId) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void removeUserFromCurrentThread() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setUserLoading(boolean loading) {
      this.loading = loading;
   }

   @Override
   public void setUserFromBasic(String credential, ApiKeyApi apiKeyApi) {
      throw new UnsupportedOperationException();
   }

   @Override
   public OseeUser getCurrentUser() {
      if (currentUser == null) {
         getUser();
      }
      return currentUser;
   }

   public void setCurrentUser(OseeUser currentUser) {
      this.currentUser = currentUser;
   }

   @Override
   public String getSafeUserName(UserId userId) {
      try {
         OseeUser user = getUser(userId);
         return user.getName();
      } catch (OseeCoreException ex) {
         return ex.getLocalizedMessage();
      }
   }

   @Override
   public OseeUserImpl create(UserToken userTok) {
      return new OseeUserImpl(userTok);
   }

   @Override
   public boolean isSystemUser(ArtifactToken artifact) {
      return artifact.equals(SystemUser.OseeSystem);
   }

   @Override
   public boolean isCurrentUser(ArtifactToken artifact) {
      return artifact.equals(getUser());
   }

   @Override
   public String getAbridgedEmail(ArtifactToken userTok) {
      return getUserArt().getSoleAttributeValue(CoreAttributeTypes.AbridgedEmail, "");
   }

   @Override
   public String getAbridgedEmail(UserToken userTok) {
      return getUser(userTok).getAbridgedEmail();
   }

   @Override
   public String getSetting(String key) {
      ensureUserSettingsAreLoaded();
      return userSettings.get(key);
   }

   @Override
   public boolean getBooleanSetting(String key) {
      return Boolean.parseBoolean(getSetting(key));
   }

   @Override
   public void setSetting(String key, String value) {
      ensureUserSettingsAreLoaded();
      userSettings.put(key, value);

   }

   @Override
   public void setSetting(String key, Long value) {
      ensureUserSettingsAreLoaded();
      userSettings.put(key, value);

   }

   public Artifact getUserArt() {
      return ArtifactQuery.getArtifactFromId(getCurrentUser().getId(), CoreBranches.COMMON);
   }

   @Override
   public void saveSettings() {
      if (userSettings != null) {
         StringWriter stringWriter = new StringWriter();
         try {
            userSettings.save(stringWriter);
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
         Artifact userArt = getUserArt();
         userArt.setSoleAttributeFromString(CoreAttributeTypes.UserSettings, stringWriter.toString());
         if (userArt.isDirty()) {
            userArt.persist("User - Save Settings (IDE)");
         }
      }
   }

   private void ensureUserSettingsAreLoaded() {
      if (userSettings == null) {
         Artifact userArt = getUserArt();
         PropertyStore store = new PropertyStore(userArt.getGuid());
         try {
            String settings = userArt.getSoleAttributeValue(CoreAttributeTypes.UserSettings, null);
            if (settings != null) {
               store.load(new StringReader(settings));
            }
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
         userSettings = store;
      }
   }

   @Override
   public void setBooleanSetting(String key, boolean value) {
      setSetting(key, String.valueOf(value));
   }

   @Override
   public void setShowTokenForChangeName(boolean showTokenForChangeName) {
      this.showTokenForChangeName.set(showTokenForChangeName);
      setBooleanSetting(OseeProperties.OSEE_SHOW_TOKEN_FOR_CHANGE_NAME, showTokenForChangeName);
   }

   @Override
   public boolean isShowTokenForChangeName() {
      if (this.showTokenForChangeName == null) {
         this.showTokenForChangeName = new AtomicBoolean(false);
         this.showTokenForChangeName.set(getBooleanSetting(OseeProperties.OSEE_SHOW_TOKEN_FOR_CHANGE_NAME));
      }
      return this.showTokenForChangeName.get();
   }

   @Override
   public UserToken create(ArtifactToken userTok) {
      Artifact userArt = null;
      if (userTok instanceof Artifact) {
         userArt = (Artifact) userTok;
      } else {
         userArt = ArtifactQuery.getArtifactFromId(userTok.getId(), CoreBranches.COMMON);
      }
      return new OseeUserArtifact(userArt);
   }
}