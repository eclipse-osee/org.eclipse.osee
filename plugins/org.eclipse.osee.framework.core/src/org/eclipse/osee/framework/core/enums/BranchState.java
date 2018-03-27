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
 * @author Roberto E. Escobar
 */
public class BranchState extends NamedIdBase {
   public static final BranchState CREATED = new BranchState(0, "CREATED");
   public static final BranchState MODIFIED = new BranchState(1, "MODIFIED");
   public static final BranchState COMMITTED = new BranchState(2, "COMMITTED");
   public static final BranchState REBASELINED = new BranchState(3, "REBASELINED");
   public static final BranchState DELETED = new BranchState(4, "DELETED");
   public static final BranchState REBASELINE_IN_PROGRESS = new BranchState(5, "REBASELINE_IN_PROGRESS");
   public static final BranchState COMMIT_IN_PROGRESS = new BranchState(6, "COMMIT_IN_PROGRESS");
   public static final BranchState CREATION_IN_PROGRESS = new BranchState(7, "CREATION_IN_PROGRESS");
   public static final BranchState DELETE_IN_PROGRESS = new BranchState(8, "DELETE_IN_PROGRESS");
   public static final BranchState PURGE_IN_PROGRESS = new BranchState(9, "PURGE_IN_PROGRESS");
   public static final BranchState PURGED = new BranchState(10, "PURGED");
   private static final BranchState[] values = new BranchState[] {
      CREATED,
      MODIFIED,
      COMMITTED,
      REBASELINED,
      DELETED,
      REBASELINE_IN_PROGRESS,
      COMMIT_IN_PROGRESS,
      CREATION_IN_PROGRESS,
      DELETE_IN_PROGRESS,
      PURGE_IN_PROGRESS,
      PURGED};

   private BranchState(int id, String name) {
      super(Long.valueOf(id), name);
   }

   public boolean isCommitInProgress() {
      return this == BranchState.COMMIT_IN_PROGRESS;
   }

   public boolean isCreated() {
      return this == BranchState.CREATED;
   }

   public boolean isModified() {
      return this == BranchState.MODIFIED;
   }

   public boolean isCommitted() {
      return this == BranchState.COMMITTED;
   }

   public boolean isRebaselined() {
      return this == BranchState.REBASELINED;
   }

   public boolean isRebaselineInProgress() {
      return this == BranchState.REBASELINE_IN_PROGRESS;
   }

   public boolean isCreationInProgress() {
      return this == BranchState.CREATION_IN_PROGRESS;
   }

   public boolean isDeleted() {
      return this == BranchState.DELETED;
   }

   public boolean isPurged() {
      return this == BranchState.PURGED;
   }

   public boolean isDeleteInProgress() {
      return this == BranchState.DELETE_IN_PROGRESS;
   }

   public boolean isPurgeInProgress() {
      return this == BranchState.PURGE_IN_PROGRESS;
   }

   public static BranchState valueOf(int id) {
      return NamedIdBase.valueOf(Long.valueOf(id), values);
   }

   public static BranchState valueOf(String id) {
      return NamedIdBase.valueOf(Long.valueOf(id), values);
   }

   public static BranchState[] values() {
      return values;
   }

   public static BranchState fromName(String name) {
      return NamedIdBase.fromName(name, values);
   }
}