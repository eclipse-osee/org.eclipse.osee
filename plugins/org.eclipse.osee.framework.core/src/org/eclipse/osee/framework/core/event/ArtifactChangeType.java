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
 * Change type constants for artifact/relation events sent over SSE and ActiveMQ.
 * These values must match the TypeScript {@code artifactChangeType} union in
 * {@code web/apps/osee/src/app/shared/services/network/sse-event.service.ts}.
 *
 * @author AI Agent
 */
public final class ArtifactChangeType {

   private ArtifactChangeType() {
      // constants only
   }

   public static final String ATTRIBUTE_MODIFIED = "attribute_modified";
   public static final String ARTIFACT_CREATED = "artifact_created";
   public static final String ARTIFACT_DELETED = "artifact_deleted";
   public static final String RELATION_ADDED = "relation_added";
   public static final String RELATION_DELETED = "relation_deleted";
   public static final String RELATION_MODIFIED = "relation_modified";
}
