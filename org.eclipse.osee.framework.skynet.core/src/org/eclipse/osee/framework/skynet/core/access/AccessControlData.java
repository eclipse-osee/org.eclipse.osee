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
package org.eclipse.osee.framework.skynet.core.access;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * A data object in an access control list. Contains a subject (user artifact) and object (what the subject is trying to
 * access) and a permission level
 * 
 * @author Jeff C. Phillips
 */
public class AccessControlData implements Comparable<AccessControlData> {

   private static final AccessControlManager accessManager = AccessControlManager.getInstance();
   private boolean dirty = false;
   private boolean birth = false;
   private Artifact subject;
   private AccessObject object;
   private PermissionEnum permission;
   private PermissionEnum branchPermission = null;
   private PermissionEnum artifactTypePermission = null;
   private PermissionEnum artifactPermission = null;

   public AccessControlData(Artifact subject, AccessObject object, PermissionEnum permission, boolean birth) {
      this(subject, object, permission, birth, true);
   }

   public AccessControlData(Artifact subject, AccessObject object, PermissionEnum permission, boolean birth, boolean dirty) {
      super();
      this.subject = subject;
      this.permission = permission;
      this.dirty = dirty;
      this.birth = birth;
      this.object = object;
   }

   /**
    * @return Returns the user.
    */
   public Artifact getSubject() {
      return subject;
   }

   /**
    * @param permission The permissionLevel to set.
    */
   public void setPermission(PermissionEnum permission) {

      if (this.permission == permission) return;

      this.permission = permission;
      dirty = true;
   }

   public PermissionEnum getBranchPermission() throws SQLException {
      return branchPermission;
   }

   public PermissionEnum getArtifactPermission() throws SQLException {
      return artifactPermission;
   }

   public PermissionEnum getArtifactTypePermission() throws SQLException {
      return artifactTypePermission;
   }

   /**
    * @return PermissionEnum
    */
   public PermissionEnum getPermission() {
      return permission;
   }

   /**
    * @return Returns the dirty.
    */
   public boolean isDirty() {
      return dirty;
   }

   public void setNotDirty() {
      this.dirty = false;
   }

   /**
    * @return Returns the object.
    */
   public AccessObject getObject() {
      return object;
   }

   /**
    * @return Returns the birth.
    */
   public boolean isBirth() {
      return birth;
   }

   public void persist() {
      accessManager.persistPermission(this, false);
   }

   public void persist(boolean recurse) {
      accessManager.persistPermission(this, recurse);
   }

   public int compareTo(AccessControlData data) {
      return subject.getDescriptiveName().compareTo(data.subject.getDescriptiveName());
   }

   /**
    * @param branchPermission the branchPermission to set
    */
   public void setBranchPermission(PermissionEnum branchPermission) {
      this.branchPermission = branchPermission;
   }

   /**
    * @param artifactTypePermission the artifactTypePermission to set
    */
   public void setArtifactTypePermission(PermissionEnum artifactTypePermission) {
      this.artifactTypePermission = artifactTypePermission;
   }

   /**
    * @param artifactPermission the artifactPermission to set
    */
   public void setArtifactPermission(PermissionEnum artifactPermission) {
      this.artifactPermission = artifactPermission;
   }
}
