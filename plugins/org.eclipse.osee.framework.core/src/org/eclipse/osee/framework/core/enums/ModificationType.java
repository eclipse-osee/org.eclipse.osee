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

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public enum ModificationType {
   // Artifact, Attribute or Relation that was newly created
   NEW("New", 1),

   // Artifact if any Attribute was changed (not Relation)
   // Attribute if its value was modified
   // Relation if rationale changes
   // Relation temporarily set to this value in database UnDeleted in memory; this way until UNDELETED persisted to DB
   MODIFIED("Modified", 2),

   // Artifact, Attribute or Relation was deleted
   DELETED("Deleted", 3),

   // Artifact: Not Valid
   // Attribute: if value was merged from merge manager
   // TODO Relation: Not Currently Valid, but needs to be handled
   MERGED("Merged", 4),

   // Artifact: Not Valid
   // Attribute or Relation: was deleted as a direct result of Artifact delete, will be marked as ARTIFACT_DELETED
   ARTIFACT_DELETED("Artifact Deleted", 5),

   // Artifact or Attribute has been reflected from another branch
   INTRODUCED("Introduced", 6),

   // Previously deleted artifact, attribute, or relation has been reinstated
   // Relation can be in this state in memory, but this mod type is persisted as MODIFIED
   UNDELETED("Undeleted", 7),

   //Artifact: Not valid
   //Attribute: Valid and can be used to replace the current version of an attribute with another historical version
   REPLACED_WITH_VERSION("Replace_with_version", 8),

   DELETED_ON_DESTINATION("Deleted on Destination", 9),

   // This should never appear in the database, it is only used by the gui to show applicability changes
   APPLICABILITY("Applicability", 10);

   private int value;
   private String displayName;

   ModificationType(String displayName, int value) {
      this.displayName = displayName;
      this.value = value;
   }

   /**
    * @return Returns the value.
    */
   public int getValue() {
      return value;
   }

   public boolean isEdited() {
      return this == MERGED || this == MODIFIED;
   }

   public String getDisplayName() {
      return displayName;
   }

   public boolean matches(ModificationType... modTypes) {
      for (ModificationType modType : modTypes) {
         if (modType == this) {
            return true;
         }
      }
      return false;
   }

   /**
    * @param value The value of the ModificationType to get.
    * @return The ModificationType that has the value passed.
    */
   public static ModificationType getMod(int value) throws OseeArgumentException {
      for (ModificationType modtype : values()) {
         if (modtype.value == value) {
            return modtype;
         }
      }
      throw new OseeArgumentException("[%s] does not correspond to any defined ModificationType enumerations", value);
   }

   // Returns true if artifact has been deleted even if the relation or attribute was not deleted on that artifact
   public boolean isDeleted() {
      return this == DELETED || this == ARTIFACT_DELETED;
   }

   public boolean isUnDeleted() {
      return this == UNDELETED;
   }

   // Returns true if attribute or relation was deleted from an artifact deletion
   public boolean isArtifactDeleted() {
      return this == ARTIFACT_DELETED;
   }

   // Returns false for attributes and relations if artifact was deleted but the attribute or relation was not deleted
   public boolean isHardDeleted() {
      return this == DELETED;
   }

   public boolean isExistingVersionUsed() {
      boolean result = false;
      switch (this) {
         case ARTIFACT_DELETED:
         case DELETED:
         case INTRODUCED:
         case REPLACED_WITH_VERSION:
            result = true;
            break;
         default:
            result = false;
            break;
      }
      return result;
   }
}