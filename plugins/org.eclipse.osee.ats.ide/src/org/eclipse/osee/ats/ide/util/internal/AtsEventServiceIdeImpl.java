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

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.core.event.AbstractAtsEventServiceImpl;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.event.EventType;
import org.eclipse.osee.framework.plugin.core.PluginUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.TopicEvent;
import org.osgi.framework.BundleContext;

/**
 * @author Donald G. Dunne
 */
public class AtsEventServiceIdeImpl extends AbstractAtsEventServiceImpl {

   public AtsEventServiceIdeImpl() {
      // for jax-rs instantiation
   }

   @Override
   public BundleContext getBundleContext(String pluginId) {
      PluginUtil pluginUtil = new PluginUtil(pluginId);
      return pluginUtil.getBundleContext();
   }

   @Override
   public void postAtsWorkItemTopicEvent(AtsTopicEvent event, Collection<IAtsWorkItem> workItems) {
      // Send locally if need be
      if (event.getEventType() == EventType.LocalOnly || event.getEventType() == EventType.LocalAndRemote) {
         super.postAtsWorkItemTopicEvent(event, workItems);
      }
      // Send remote if need be
      if (event.getEventType() == EventType.LocalAndRemote || event.getEventType() == EventType.RemoteOnly) {
         TopicEvent frameworkTopicEvent = new TopicEvent(event.getTopic(), AtsTopicEvent.WORK_ITEM_IDS_KEY,
            AtsObjects.toIdsString(";", workItems), EventType.RemoteOnly);
         OseeEventManager.kickTopicEvent(getClass(), frameworkTopicEvent);
      }
   }

}
