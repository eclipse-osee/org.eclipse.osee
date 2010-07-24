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
package org.eclipse.osee.framework.messaging.event.res.test;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.messaging.event.res.test.cases.RemoteNetworkSenderTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {RemoteNetworkSenderTest.class,})
/**
 * @author Donald G. Dunne
 */
public class FrameworkRes_Demo_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      assertTrue("Demo Application Server must be running.",
            ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
            ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
   }

}
