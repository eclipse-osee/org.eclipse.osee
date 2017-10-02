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
 * @author Theron Virgin
 */
public enum ConflictStatus {

   NOT_CONFLICTED(0),
   UNTOUCHED(1),
   EDITED(2),
   RESOLVED(3),
   OUT_OF_DATE_RESOLVED(4),
   COMMITTED(6),
   INFORMATIONAL(7),
   OUT_OF_DATE(8),
   PREVIOUS_MERGE_APPLIED_SUCCESS(9),
   PREVIOUS_MERGE_APPLIED_CAUTION(10);

   private final int value;

   private ConflictStatus(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public static ConflictStatus valueOf(int value) {
      for (ConflictStatus type : values()) {
         if (type.value == value) {
            return type;
         }
      }
      throw new OseeArgumentException("%d is not a valid ConflictStatus", value);
   }

   public boolean isConflict() {
      return this != NOT_CONFLICTED;
   }

   public boolean isCommitted() {
      return this == COMMITTED;
   }

   public boolean isOutOfDate() {
      return this == OUT_OF_DATE;
   }

   public boolean isInformational() {
      return this == INFORMATIONAL;
   }

   public boolean isUntouched() {
      return this == UNTOUCHED;
   }

   public boolean isOutOfDateCommitted() {
      return this == OUT_OF_DATE_RESOLVED;
   }

   public boolean isResolved() {
      return this == RESOLVED;
   }

   public boolean isEdited() {
      return this == EDITED;
   }

   public boolean isPreviousMergeAppliedWithCaution() {
      return this == PREVIOUS_MERGE_APPLIED_CAUTION;
   }

   public boolean isPreviousMergeSuccessfullyApplied() {
      return this == PREVIOUS_MERGE_APPLIED_SUCCESS;
   }

   public boolean isIgnoreable() {
      return isInformational();
   }

   public boolean isEditable() {
      return !(isResolved() || isCommitted() || isInformational());
   }

   public boolean isResolvable() {
      return !isInformational() && !isCommitted();
   }

   public boolean isOverwriteAllowed() {
      return !isResolved() && !isCommitted();
   }

   public boolean isConsideredResolved() {
      return isResolved() || isCommitted() || isInformational();
   }
}
