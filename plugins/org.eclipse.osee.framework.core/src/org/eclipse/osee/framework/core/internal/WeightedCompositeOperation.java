/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.enums.OperationBehavior;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class WeightedCompositeOperation extends AbstractOperation {

   public static final Double EMPTY_WEIGHT_HINT = null;

   private final List<IStatus> statuses = new ArrayList<>();
   private final List<Pair<Double, ? extends IOperation>> operations;
   private final OperationBehavior behavior;
   private final double runningTotal;
   private final int itemsWithHints;

   public WeightedCompositeOperation(String name, String pluginId, OperationBehavior behavior, OperationLogger logger, double runningTotal, int itemsWithHints, List<Pair<Double, ? extends IOperation>> operations) {
      super(name, pluginId, logger);
      this.operations = operations;
      this.behavior = behavior;
      this.runningTotal = runningTotal;
      this.itemsWithHints = itemsWithHints;
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
      double averageWeightHint = getAverageWeightHint();
      double sumOfAllWeights = computeSumOfTotalWeights(averageWeightHint);

      for (Pair<Double, ? extends IOperation> entry : operations) {
         checkForCancelledStatus(subMonitor);

         double actualWeight = getActualWeight(entry.getFirst(), averageWeightHint, sumOfAllWeights);
         IOperation operation = entry.getSecond();

         int subTicks = calculateWork(actualWeight);

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

   private double getAverageWeightHint() {
      double average;
      if (itemsWithHints > 0) {
         average = runningTotal / itemsWithHints;
      } else {
         int total = operations.size();
         average = 1.0 / total;
      }
      return average;
   }

   private double computeSumOfTotalWeights(double averageWeightHint) {
      int totalItems = operations.size();
      double itemsWithoutHints = totalItems - itemsWithHints;
      return runningTotal + itemsWithoutHints * averageWeightHint;
   }

   private double getActualWeight(Double initialWeightHint, double averageWeightHint, double sumOfAllWeights) {
      Double value = initialWeightHint;
      if (value == EMPTY_WEIGHT_HINT) {
         value = averageWeightHint;
      }
      return value / sumOfAllWeights;
   }

   private IStatus computeCombinedStatus() {
      IStatus toReturn = null;
      if (!statuses.isEmpty()) {
         if (statuses.size() > 1) {
            StringBuilder strB = new StringBuilder();
            for (IStatus status : statuses) {
               strB.append(status.getMessage());
               strB.append("\n");
            }
            IStatus[] statusArray = statuses.toArray(new IStatus[statuses.size()]);
            toReturn = new MultiStatus(getPluginId(), IStatus.OK, statusArray, strB.toString(), null);
         } else {
            toReturn = statuses.get(0);
         }
      }
      return toReturn;
   }
}