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

package org.eclipse.osee.framework.core.enums;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface ModificationType extends NamedId {

   public static final ModificationType SENTINEL = internalCreate(Id.SENTINEL, "");

   // Artifact, Attribute or Relation that was newly created
   public static final ModificationType NEW = internalCreate(1L, "New");

   // Artifact if any Attribute was changed (not Relation)
   // Attribute if its value was modified
   // Relation if rationale changes
   // Relation temporarily set to this value in database UnDeleted in memory; this way until UNDELETED persisted to DB
   public static final ModificationType MODIFIED = internalCreate(2L, "Modified");

   // Artifact, Attribute or Relation was deleted
   public static final ModificationType DELETED = internalCreate(3L, "Deleted");

   // Artifact: Not Valid
   // Attribute: if value was merged from merge manager
   // TODO Relation: Not Currently Valid, but needs to be handled
   public static final ModificationType MERGED = internalCreate(4L, "Merged");

   // Artifact: Not Valid
   // Attribute or Relation: was deleted as a direct result of Artifact delete, will be marked as ARTIFACT_DELETED
   public static final ModificationType ARTIFACT_DELETED = internalCreate(5L, "Artifact Deleted");

   // Artifact or Attribute has been reflected from another branch
   public static final ModificationType INTRODUCED = internalCreate(6L, "Introduced");

   // Previously deleted artifact, attribute, or relation has been reinstated
   // Relation can be in this state in memory, but this mod type is persisted as MODIFIED
   public static final ModificationType UNDELETED = internalCreate(7L, "Undeleted");

   //Artifact: Not valid
   //Attribute: Valid and can be used to replace the current version of an attribute with another historical version
   public static final ModificationType REPLACED_WITH_VERSION = internalCreate(8L, "Replace_with_version");

   public static final ModificationType DELETED_ON_DESTINATION = internalCreate(9L, "Deleted on Destination");

   // This should never appear in the database, it is only used by the gui to show applicability changes
   public static final ModificationType APPLICABILITY = internalCreate(10L, "Applicability");

   public static final ModificationType[] values = new ModificationType[] {
      SENTINEL,
      NEW,
      MODIFIED,
      DELETED,
      MERGED,
      ARTIFACT_DELETED,
      INTRODUCED,
      UNDELETED,
      REPLACED_WITH_VERSION,
      DELETED_ON_DESTINATION,
      APPLICABILITY};

   /**
    * This method is only public because all methods in an interface are and it should never be called outside of this
    * interface
    */
   public static ModificationType internalCreate(Long id, String name) {
      final class ModificationTypeImpl extends NamedIdBase implements ModificationType {
         public ModificationTypeImpl(Long id, String name) {
            super(id, name);
         }
      }
      return new ModificationTypeImpl(id, name);
   }

   public static ModificationType valueOf(String id) {
      return Id.valueOf(id, ModificationType::valueOf);
   }

   default boolean isIncluded(DeletionFlag deletionFlag) {
      return !deletionFlag.equals(DeletionFlag.EXCLUDE_DELETED) || !isDeleted();
   }

   /**
    * @param value The value of the ModificationType to get.
    * @return The ModificationType that has the value passed.
    */
   public static ModificationType valueOf(long id) {
      if (id == -1) {
         return SENTINEL;
      }
      if (id > values.length) {
         throw new OseeArgumentException("[%s] is not a valid ModificationType value", id);
      }
      return values[(int) id];
   }

   default boolean isEdited() {
      return this == MERGED || this == MODIFIED;
   }

   // Returns true if artifact has been deleted even if the relation or attribute was not deleted on that artifact
   default boolean isDeleted() {
      return this == DELETED || this == ARTIFACT_DELETED;
   }

   default boolean isUnDeleted() {
      return this == UNDELETED;
   }

   // Returns true if attribute or relation was deleted from an artifact deletion
   default boolean isArtifactDeleted() {
      return this == ARTIFACT_DELETED;
   }

   // Returns false for attributes and relations if artifact was deleted but the attribute or relation was not deleted
   default boolean isHardDeleted() {
      return this == DELETED;
   }

   default boolean isExistingVersionUsed() {
      return matches(ARTIFACT_DELETED, DELETED, INTRODUCED, REPLACED_WITH_VERSION);
   }
}