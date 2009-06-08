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
package org.eclipse.osee.framework.ui.skynet.test.cases;

import static org.junit.Assert.assertTrue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOpsExtensionManager;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseIntegrityTest {

   @org.junit.Test
   public void testDatabaseIntegrity() {
      for (String verifyOpId : DatabaseHealthOpsExtensionManager.getVerifyOperationNames()) {
         DatabaseHealthOperation healthCheck = DatabaseHealthOpsExtensionManager.getVerifyOperationByName(verifyOpId);
         healthCheck.setShowDetailsEnabled(false);
         healthCheck.setFixOperationEnabled(false);
         IStatus status;
         try {
            healthCheck.run(new NullProgressMonitor());
            status = healthCheck.getStatus();
            assertTrue(status.getMessage(), status.isOK());
         } catch (Exception ex) {
            assertTrue(Lib.exceptionToString(ex), false);
         }
      }
   }
}
