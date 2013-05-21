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
package org.eclipse.osee.orcs.db.internal.exchange;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.operation.InvalidTxCurrentsAndModTypes;

/**
 * @author Ryan D. Brooks
 */
public class TxCurrentsOpFactory {

   private TxCurrentsOpFactory() {
      //Static utility
   }

   public static IOperation createTxCurrentsAndModTypesOp(IOseeDatabaseService db, OperationLogger logger, boolean archived) {
      List<IOperation> ops = createSubOperations(db, logger, archived);
      return Operations.createBuilder("TxCurrents And Mod Types").addAll(ops).build();
   }

   private static List<IOperation> createSubOperations(IOseeDatabaseService db, OperationLogger logger, boolean archived) {
      List<IOperation> operations = new ArrayList<IOperation>(3);
      operations.add(buildFixOperation(db, logger, archived, "1/3 ", "osee_artifact", "art_id"));
      operations.add(buildFixOperation(db, logger, archived, "2/3 ", "osee_attribute", "attr_id"));
      operations.add(buildFixOperation(db, logger, archived, "3/3 ", "osee_relation_link", "rel_link_id"));
      return operations;
   }

   private static IOperation buildFixOperation(IOseeDatabaseService db, OperationLogger logger, boolean archived, String operationName, String tableName, String columnName) {
      return new InvalidTxCurrentsAndModTypes(db, operationName, tableName, columnName, logger, true, archived);
   }
}