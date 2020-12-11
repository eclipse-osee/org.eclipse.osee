/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.notify;

import org.eclipse.osee.ats.api.notify.IAtsNotificationService;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsNotificationService implements IAtsNotificationService {

   private volatile boolean emailEnabled = true;

   public AbstractAtsNotificationService() {
   }

   @Override
   public boolean isNotificationsEnabled() {
      return emailEnabled;
   }

   @Override
   public void setNotificationsEnabled(boolean enabled) {
      this.emailEnabled = enabled;
   }

}
