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

import java.util.List;
import junit.framework.TestCase;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseIntegrityTest extends TestCase {

   List<DatabaseHealthOperation> databaseHealthTasks;

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception {
      super.setUp();
      ExtensionDefinedObjects<DatabaseHealthOperation> extensionObjects =
            new ExtensionDefinedObjects<DatabaseHealthOperation>("org.eclipse.osee.framework.ui.skynet.DBHealthTask",
                  "DBHealthTask", "class");
      databaseHealthTasks = extensionObjects.getObjects();
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      if (databaseHealthTasks != null) {
         databaseHealthTasks.clear();
      }
   }

   public void testDatabaseIntegrity() {
      for (DatabaseHealthOperation healthCheck : databaseHealthTasks) {
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
