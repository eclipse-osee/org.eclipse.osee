/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.rest.test.db;

import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.db.mock.OsgiRule;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.osgi.service.event.EventAdmin;

/**
 * Rule to provide one AtsDatabase per class. Test Class must provide @AfterClass method and call
 * AtsClassDatabase.cleanup().
 *
 * @author Donald G. Dunne
 */
public final class AtsIntegrationByClassRule extends OsgiRule {

   private AtsIntegrationByClassRule() {
      // Utility
   }

   public static TestRule integrationRule(Object testObject) {
      return RuleChain.outerRule(new AtsClassDatabase()).around(new OsgiRule(new CheckServices(), testObject));
   }

   public static class CheckServices {
      // @formatter:off
      @OsgiService public JdbcService jdbcService;
      @OsgiService public Log log;
      @OsgiService public EventAdmin eventAdmin;
      @OsgiService public ExecutorAdmin executorAdmin;
      @OsgiService public OrcsApi orcsApi;
      @OsgiService public IAtsServer atsServer;
      // @formatter:on
   }

}
