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
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.util.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;

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

   public User(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactSubtypeDescriptor artifactType) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public void setFieldsBasedon(User u) throws Exception {
      setName(u.getName());
      setPhone(u.getPhone());
      setEmail(u.getEmail());
      setUserID(u.getUserId());
      setActive(u.isActive());
   }

   public String toString() {
      try {
         return String.format("%s (%s)", getName(), getUserId());
      } catch (Exception ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
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

      } catch (Exception ex) {
         // do nothing
      } finally {
         deleteCheckOveride = false;
         if (otherUser != null) otherUser.deleteCheckOveride = false;
      }
      return false;
   }

   public boolean isMe() throws SQLException, MultipleAttributesExist {
      try {
         return (getUserId().equals(SkynetAuthentication.getInstance().getAuthenticatedUser().getUserId()));
      } catch (Exception ex) {
         return false;
      }
   }

   public boolean equals(User users[]) {
      for (int i = 0; i < users.length; i++) {
         if (users[i].equals(this)) return true;
      }
      return false;
   }

   public int hashCode() {
      try {
         try {
            deleteCheckOveride = true;
            return getUserId().hashCode();
         } finally {
            deleteCheckOveride = false;
         }
      } catch (Exception ex) {
         // do nothing
      }
      return 0;
   }

   public String getUserId() throws SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(userIdAttributeName, "");
   }

   public void setUserID(String userId) throws IllegalStateException, SQLException {
      setSoleStringAttributeValue(userIdAttributeName, userId);
   }

   public String getEmail() throws SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(Attributes.Email.toString(), "");
   }

   public void setEmail(String email) throws IllegalStateException, SQLException {
      setSoleStringAttributeValue(Attributes.Email.toString(), email);
   }

   public String getName() {
      return getDescriptiveName();
   }

   public void setName(String name) {
      setDescriptiveName(name);
   }

   public String getPhone() throws SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(Attributes.Phone.toString(), "");
   }

   public void setPhone(String phone) throws IllegalStateException, SQLException {
      setSoleStringAttributeValue(Attributes.Phone.toString(), phone);
   }

   public Boolean isActive() throws SQLException, MultipleAttributesExist, AttributeDoesNotExist, MultipleAttributesExist {
      return getSoleAttributeValue(Attributes.Active.toString());
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
            if (!branchIds.contains(branchAttr.getValue())) {
               attribute.delete();
            } else if (favoriteBranch.getBranchId() == branchAttr.getValue()) {
               attribute.delete();
               found = true;
               break;
            }
         }

         if (!found) {
            attributeManager.getNewAttribute().setValue(favoriteBranch.getBranchId());
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public boolean isFavoriteBranch(Branch branch) {
      try {
         DynamicAttributeManager attributeManager = getAttributeManager(favoriteBranchAttributeName);
         IntegerAttribute branchAttr;
         for (Attribute attribute : attributeManager.getAttributes()) {
            branchAttr = (IntegerAttribute) attribute;
            if (branch.getBranchId() == branchAttr.getValue()) {
               return true;
            }
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

      return false;
   }
}
