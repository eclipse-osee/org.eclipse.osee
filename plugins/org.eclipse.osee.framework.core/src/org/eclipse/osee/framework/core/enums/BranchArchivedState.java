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

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Ryan D. Brooks
 */
public class BranchArchivedState extends NamedIdBase {
   public static final BranchArchivedState ARCHIVED = new BranchArchivedState(1, "ARCHIVED");
   public static final BranchArchivedState UNARCHIVED = new BranchArchivedState(0, "UNARCHIVED");
   public static final BranchArchivedState ALL = new BranchArchivedState(-1, "ALL");
   public static final BranchArchivedState ARCHIVED_IN_PROGRESS = new BranchArchivedState(2, "ARCHIVED_IN_PROGRESS");
   public static final BranchArchivedState UNARCHIVED_IN_PROGRESS =
      new BranchArchivedState(3, "UNARCHIVED_IN_PROGRESS");

   private static final BranchArchivedState[] values =
      new BranchArchivedState[] {ARCHIVED, UNARCHIVED, ALL, ARCHIVED_IN_PROGRESS, UNARCHIVED_IN_PROGRESS};

   BranchArchivedState(int id, String name) {
      super(Long.valueOf(id), name);
   }

   public boolean isBeingArchived() {
      return this == ARCHIVED_IN_PROGRESS;
   }

   public boolean isBeingUnarchived() {
      return this == UNARCHIVED_IN_PROGRESS;
   }

   public boolean isArchived() {
      return this == ARCHIVED;
   }

   public boolean isUnArchived() {
      return this == UNARCHIVED;
   }

   public boolean matches(boolean isArchived) {
      return this == BranchArchivedState.ALL || this == fromBoolean(isArchived);
   }

   /**
    * @return mapping to BranchArchivedState subset.
    * <p>
    * <code> true == BranchArchivedState.ARCHIVED<br/>
    * false == BranchArchivedState.UNARCHIVED <code>
    * </p>
    */
   public static BranchArchivedState fromBoolean(boolean archived) {
      return archived ? ARCHIVED : UNARCHIVED;
   }

   public static BranchArchivedState valueOf(int id) {
      return NamedIdBase.valueOf(Long.valueOf(id), values);
   }

   public static BranchArchivedState valueOf(String id) {
      return NamedIdBase.valueOf(Long.valueOf(id), values);
   }

   public static BranchArchivedState[] values() {
      return values;
   }
}