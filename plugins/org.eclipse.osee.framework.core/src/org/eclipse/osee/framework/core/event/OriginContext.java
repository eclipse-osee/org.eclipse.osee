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
 * Request-scoped holder for the client-minted {@code originId} that identifies the browser tab (or
 * other client) that initiated the current request.
 * <p>
 * Set once at the server edge (a JAX-RS request filter reads the {@code X-Origin-Id} header) and
 * read only at change-broadcast capture points, always on the request thread. The value is then
 * carried in the outgoing event payload ({@link TransactionCommitTopic#ORIGIN_ID}, branch change
 * message, server-to-server event) so that consumers on other threads or servers never read this
 * thread-local.
 * <p>
 * <b>Invariant:</b> read only on the request thread. Anything crossing a thread or server boundary
 * must carry {@code originId} in its message, not rely on this holder.
 * <p>
 * This mirrors the established {@code setUserForCurrentThread} pattern already used by the
 * authentication filter. A dedicated holder (rather than SLF4J MDC or JAX-RS request properties) is
 * used so the value is readable from the ORCS layer as well as the REST layer, with no new
 * dependency. The {@code set}/{@code get}/{@code clear} facade allows a later swap to
 * {@code ScopedValue} once it is no longer a preview feature.
 */
public final class OriginContext {

   private static final ThreadLocal<String> ORIGIN_ID = new ThreadLocal<>();

   private OriginContext() {
      // static holder
   }

   /**
    * Binds the origin id for the current thread. Called by the server edge filter.
    *
    * @param originId the client-minted origin id, or null to leave unset
    */
   public static void set(String originId) {
      if (originId == null || originId.isEmpty()) {
         ORIGIN_ID.remove();
      } else {
         ORIGIN_ID.set(originId);
      }
   }

   /**
    * @return the origin id bound to the current thread, or null if none
    */
   public static String get() {
      return ORIGIN_ID.get();
   }

   /**
    * Clears the origin id for the current thread. Called by the server edge (response filter) to
    * avoid leaking the value onto pooled request threads.
    */
   public static void clear() {
      ORIGIN_ID.remove();
   }
}
