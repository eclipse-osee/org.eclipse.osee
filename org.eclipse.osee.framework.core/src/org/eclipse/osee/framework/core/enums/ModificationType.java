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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.osee.framework.core.exception.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public enum ModificationType implements Serializable {
   // Artifact, Attribute or Relation that was newly created
   NEW("New", 1),

   // Artifact if any Attribute was changed (not Relation)
   // Attribute if its value was modified
   // Relation if rationale or ordering change
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
   UNDELETED("Undeleted", 7);

   private final static Set<ModificationType> ALL_NONE_HARD_DELETED = new HashSet<ModificationType>();
   private final static Set<ModificationType> ALL_MOD_TYPES = new HashSet<ModificationType>();
   private final static Set<ModificationType> ALL_STATES = new HashSet<ModificationType>();
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

   /**
    * @param value The value of the ModificationType to get.
    * @return The ModificationType that has the value passed.
    * @throws OseeArgumentException
    */
   public static ModificationType getMod(int value) throws OseeArgumentException {
      for (ModificationType modtype : values()) {
         if (modtype.value == value) {
            return modtype;
         }
      }
      throw new OseeArgumentException(value + " does not correspond to any defined ModificationType enumerations");
   }

   public boolean isDeleted() {
      return this == DELETED || this == ARTIFACT_DELETED;
   }

   public static Set<ModificationType> getCurrentModTypes() {
      if (ALL_NONE_HARD_DELETED.isEmpty()) {
         synchronized (ALL_NONE_HARD_DELETED) {
            ALL_NONE_HARD_DELETED.addAll(Arrays.asList(ModificationType.values()));
            ALL_NONE_HARD_DELETED.remove(ModificationType.DELETED);
         }
      }
      return ALL_NONE_HARD_DELETED;
   }

   public static Set<ModificationType> getAllCurrentModTypes() {
      if (ALL_MOD_TYPES.isEmpty()) {
         synchronized (ALL_MOD_TYPES) {
            ALL_MOD_TYPES.addAll(Arrays.asList(ModificationType.values()));
         }
      }
      return ALL_MOD_TYPES;
   }

   public static Set<ModificationType> getAllStates() {
      if (ALL_STATES.isEmpty()) {
         synchronized (ALL_STATES) {
            ALL_STATES.addAll(Arrays.asList(ModificationType.values()));
         }
      }
      return ALL_STATES;
   }
}