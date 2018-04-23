/*
 * Created on Feb 1, 2016
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.internal;

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
