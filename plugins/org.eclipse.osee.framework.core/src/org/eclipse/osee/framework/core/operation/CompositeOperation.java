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
package org.eclipse.osee.framework.core.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.enums.OperationBehavior;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 * @deprecated Use {@link Operations#createBuilder(String) Operations.createBuilder(String name)} instead
 */
@Deprecated
public class CompositeOperation extends AbstractOperation {
   private final List<IStatus> statuses = new ArrayList<IStatus>();
   private final List<? extends IOperation> operations;
   private final OperationBehavior behavior;

   public CompositeOperation(String name, String pluginId, OperationBehavior behavior, OperationLogger logger, List<? extends IOperation> operations) {
      super(name, pluginId, logger);
      this.operations = operations;
      this.behavior = behavior;
   }

   public CompositeOperation(String name, String pluginId, OperationBehavior behavior, List<? extends IOperation> operations) {
      this(name, pluginId, behavior, NullOperationLogger.getSingleton(), operations);
   }

   public CompositeOperation(String name, String pluginId, OperationLogger logger, List<? extends IOperation> operations) {
      this(name, pluginId, OperationBehavior.TerminateOnError, logger, operations);
   }

   public CompositeOperation(String name, String pluginId, List<? extends IOperation> operations) {
      this(name, pluginId, OperationBehavior.TerminateOnError, operations);
   }

   public CompositeOperation(String name, String pluginId, OperationBehavior behavior, OperationLogger logger, IOperation... operations) {
      this(name, pluginId, behavior, logger, Arrays.asList(operations));
   }

   public CompositeOperation(String name, String pluginId, OperationBehavior behavior, IOperation... operations) {
      this(name, pluginId, behavior, Arrays.asList(operations));
   }

   public CompositeOperation(String name, String pluginId, OperationLogger logger, IOperation... operations) {
      this(name, pluginId, logger, Arrays.asList(operations));
   }

   public CompositeOperation(String name, String pluginId, IOperation... operations) {
      this(name, pluginId, Arrays.asList(operations));
   }

   @Override
   protected void doWork(IProgressMonitor parentMonitor) throws Exception {
      Conditions.checkNotNullOrEmpty(operations, "sub-operations");

      SubMonitor subMonitor = SubMonitor.convert(parentMonitor, getName(), Operations.TASK_WORK_RESOLUTION);
      try {
         processSubWork(subMonitor);
      } finally {
         subMonitor.done();
      }
   }

   private void processSubWork(SubMonitor subMonitor) throws Exception {
      int subTicks = Operations.TASK_WORK_RESOLUTION / operations.size();
      for (IOperation operation : operations) {
         checkForCancelledStatus(subMonitor);

         SubMonitor childMonitor = subMonitor.newChild(subTicks);
         childMonitor.subTask(operation.getName());

         IStatus status;
         try {
            status = operation.run(childMonitor);
         } finally {
            childMonitor.done();
         }

         if (behavior == OperationBehavior.TerminateOnError && status.getSeverity() == IStatus.ERROR) {
            setStatus(status);
            return;
         } else if (status.getSeverity() != IStatus.OK) {
            statuses.add(status);
         }
      }
      setStatus(computeCombinedStatus());
   }

   private IStatus computeCombinedStatus() {
      if (statuses.isEmpty()) {
         return null;
      }
      if (statuses.size() == 1) {
         return statuses.get(0);
      }

      StringBuilder strB = new StringBuilder();
      for (IStatus status : statuses) {
         strB.append(status.getMessage());
         strB.append("\n");
      }
      IStatus[] statusArray = statuses.toArray(new IStatus[statuses.size()]);
      return new MultiStatus(getPluginId(), IStatus.OK, statusArray, strB.toString(), null);
   }
}