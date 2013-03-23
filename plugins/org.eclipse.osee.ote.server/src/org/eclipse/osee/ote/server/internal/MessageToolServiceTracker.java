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
package org.eclipse.osee.ote.server.internal;

import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class MessageToolServiceTracker extends ServiceTracker {

   public MessageToolServiceTracker() {
      super(FrameworkUtil.getBundle(MessageToolServiceTracker.class).getBundleContext(), IRemoteMessageService.class.getName(), null);
   }

   public MessageToolServiceTracker(ServiceTrackerCustomizer customizer) {
      super(FrameworkUtil.getBundle(MessageToolServiceTracker.class).getBundleContext(), IRemoteMessageService.class.getName(), customizer);
   }

   @Override
   public IRemoteMessageService waitForService(long timeout) throws InterruptedException {
      return (IRemoteMessageService) super.waitForService(timeout);
   }

}
