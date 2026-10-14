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
 * The branch-change-type vocabulary broadcast to web clients on {@code branchChanged} SSE events.
 * <p>
 * Each constant's {@link #getWebValue() web value} is a client contract: those strings must stay in
 * sync with the {@code branchChangeType} union in {@code
 * web/apps/osee/src/app/shared/services/network/sse-event.service.ts}. Centralized here so every
 * server producer/consumer (branch endpoints, SSE broadcast, the server-to-server bus, the desktop
 * bridge) references one definition instead of duplicating literals.
 * <p>
 * This set mirrors the <b>actionable</b> subset of the desktop {@code BranchEventType} vocabulary,
 * mapped 1:1 by {@link BranchEventGuids} in the desktop&harr;web bridge ({@code ActiveMqSseBridge}
 * in {@code orcs.rest}). The desktop's transient/in-progress markers
 * ({@code Committing}/{@code Deleting}/{@code Purging}/{@code CommitFailed}) are intentionally NOT
 * web types: the web is notification-only (GET-on-notify) and has nothing to fetch mid-operation,
 * so those are not relayed. All web types are terminal.
 */
public enum WebBranchChangeType {

   /** A branch was created (DTC {@code ADDED}). Web use: refresh branch lists/selectors. */
   CREATED("created"),
   /** A branch was committed into its parent (DTC {@code COMMITTED}). Web use: refresh; close/re-point views on the source. */
   COMMITTED("committed"),
   /** A branch was renamed (DTC {@code RENAMED}). Web use: refresh name in lists and current-branch views. */
   RENAMED("renamed"),
   /** A branch was archived (DTC {@code ARCHIVE_STATE_UPDATED}). Web use: refresh lists (archived usually hidden). */
   ARCHIVED("archived"),
   /** A branch was unarchived (DTC {@code ARCHIVE_STATE_UPDATED}). Web use: refresh lists. */
   UNARCHIVED("unarchived"),
   /** A non-terminal branch-state change (DTC {@code STATE_UPDATED}). Web use: re-GET current branch state. */
   STATE_CHANGED("state_changed"),
   /** A branch's type changed, e.g. WORKING&harr;BASELINE (DTC {@code TYPE_UPDATED}). Web use: refresh lists/type. */
   TYPE_CHANGED("type_changed"),
   /** A branch was deleted (DTC {@code DELETED}). Web use: remove from lists; close/navigate away from views on it. */
   DELETED("deleted"),
   /** A branch was physically purged (DTC {@code PURGED}). Web use: same as deleted. */
   PURGED("purged"),
   /** Update-from-parent branch swap (web-specific; carries {@code newBranchId}). Web use: re-point views old&rarr;new. */
   REBASELINED("rebaselined");

   private final String webValue;

   private WebBranchChangeType(String webValue) {
      this.webValue = webValue;
   }

   /**
    * @return the wire string sent to web clients (the client-contract token). Use this wherever a
    * change type crosses a boundary as a string (JSON payloads, the desktop bridge, the S2S bus).
    */
   public String getWebValue() {
      return webValue;
   }

   /**
    * Reverse lookup from a wire string to its enum constant.
    *
    * @param webValue a client-contract token (e.g. {@code "created"}); may be null
    * @return the matching constant, or {@code null} if none matches (unknown/transient type)
    */
   public static WebBranchChangeType fromWebValue(String webValue) {
      if (webValue != null) {
         for (WebBranchChangeType type : values()) {
            if (type.webValue.equals(webValue)) {
               return type;
            }
         }
      }
      return null;
   }
}
