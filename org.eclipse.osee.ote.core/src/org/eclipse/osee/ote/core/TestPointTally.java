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
package org.eclipse.osee.ote.core;

import org.eclipse.osee.ote.core.environment.status.OTEStatusBoard;
import org.eclipse.osee.ote.core.internal.Activator;
import org.eclipse.osee.ote.core.log.ITestPointTally;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class TestPointTally implements ITestPointTally {
   private int testPointSuccesses;
   private int testPointFailures;
   private String testName;
   private OTEStatusBoard statusBoard;
   
   public TestPointTally(String testName){
      this.testName = testName;
      statusBoard = Activator.getInstance().getOteStatusBoard();
   }
   
   public void reset() {
      this.testPointSuccesses = 0;
      this.testPointFailures = 0;
      sendUpdate();
   }

   public int tallyTestPoint(boolean pass) {
      if (pass) {
         testPointSuccesses++;
      } else {
         testPointFailures++;
      }
      sendUpdate();
      return getTestPointTotal();
   }

   private void sendUpdate(){
      statusBoard.onTestPointUpdate(testPointSuccesses, testPointFailures, testName);
   }
   
   public int getTestPointTotal() {
      return testPointSuccesses + testPointFailures;
   }
}

