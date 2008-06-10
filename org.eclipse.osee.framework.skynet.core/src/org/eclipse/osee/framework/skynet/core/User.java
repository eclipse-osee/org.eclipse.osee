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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

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
   public void onBirth() throws OseeCoreException {
      super.onBirth();
      try {
         EveryoneGroup.addGroupMember(this);
      } catch (SQLException ex) {
         throw new OseeCoreException(ex.getMessage(), ex);
      }
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

   public String toString() {
      try {
         return String.format("%s (%s)", getName(), getUserId());
      } catch (Exception ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   public boolean isMe() throws SQLException, MultipleAttributesExist {
      try {
         return (getUserId().equals(SkynetAuthentication.getUser().getUserId()));
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

   public String getUserId() throws SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(userIdAttributeName, "");
   }

   public void setUserID(String userId) throws SQLException, MultipleAttributesExist {
      setSoleAttributeValue(userIdAttributeName, userId);
   }

   public String getEmail() throws SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(Attributes.Email.toString(), "");
   }

   public void setEmail(String email) throws SQLException, MultipleAttributesExist {
      setSoleAttributeValue(Attributes.Email.toString(), email);
   }

   public String getName() {
      return getDescriptiveName();
   }

   public String getPhone() throws SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(Attributes.Phone.toString(), "");
   }

   public void setPhone(String phone) throws SQLException, MultipleAttributesExist {
      setSoleAttributeValue(Attributes.Phone.toString(), phone);
   }

   public Boolean isActive() throws SQLException, MultipleAttributesExist, AttributeDoesNotExist, MultipleAttributesExist {
      return getSoleAttributeValue(Attributes.Active.toString());
   }

   public void setActive(boolean required) throws SQLException, MultipleAttributesExist {
      setSoleAttributeValue(Attributes.Active.toString(), required);
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
         Collection<Branch> branches = BranchPersistenceManager.getBranches();
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
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public boolean isFavoriteBranch(Branch branch) {
      try {
         Collection<Attribute<Integer>> attributes = getAttributes(favoriteBranchAttributeName);
         for (Attribute<Integer> attribute : attributes) {
            if (branch.getBranchId() == attribute.getValue()) {
               return true;
            }
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

      return false;
   }
}
