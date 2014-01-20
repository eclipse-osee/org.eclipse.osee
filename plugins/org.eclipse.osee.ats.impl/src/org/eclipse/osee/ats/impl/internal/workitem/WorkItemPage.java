/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.workitem;

import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.action.ActionLoadLevel;
import org.eclipse.osee.ats.impl.action.IWorkItemPage;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class WorkItemPage implements IWorkItemPage {

   private final IResourceRegistry registry;
   private final IAtsServer atsServer;
   private final Log logger;
   private final OrcsApi orcsApi;

   public WorkItemPage(OrcsApi orcsApi, Log logger, IAtsServer atsServer, IResourceRegistry registry) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.atsServer = atsServer;
      this.registry = registry;
   }

   @Override
   public String getHtml(ArtifactReadable action, String title, ActionLoadLevel actionLoadLevel) throws Exception {
      ActionPage page = new ActionPage(logger, atsServer, registry, action, title, actionLoadLevel);
      return page.generate();
   }

   @Override
   public String getHtmlWithStates(ArtifactReadable action, String title, ActionLoadLevel actionLoadLevel) throws Exception {
      ActionPage page = new ActionPage(logger, atsServer, registry, action, title, actionLoadLevel);
      page.addTransitionStates();
      return page.generate();
   }

   @Override
   public ArtifactId createAction(String title, String description, String actionableItemName, String changeType, String priority, String asUserId) throws Exception {
      ActionUtility actionUtility = new ActionUtility(orcsApi, atsServer);
      return actionUtility.createAction(title, description, actionableItemName, changeType, priority, asUserId);
   }

}
