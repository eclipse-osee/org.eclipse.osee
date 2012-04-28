/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal;

import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.skynet.core.event.EventSystemPreferences;
import org.eclipse.osee.framework.skynet.core.internal.event.EventListenerRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Ryan D. Brooks
 */
public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.skynet.core";

   // To Resolve Initialization issues causes by static OseeEventManager
   private static final EventSystemPreferences preferences = new EventSystemPreferences();
   private static final EventListenerRegistry eventListeners = new EventListenerRegistry();

   @Override
   public void start(BundleContext context) throws Exception {
      ClientSessionManager.class.getCanonicalName();
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      //
   }

   public static EventSystemPreferences getEventPreferences() {
      return preferences;
   }

   public static EventListenerRegistry getEventListeners() {
      return eventListeners;
   }
}