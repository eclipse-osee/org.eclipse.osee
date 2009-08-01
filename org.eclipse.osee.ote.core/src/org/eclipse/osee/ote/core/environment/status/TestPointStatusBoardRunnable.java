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

import java.rmi.ConnectException;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class TestPointStatusBoardRunnable extends StatusBoardRunnable {

   private StatusBoard statusBoard;
   /**
    * 
    */
   public TestPointStatusBoardRunnable(IServiceStatusData data, StatusBoard statusBoard) {
      super(data);
      this.statusBoard = statusBoard;
   }
   
   public void run() {
      int size = statusBoard.getListeners().size();
      for (int i = 0; i < size; i++) {
         try {
            statusBoard.getListeners().get(i).statusBoardUpdated(getData());
         } catch (ConnectException e) {
            OseeLog.log(TestEnvironment.class,Level.SEVERE,
                  e.getMessage(), e);
            statusBoard.getListeners().remove(i);
            statusBoard.notifyListeners(getData());
            return;
         } catch (Throwable e) {
            e.printStackTrace();
            OseeLog.log(TestEnvironment.class, Level.SEVERE,
                  e.getMessage(), e);
         }
      }
   }

}
