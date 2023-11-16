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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * Select users and store as single userId attributes. This is an AUTO_SAVE widget.
 *
 * @author Donald G. Dunne
 */
public class XHyperlabelMemberSelDam extends XHyperlabelMemberSelection implements AttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XHyperlabelMemberSelDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void refresh() {
      this.selectedUsers = getStoredUsers();
      super.refresh();
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
      refresh();
   }

   public Set<User> getStoredUsers() {
      Set<User> users = new HashSet<>();
      try {
         for (String userId : artifact.getAttributesToStringList(attributeType)) {
            try {
               users.add(UserManager.getUserByUserId(userId));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      return users;
   }

   @Override
   public void saveToArtifact() {
      try {
         Set<String> userIds = new HashSet<>();
         for (User user : getSelectedUsers()) {
            userIds.add(user.getUserId());
         }
         artifact.setAttributeValues(attributeType, userIds);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Result isDirty() {
      if (isEditable()) {
         Set<User> selected = getSelectedUsers();
         Set<User> stored = getStoredUsers();
         if (!Collections.isEqual(selected, stored)) {
            return new Result(true, attributeType + " is dirty");
         }
      }
      return Result.FalseResult;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               if (isRequiredEntry() && getSelectedUsers().isEmpty()) {
                  status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                     "Must Select " + getAttributeType().getUnqualifiedName());
               }
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

   @Override
   public void revert() {
      super.setSelectedUsers(getStoredUsers());
   }

   @Override
   public boolean handleSelection() {
      boolean selected = super.handleSelection();
      if (selected) {
         saveToArtifact();
         getArtifact().persist(getClass().getSimpleName());
      }
      return selected;
   }
}
