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
package org.eclipse.osee.x.server.integration.tests.util;

import static org.eclipse.osee.orcs.rest.client.OseeClient.OSEE_APPLICATION_SERVER;
import java.util.HashMap;
import java.util.Map;
import org.databene.contiperf.junit.ContiPerfRule;
import org.databene.contiperf.report.CSVLatencyReportModule;
import org.databene.contiperf.report.CSVSummaryReportModule;
import org.databene.contiperf.report.HtmlReportModule;
import org.eclipse.osee.account.rest.client.AccountClient;
import org.eclipse.osee.account.rest.client.AccountClientStandaloneSetup;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.client.OseeClientStandaloneSetup;
import org.junit.rules.MethodRule;

public final class IntegrationUtil {

   private IntegrationUtil() {
      // Utility class
   }

   public static MethodRule createPerformanceRule() {
      return new ContiPerfRule(new HtmlReportModule(), new CSVSummaryReportModule(), new CSVLatencyReportModule());
   }

   public static OseeClient createClient() {
      Map<String, Object> config = createClientConfig();
      return OseeClientStandaloneSetup.createClient(config);
   }

   private static String getOseeApplicationServer() {
      return System.getProperty(OSEE_APPLICATION_SERVER, "http://localhost:8089");
   }

   private static Map<String, Object> createClientConfig() {
      Map<String, Object> config = new HashMap<>();
      config.put(OSEE_APPLICATION_SERVER, getOseeApplicationServer());
      return config;
   }

   public static AccountClient createAccountClient() {
      Map<String, Object> config = createClientConfig();
      return AccountClientStandaloneSetup.createClient(config);
   }

}
