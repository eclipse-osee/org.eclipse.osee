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

import static org.junit.Assert.assertEquals;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOpsExtensionManager;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseIntegrityTest {

   @org.junit.Test
   public void testDatabaseIntegrity() {
      for (String verifyOpId : DatabaseHealthOpsExtensionManager.getVerifyOperationNames()) {
         DatabaseHealthOperation operation = DatabaseHealthOpsExtensionManager.getVerifyOperationByName(verifyOpId);
         operation.setShowDetailsEnabled(false);
         operation.setFixOperationEnabled(false);
         Operations.executeWork(operation, new NullProgressMonitor(), -1);
         assertEquals(String.format("Error [%s]: [%s]", operation.getName(), operation.getStatus().getMessage()),
               IStatus.OK, operation.getStatus().getSeverity());

         int totalItemsToFix = operation.getItemsToFixCount();
         assertEquals(String.format("Error [%s]: found [%s] items", operation.getName(), totalItemsToFix), 0,
               totalItemsToFix);
      }
   }
}
