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
package org.eclipse.osee.framework.skynet.core.test.event;

import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class TransactionEventLocalTest extends TransactionEventTest {

   @BeforeClass
   public static void setUp() {
      OseeEventManager.getPreferences().setEnableRemoteEventLoopback(false);
   }

   @Override
   protected boolean isRemoteTest() {
      return false;
   }

}