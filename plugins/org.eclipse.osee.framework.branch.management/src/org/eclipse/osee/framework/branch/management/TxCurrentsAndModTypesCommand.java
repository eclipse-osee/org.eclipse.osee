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
package org.eclipse.osee.framework.branch.management;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationReporter;
import org.eclipse.osee.framework.database.operation.InvalidTxCurrentsAndModTypes;

/**
 * @author Ryan D. Brooks
 */
public class TxCurrentsAndModTypesCommand extends CompositeOperation {
   public TxCurrentsAndModTypesCommand(OperationReporter reporter, boolean archived) {
      super("TxCurrents And Mod Types", Activator.PLUGIN_ID, buildSubOperations(reporter, archived));
   }

   private static List<IOperation> buildSubOperations(OperationReporter reporter, boolean archived) {
      List<IOperation> operations = new ArrayList<IOperation>(3);
      operations.add(buildFixOperation(reporter, archived, "osee_artifact", "art_id"));
      operations.add(buildFixOperation(reporter, archived, "osee_attribute", "attr_id"));
      operations.add(buildFixOperation(reporter, archived, "osee_relation_link", "rel_link_id"));
      return operations;
   }

   private static IOperation buildFixOperation(OperationReporter reporter, boolean archived, String tableName, String columnName) {
      return new InvalidTxCurrentsAndModTypes(tableName, columnName, reporter, true, archived);
   }
}