/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.internal;

import org.eclipse.osee.ats.api.util.IAtsEventService;
import org.eclipse.osee.framework.plugin.core.PluginUtil;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G. Dunne
 */
public class AtsEventServiceImpl implements IAtsEventService {

   @Override
   public EventAdmin getEventAdmin(String pluginId) {
      return (EventAdmin) getBundleContext(pluginId).getServiceReference(EventAdmin.class);
   }

   @Override
   public void postEvent(Event event, String pluginId) {
      getEventAdmin(pluginId).postEvent(event);
   }

   @Override
   public void sendEvent(Event event, String pluginId) {
      getEventAdmin(pluginId).sendEvent(event);
   }

   @Override
   public BundleContext getBundleContext(String pluginId) {
      PluginUtil pluginUtil = new PluginUtil(pluginId);
      return pluginUtil.getBundleContext();
   }

}
