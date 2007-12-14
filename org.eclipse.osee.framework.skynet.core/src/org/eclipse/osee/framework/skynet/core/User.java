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
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;

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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.artifact.Artifact#onBirth()
    */
   @Override
   public void onBirth() throws SQLException {
      super.onBirth();
      if (EveryoneGroup.getInstance() != null) EveryoneGroup.getInstance().addGroupMember(this);
   }

   public User(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
   }

   public void setFieldsBasedon(User u) throws SQLException {
      setName(u.getName());
      setPhone(u.getPhone());
      setEmail(u.getEmail());
      setUserID(u.getUserId());
      setActive(u.isActive());
   }

   public String toString() {
      return String.format("%s (%s)", getName(), getUserId());
   }

   public boolean equals(Object obj) {
      User otherUser = null;
      try {
         deleteCheckOveride = true;

         if (obj == null) return false;
         if (obj instanceof User) {
            otherUser = (User) obj;
            otherUser.deleteCheckOveride = true;
            if (otherUser.getUserId().equals(getUserId())) {
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } finally {
         deleteCheckOveride = false;
         if (otherUser != null) otherUser.deleteCheckOveride = false;
      }
   }

   public boolean isMe() {
      return (getUserId().equals(SkynetAuthentication.getInstance().getAuthenticatedUser().getUserId()));
   }

   public boolean equals(User users[]) {
      for (int i = 0; i < users.length; i++) {
         if (users[i].equals(this)) return true;
      }
      return false;
   }

   public int hashCode() {
      try {
         deleteCheckOveride = true;
         return getUserId().hashCode();
      } finally {
         deleteCheckOveride = false;
      }
   }

   public String getUserId() {
      return getSoleAttributeValue(userIdAttributeName);
   }

   public void setUserID(String userId) throws IllegalStateException, SQLException {
      setSoleAttributeValue(userIdAttributeName, userId);
   }

   public String getEmail() {
      return getSoleAttributeValue(Attributes.Email.toString());
   }

   public void setEmail(String email) throws IllegalStateException, SQLException {
      setSoleAttributeValue(Attributes.Email.toString(), email);
   }

   public String getName() {
      return getDescriptiveName();
   }

   public void setName(String name) {
      setDescriptiveName(name);
   }

   public String getPhone() {
      return getSoleAttributeValue(Attributes.Phone.toString());
   }

   public void setPhone(String phone) throws IllegalStateException, SQLException {
      setSoleAttributeValue(Attributes.Phone.toString(), phone);
   }

   public Boolean isActive() throws SQLException {
      String str = getAttributeManager(Attributes.Active.toString()).getSoleAttribute().getStringData();
      return (str.equals("yes"));
   }

   public void setActive(boolean required) throws IllegalStateException, SQLException {
      setSoleBooleanAttributeValue(Attributes.Active.toString(), required);
   }

   @Override
   public boolean isVersionControlled() {
      return true;
   }

   /**
    * @param favoriteBranch
    */
   public void toggleFavoriteBranch(Branch favoriteBranch) {

      try {
         Collection<Branch> branches = branchManager.getBranches();
         HashSet<Integer> branchIds = new HashSet<Integer>();
         for (Branch branch : branches)
            branchIds.add(branch.getBranchId());

         DynamicAttributeManager attributeManager = getAttributeManager(favoriteBranchAttributeName);
         IntegerAttribute branchAttr;
         boolean found = false;
         for (Attribute attribute : attributeManager.getAttributes()) {
            branchAttr = (IntegerAttribute) attribute;
            // Remove attributes that are no longer valid
            if (!branchIds.contains(branchAttr.getInt())) {
               attribute.delete();
            } else if (favoriteBranch.getBranchId() == branchAttr.getInt()) {
               attribute.delete();
               found = true;
               break;
            }
         }

         if (!found) {
            branchAttr = (IntegerAttribute) attributeManager.getNewAttribute();
            branchAttr.setInt(favoriteBranch.getBranchId());
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   public boolean isFavoriteBranch(Branch branch) {
      try {
         DynamicAttributeManager attributeManager = getAttributeManager(favoriteBranchAttributeName);
         IntegerAttribute branchAttr;
         for (Attribute attribute : attributeManager.getAttributes()) {
            branchAttr = (IntegerAttribute) attribute;
            if (branch.getBranchId() == branchAttr.getInt()) {
               return true;
            }
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

      return false;
   }
}
