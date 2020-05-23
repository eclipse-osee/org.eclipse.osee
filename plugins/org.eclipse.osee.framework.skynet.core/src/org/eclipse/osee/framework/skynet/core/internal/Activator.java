/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.skynet.core.internal;

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
      //
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