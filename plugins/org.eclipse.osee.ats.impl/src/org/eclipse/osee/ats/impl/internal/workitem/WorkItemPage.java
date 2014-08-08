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
import org.eclipse.osee.ats.impl.action.IWorkItemPage;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class WorkItemPage implements IWorkItemPage {

   private final IAtsServer atsServer;
   private final Log logger;

   public WorkItemPage(Log logger, IAtsServer atsServer) {
      this.logger = logger;
      this.atsServer = atsServer;
   }

   @Override
   public String getHtml(ArtifactReadable action, String title, IResourceRegistry registry) throws Exception {
      ActionPage page = new ActionPage(logger, atsServer, registry, action, title);
      return page.generate();
   }

   @Override
   public String getHtmlWithTransition(ArtifactReadable action, String title, IResourceRegistry registry) throws Exception {
      ActionPage page = new ActionPage(logger, atsServer, registry, action, title);
      page.addTransitionStates();
      return page.generate();
   }

}
