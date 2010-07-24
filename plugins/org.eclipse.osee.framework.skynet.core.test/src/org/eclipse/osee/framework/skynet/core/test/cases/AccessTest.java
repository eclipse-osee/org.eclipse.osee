/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertFalse;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.junit.Before;

public class AccessTest {
   public AccessTest() {
   }

   @Before
   protected void setUp() throws Exception {
      assertFalse(ClientSessionManager.isProductionDataStore());
   }
}