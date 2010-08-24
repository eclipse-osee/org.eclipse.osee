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
import org.eclipse.osee.framework.core.exception.OseeArgumentException;

/**
 * @author Roberto E. Escobar
 */
public class CompositeOperation extends AbstractOperation {
   private static final int MONITOR_RESOLUTION = 1000;
   private final List<IStatus> statuses = new ArrayList<IStatus>();
   private final List<IOperation> operations;
   private final OperationBehavior behavior;

   public CompositeOperation(String name, String pluginId, OperationBehavior behavior, List<IOperation> operations) {
      super(name, pluginId);
      this.operations = operations;
      this.behavior = behavior;
   }

   public CompositeOperation(String name, String pluginId, List<IOperation> operations) {
      this(name, pluginId, OperationBehavior.TerminateOnError, operations);
   }

   public CompositeOperation(String name, String pluginId, OperationBehavior behavior, IOperation... operations) {
      this(name, pluginId, behavior, Arrays.asList(operations));
   }

   public CompositeOperation(String name, String pluginId, IOperation... operations) {
      this(name, pluginId, Arrays.asList(operations));
   }

   @Override
   protected void doWork(IProgressMonitor parentMonitor) throws Exception {
      if (operations == null || operations.isEmpty()) {
         throw new OseeArgumentException("Sub-operations not available.");
      }
      SubMonitor subMonitor = SubMonitor.convert(parentMonitor, getName(), MONITOR_RESOLUTION);
      int subTicks = MONITOR_RESOLUTION / operations.size();
      for (IOperation operation : operations) {
         IStatus status = operation.run(subMonitor.newChild(subTicks));

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