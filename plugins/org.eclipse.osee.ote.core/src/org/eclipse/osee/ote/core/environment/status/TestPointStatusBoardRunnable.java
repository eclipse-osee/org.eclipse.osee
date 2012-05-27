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
package org.eclipse.osee.ote.core.environment.status;

import org.eclipse.osee.ote.core.environment.status.msg.TestPointUpdateMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.osgi.service.event.EventAdmin;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestPointStatusBoardRunnable extends StatusBoardRunnable {

   private final EventAdmin eventAdmin;

   public TestPointStatusBoardRunnable(TestPointUpdateMessage testPointUpdateMessage, EventAdmin eventAdmin) {
      super(testPointUpdateMessage);
      this.eventAdmin = eventAdmin;
   }

   @Override
   public void run() {
	   OteEventMessageUtil.sendEvent(getData(), eventAdmin);
   }

}
