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
package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.junit.Rule;

/**
 * @author Roberto E. Escobar
 */
public class SqlTest {

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   @org.junit.Test
   public void testOne() throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      chStmt.runPreparedQuery("select * from osee.osee_branch");
      while (chStmt.next()) {
         System.out.println(chStmt.getString("branch_name"));
      }
   }
}
