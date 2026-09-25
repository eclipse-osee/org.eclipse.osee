/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.core.event;

/**
 * Stable GUID identifiers for branch event types. Single source of truth shared by the
 * client-side {@code BranchEventType} enum (skynet.core) and server-side event bridging
 * (orcs.rest), which cannot depend on skynet.core. These strings are persisted in event
 * wire formats, so they must never change.
 *
 * @author AI Agent
 */
public final class BranchEventGuids {

   private BranchEventGuids() {
      // constants only
   }

   public static final String ADDED = "AAn_QHDohywDoSTxwcQA";
   public static final String ARCHIVE_STATE_UPDATED = "AAn_QHS7Zhr6OLhKl3gA";
   public static final String RENAMED = "AAn_QHGLIUsH2BdX2gwA";
   public static final String STATE_UPDATED = "AAn_QHQdKhxNLtWPchAA";
   public static final String TYPE_UPDATED = "AAn_QHLW4DKKbUkEZggA";
   public static final String PURGING = "ATPHeMoAFyL543vrAyQA";
   public static final String PURGED = "AAn_QG7jRGZAqPE0UewA";
   public static final String DELETING = "ATPHeNujxAkPZEkWUtQA";
   public static final String DELETED = "AAn_QHBDvwtT5jjKaHgA";
   public static final String COMMITTING = "ATPHeN1du2GAbS3SQsAA";
   public static final String COMMIT_FAILED = "ATPHeN3RaBnDmpoYXkQA";
   public static final String COMMITTED = "AAn_QHIu0mGZytQ11QwA";
   public static final String MERGE_CONFLICT_RESOLVED = "AAn_QHiJ53W5W_k8W7AA";
   public static final String FAVORITES_UPDATED = "AFRkIheIUn3Jpz4kNBgA";
}
