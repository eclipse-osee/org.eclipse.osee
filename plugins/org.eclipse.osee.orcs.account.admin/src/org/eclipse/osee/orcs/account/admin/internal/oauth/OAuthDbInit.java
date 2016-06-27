/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.account.admin.internal.oauth;

import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.OrcsTopicEvents;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * Handler for {@link OrcsTopicEvents.DBINIT_IMPORT_TYPES}
 *
 * @author Donald G. Dunne
 */
public class OAuthDbInit implements EventHandler {

   private static Log logger;
   private static OrcsApi orcsApi;

   public void setLogger(Log logger) {
      OAuthDbInit.logger = logger;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      OAuthDbInit.orcsApi = orcsApi;
   }

   @Override
   public void handleEvent(Event event) {
      ClientStorageProvider provider = new ClientStorageProvider();
      provider.setLogger(logger);
      provider.setOrcsApi(orcsApi);
      provider.createLoaderTask().run();
   }

}
