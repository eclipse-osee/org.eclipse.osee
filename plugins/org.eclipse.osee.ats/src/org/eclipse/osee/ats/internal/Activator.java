/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.internal;

import org.eclipse.osee.ats.api.util.AtsEvents;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.workflow.AtsWorkItemEventHandler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * @author Donald G. Dunne
 */
public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.ats";

   @Override
   public void start(final BundleContext context) {
      context.registerService(EventHandler.class.getName(), new AtsWorkItemEventHandler(),
         AtsUtilCore.hashTable(EventConstants.EVENT_TOPIC, AtsEvents.WORK_ITEM_MODIFIED));
   }

   @Override
   public void stop(BundleContext context) {
      //
   }

}
