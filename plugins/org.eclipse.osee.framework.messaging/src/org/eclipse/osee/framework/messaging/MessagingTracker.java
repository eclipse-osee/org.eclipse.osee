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

package org.eclipse.osee.framework.messaging;

import org.eclipse.osee.framework.messaging.internal.Activator;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
@SuppressWarnings("rawtypes")
public class MessagingTracker extends ServiceTracker {

   @SuppressWarnings("unchecked")
   public MessagingTracker() {
      super(Activator.getInstance().getContext(), OseeMessaging.class.getName(), null);
   }

}
