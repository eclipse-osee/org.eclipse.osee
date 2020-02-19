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
package org.eclipse.osee.ats.ide.internal;

import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.access.AtsBranchAccessManager;
import org.eclipse.osee.ats.ide.workflow.AtsWorkItemEventHandler;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.eclipse.osee.framework.skynet.core.event.model.AccessTopicEvent;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * @author Donald G. Dunne
 */
public class Activator extends OseeActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.ats.ide";

   @Override
   public void start(final BundleContext context) {
      context.registerService(EventHandler.class.getName(), new AtsWorkItemEventHandler(),
         AtsUtil.hashTable(EventConstants.EVENT_TOPIC, AtsTopicEvent.WORK_ITEM_MODIFIED.getTopic()));
      context.registerService(EventHandler.class.getName(), new AtsBranchAccessManager(),
         AtsUtil.hashTable(EventConstants.EVENT_TOPIC, AccessTopicEvent.ACCESS_BRANCH_MODIFIED.getTopic()));
   }

}
