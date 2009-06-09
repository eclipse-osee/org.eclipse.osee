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
package org.eclipse.osee.framework.skynet.core.test;

import static org.junit.Assert.assertFalse;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.skynet.core.test.cases.ArtifactQueryPerformanceTests;
import org.eclipse.osee.framework.skynet.core.test.cases.ArtifactQueryTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {ArtifactQueryPerformanceTests.class, ArtifactQueryTest.class})
/**
 * @author Donald G. Dunne
 */
public class FrameworkCore_Production_Suite {

   @BeforeClass
   public static void setUp() throws Exception {
      assertFalse("Application Server must be running.", ClientSessionManager.getAuthenticationProtocols().contains(
            "demo"));
      assertFalse("Client can't authenticate using demo protocol",
            ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
   }
}
