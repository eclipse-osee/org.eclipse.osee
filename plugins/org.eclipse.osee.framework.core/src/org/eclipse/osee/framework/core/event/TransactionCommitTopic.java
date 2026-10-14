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
 * OSGi EventAdmin topic and property constants for transaction commit events.
 * <p>
 * Fired asynchronously via {@code EventAdmin.postEvent()} after every successful
 * transaction commit, regardless of whether it originated from a REST endpoint
 * or an internal/desktop client path. Listeners can use these events to:
 * <ul>
 *   <li>Broadcast to SSE web clients</li>
 *   <li>Notify desktop Eclipse clients via ActiveMQ</li>
 *   <li>Perform any other post-commit processing</li>
 * </ul>
 */
public final class TransactionCommitTopic {

   private TransactionCommitTopic() {
      // constants only
   }

   /** OSGi EventAdmin topic for transaction commit events. */
   public static final String TOPIC = "org/eclipse/osee/transaction/COMMITTED";

   /** Property: branch ID (String). */
   public static final String BRANCH_ID = "branchId";

   /** Property: transaction ID (String). */
   public static final String TRANSACTION_ID = "transactionId";

   /** Property: author user ID (String) -- who committed. */
   public static final String AUTHOR_USER_ID = "authorUserId";

   /** Property: affected artifact IDs (String[] or List<String>). */
   public static final String ARTIFACT_IDS = "artifactIds";

   /** Property: artifact type IDs corresponding to each artifact (String[]). */
   public static final String ARTIFACT_TYPE_IDS = "artifactTypeIds";

   /** Property: change types (String[]) -- e.g. "artifact_created", "attribute_modified". */
   public static final String CHANGE_TYPES = "changeTypes";

   /**
    * Property: serialized attribute changes (String -- JSON).
    * <p>
    * JSON format: {@code { "<artifactId>": [ { "attrId": N, "attrTypeId": N, "gammaId": N, "modType": "...", "data": ["..."] }, ... ], ... }}
    * <p>
    * Keyed by artifact ID string. Each entry is an array of attribute changes for that artifact.
    */
   public static final String ATTRIBUTE_CHANGES = "attributeChanges";

   /**
    * Property: per-artifact modification type names (String[]).
    * Parallel array to ARTIFACT_IDS. Values are ModificationType names: "New", "Modified", "Deleted", etc.
    */
   public static final String ARTIFACT_MOD_TYPES = "artifactModTypes";

   /**
    * Property: serialized relation changes (String -- JSON).
    * <p>
    * JSON format: {@code [ { "relTypeId": N, "artIdA": N, "artIdB": N, "gammaId": N, "modType": "...", "rationale": "..." }, ... ]}
    */
   public static final String RELATION_CHANGES = "relationChanges";

   /**
    * Property: serialized user references from changed user-valued attributes (String -- JSON),
    * grouped by attribute type. Populated generically from attribute types marked with a
    * user-reference {@code DisplayHint} -- no domain (ATS/MIM) knowledge on the producer.
    * <p>
    * JSON format: {@code [ { "typeId": "N", "encoding": "artId|userId", "userIds": ["..."] }, ... ]}
    * <p>
    * Consumers (e.g. the Actra world view) filter by the {@code typeId} they care about and match
    * {@code userIds} against the current user to decide client-side relevance.
    */
   public static final String ASSOCIATED_USERS = "associatedUsers";

   /**
    * Property: distinct attribute type ids changed anywhere in this transaction (String -- JSON
    * array of id strings, e.g. {@code ["1152921504606847088", ...]}). Populated from the dirty
    * attributes at commit; lets clients do targeted refreshes (e.g. refresh a hierarchy label only
    * when the Name attribute type changed) instead of reacting to every {@code attribute_modified}.
    */
   public static final String CHANGED_ATTRIBUTE_TYPE_IDS = "changedAttributeTypeIds";

   /**
    * Property: client-minted origin id (String) identifying the browser tab/client that initiated
    * the commit, captured from the {@code X-Origin-Id} request header via {@link OriginContext}.
    * <p>
    * Carried through to the SSE event so the originating client recognizes and ignores its own echo
    * (self-dedup) without server-side connection exclusion. May be null for non-web/internal
    * commits.
    */
   public static final String ORIGIN_ID = "originId";
}
