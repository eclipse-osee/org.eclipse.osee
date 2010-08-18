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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.CommandInterpreterReporter;
import org.eclipse.osee.framework.core.operation.OperationReporter;
import org.eclipse.osee.framework.database.operation.InvalidTxCurrentsAndModTypes;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Ryan D. Brooks
 */
public class TxCurrentsAndModTypesCommand extends AbstractOperation {
   private final boolean archived;
   private final OperationReporter reporter;

   public TxCurrentsAndModTypesCommand(CommandInterpreter commandInterpreter) {
      this(new CommandInterpreterReporter(commandInterpreter), Boolean.parseBoolean(commandInterpreter.nextArgument()));
   }

   public TxCurrentsAndModTypesCommand(OperationReporter reporter, boolean archived) {
      super("TxCurrents And Mod Types", Activator.PLUGIN_ID);
      this.archived = archived;
      this.reporter = reporter;
   }

   private void checkAndFix(String tableName, String columnName, IProgressMonitor monitor) throws Exception {
      doSubWork(new InvalidTxCurrentsAndModTypes(tableName, columnName, reporter, true, archived), monitor, 0.3);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      try {
         checkAndFix("osee_artifact", "art_id", monitor);
         checkAndFix("osee_attribute", "attr_id", monitor);
         checkAndFix("osee_relation_link", "rel_link_id", monitor);
      } catch (OseeCoreException ex) {
         reporter.report(ex);
      }
   }
}