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
package org.eclipse.osee.orcs;

import org.eclipse.osee.event.EventService;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.mock.OsgiRule;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.osgi.service.event.EventAdmin;

/**
 * @author Roberto E. Escobar
 */
public class OrcsIntegrationRule extends OsgiRule {

   public OrcsIntegrationRule(Object testObject) {
      super(new CheckServices(), testObject);
   }

   public static class CheckServices {
      // @formatter:off
      @OsgiService public Log log;
      @OsgiService public EventAdmin eventAdmin;
      @OsgiService public EventService eventService;
      @OsgiService public ExecutorAdmin executorAdmin;
      @OsgiService public IdentityService idService;
      @OsgiService public IOseeDatabaseService dbService;
      @OsgiService public IOseeModelFactoryService modelFactory;
      @OsgiService public IOseeCachingService cachingService;
      // @formatter:on
   }

}
