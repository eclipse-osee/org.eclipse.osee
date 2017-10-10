/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
