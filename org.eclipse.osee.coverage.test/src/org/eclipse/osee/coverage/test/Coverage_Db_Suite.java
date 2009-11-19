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
package org.eclipse.osee.coverage.test;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.coverage.test.model.CoverageItemPersistTest;
import org.eclipse.osee.coverage.test.model.CoverageUnitPersistTest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {Coverage_Suite.class, CoverageParametersTest.class, CoverageItemPersistTest.class,
      CoverageUnitPersistTest.class, CoveragePackageImportTest.class})
/**
 * @author Donald G. Dunne
 */
public class Coverage_Db_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      assertTrue("Should be run on test database.", TestUtil.isTestDb());
      assertTrue("Application Server must be running.", ClientSessionManager.getAuthenticationProtocols().contains(
            "lba"));
      assertTrue("Client must authenticate using lba protocol",
            ClientSessionManager.getSession().getAuthenticationProtocol().equals("lba"));
      TestUtil.setIsInTest(true);
   }

   @AfterClass
   public static void tearDown() throws Exception {
      TestUtil.setIsInTest(false);
   }
}
