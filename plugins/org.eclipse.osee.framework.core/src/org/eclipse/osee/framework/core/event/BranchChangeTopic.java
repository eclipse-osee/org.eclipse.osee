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
 * OSGi EventAdmin topic and property constants for branch change events.
 * <p>
 * Fired via {@code EventAdmin.postEvent()} from the ORCS branch layer after a branch is
 * created/deleted/purged/renamed/archived/unarchived, or its state/type changes -- regardless of
 * whether the change originated from a REST endpoint, an ATS operation, or an internal path. This
 * is the single server-side chokepoint for branch changes, mirroring {@link TransactionCommitTopic}
 * for artifact transactions.
 * <p>
 * Listeners (in {@code orcs.rest}) fan the change out to web SSE clients, peer web servers, and
 * desktop clients. The change-type values are the web-facing vocabulary (see {@code
 * WebBranchChangeType}); producers pass one of those strings in {@link #CHANGE_TYPE}.
 */
public final class BranchChangeTopic {

   private BranchChangeTopic() {
      // constants only
   }

   /** OSGi EventAdmin topic for branch change events. */
   public static final String TOPIC = "org/eclipse/osee/branch/CHANGED";

   /** Property: branch id (String) that changed. */
   public static final String BRANCH_ID = "branchId";

   /**
    * Property: change type (String) -- a web-facing branch change type value (e.g. {@code created},
    * {@code deleted}, {@code state_changed}). See {@code WebBranchChangeType}.
    */
   public static final String CHANGE_TYPE = "changeType";

   /** Property: user id (String) who made the change. */
   public static final String USER_ID = "userId";

   /**
    * Property: client-minted origin id (String) of the tab that initiated the change, captured
    * synchronously from {@link OriginContext} on the request thread. Carried to the SSE event so
    * the originating client recognizes and ignores its own echo (self-dedup). Null for
    * non-web/internal changes.
    */
   public static final String ORIGIN_ID = "originId";

   /**
    * Property: for a {@code rebaselined} change, the id (String) of the new working branch that
    * replaced {@link #BRANCH_ID}. Null/absent for other change types.
    */
   public static final String NEW_BRANCH_ID = "newBranchId";

   /**
    * Property: the branch's associated artifact id (String) -- the {@code osee_branch.associated_art_id}
    * of the changed branch. Lets a consumer that doesn't track the branch by id (e.g. an ATS workflow
    * editor waiting for a working branch to be created for it) match the event to its artifact.
    * Populated only where the full {@code Branch} is already loaded (create, state change) so it costs
    * no extra query; absent otherwise.
    */
   public static final String ASSOCIATED_ARTIFACT_ID = "associatedArtifactId";
}
