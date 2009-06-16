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

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStoreWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;

/**
 * @author Donald G. Dunne
 */
public class User extends Artifact implements Serializable {
   private static final long serialVersionUID = 834749078806388387L;
   public static final String userIdAttributeName = "User Id";
   public static final String favoriteBranchAttributeName = "Favorite Branch";
   public static enum Attributes {
      Phone, Email, Active, Policy
   };

   public static final String ARTIFACT_NAME = "User";

   private PropertyStore userSettings;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.artifact.Artifact#onBirth()
    */
   @Override
   public void onBirth() throws OseeCoreException {
      super.onBirth();
      SystemGroup.Everyone.addMember(this);
   }

   public User(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public void setFieldsBasedon(User u) throws Exception {
      setDescriptiveName(u.getName());
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
         return (getUserId().equals(UserManager.getUser().getUserId()));
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

   public String getName() {
      return getDescriptiveName();
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
      Collection<Branch> branches = BranchManager.getNormalBranches();
      HashSet<Integer> branchIds = new HashSet<Integer>();
      for (Branch branch : branches)
         branchIds.add(branch.getBranchId());

      boolean found = false;
      Collection<Attribute<Integer>> attributes = getAttributes(favoriteBranchAttributeName);
      for (Attribute<Integer> attribute : attributes) {
         // Remove attributes that are no longer valid
         if (!branchIds.contains(attribute.getValue())) {
            attribute.delete();
         } else if (favoriteBranch.getBranchId() == attribute.getValue()) {
            attribute.delete();
            found = true;
            break;
         }
      }

      if (!found) {
         addAttribute(favoriteBranchAttributeName, favoriteBranch.getBranchId());
      }
   }

   public boolean isFavoriteBranch(Branch branch) throws OseeCoreException {
      Collection<Attribute<Integer>> attributes = getAttributes(favoriteBranchAttributeName);
      for (Attribute<Integer> attribute : attributes) {
         if (branch.getBranchId() == attribute.getValue()) {
            return true;
         }
      }
      return false;
   }

   public String getSetting(String key) throws OseeCoreException {
      ensureUserSettingsAreLoaded();
      return userSettings.get(key);
   }

   public void setSetting(String key, String value) throws OseeCoreException {
      ensureUserSettingsAreLoaded();
      userSettings.put(key, value);

   }

   public void saveSettings() throws OseeCoreException, IOException {
      if (userSettings != null) {
         StringWriter stringWriter = new StringWriter();
         PropertyStoreWriter storeWriter = new PropertyStoreWriter();
         storeWriter.save(userSettings, stringWriter);
         setSoleAttributeFromString("User Settings", stringWriter.toString());
         persistAttributes();
      }
   }

   private void ensureUserSettingsAreLoaded() throws OseeWrappedException {
      if (userSettings == null) {
         try {
            String settings = getSoleAttributeValue("User Settings", null);
            if (settings == null) {
               userSettings = new PropertyStore(getGuid());
            } else {
               userSettings = new PropertyStore(new StringReader(settings));
            }
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
      }
   }

   public boolean isSystemUser() throws OseeCoreException {
      if (this.equals(UserManager.getUser(SystemUser.OseeSystem)) || this.equals(UserManager.getUser(SystemUser.UnAssigned)) || this.equals(UserManager.getUser(SystemUser.Guest))) {
         return true;
      }
      return false;
   }

}
