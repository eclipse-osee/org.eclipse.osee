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
package org.eclipse.osee.coverage.integration.tests.integration;

import static org.eclipse.osee.coverage.demo.CoverageChoice.OSEE_COVERAGE_DEMO;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.demo.CoverageExampleFactory;
import org.eclipse.osee.coverage.demo.CoverageExamples;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoverageManagerTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_COVERAGE_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public OseeHousekeepingRule hk = new OseeHousekeepingRule();

   @Test
   public void testImportCoverage() throws Exception {
      ICoverageImporter importer = CoverageExampleFactory.createExample(CoverageExamples.COVERAGE_IMPORT_01);
      CoverageManager.importCoverage(importer);
   }
}
