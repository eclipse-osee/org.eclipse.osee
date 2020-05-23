/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.messaging.services.internal;

import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

/**
 * @author Andrew M. Finkbeiner
 */
public class ServiceHealthPlusTimeout {

   private final ServiceHealth health;
   private final long shouldHaveRenewedTime;

   public ServiceHealthPlusTimeout(ServiceHealth health, long shouldHaveRenewedTime) {
      this.health = health;
      this.shouldHaveRenewedTime = shouldHaveRenewedTime;
   }

   public boolean isTimedOut(long currentSystemTime) {
      return currentSystemTime > shouldHaveRenewedTime;
   }

   public ServiceHealth getServiceHealth() {
      return health;
   }

}
