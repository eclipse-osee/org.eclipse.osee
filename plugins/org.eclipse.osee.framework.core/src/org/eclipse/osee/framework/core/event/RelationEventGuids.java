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
 * Single source of truth for relation-event GUIDs shared by the desktop client and the web/server.
 * Lives in {@code framework.core} so both the client {@code RelationEventType}
 * (skynet.core) and the server web-to-desktop bridge (orcs.rest) reference the same constants
 * rather than duplicating the literals. Mirrors {@link BranchEventGuids}.
 */
public final class RelationEventGuids {

   private RelationEventGuids() {
      // constants holder
   }

   public static final String DELETED = "AISIbR2MjAo7JhvDvkgA";
   public static final String PURGED = "AAn_P4kbcxaUKL4bosgA";
   public static final String ADDED = "AISIbR69A2yjMFpbsSgA";
   // Handles ModifiedRationale and (currently) Undeleted.
   public static final String MODIFIED_RATIONALE = "AISIbR9Tm0dwqN1KdoAA";
   public static final String UNDELETED = "AISIbRqzlF3s4TeMvzgA";
   public static final String MODIFIED_ORDER = "35990681-a172-4a34-8135-812aa3e26651";
   public static final String MODIFIED_RELATED_ARTIFACT = "9c68d644-afe9-4887-b1be-ade3b511bf66";
}
