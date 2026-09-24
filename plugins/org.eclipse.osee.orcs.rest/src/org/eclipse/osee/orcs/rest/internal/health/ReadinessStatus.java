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

package org.eclipse.osee.orcs.rest.internal.health;

/**
 * Minimal readiness response. Intentionally carries only the boolean signal -- no internal topology
 * -- so the probe leaks nothing; failure detail is logged server-side.
 */
public class ReadinessStatus {

   private boolean ready;

   public ReadinessStatus() {
      // For deserialization.
   }

   public ReadinessStatus(boolean ready) {
      this.ready = ready;
   }

   public boolean isReady() {
      return ready;
   }

   public void setReady(boolean ready) {
      this.ready = ready;
   }
}
