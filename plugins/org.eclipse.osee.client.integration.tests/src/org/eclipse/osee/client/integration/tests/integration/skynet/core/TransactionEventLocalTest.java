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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.junit.After;
import org.junit.Before;

/**
 * @author Donald G. Dunne
 */
public class TransactionEventLocalTest extends AbstractTransactionEventTest {

   private boolean remoteEventLoopback;

   @Before
   public void setUp() {
      remoteEventLoopback = OseeEventManager.getPreferences().isEnableRemoteEventLoopback();
      OseeEventManager.getPreferences().setEnableRemoteEventLoopback(false);
   }

   @After
   public void tearDown() {
      OseeEventManager.getPreferences().setEnableRemoteEventLoopback(remoteEventLoopback);
   }

   @Override
   protected boolean isRemoteTest() {
      return false;
   }

}