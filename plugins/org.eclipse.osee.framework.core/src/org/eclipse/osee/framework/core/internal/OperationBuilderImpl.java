/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.OperationBehavior;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.OperationBuilder;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Roberto E. Escobar
 */
public class OperationBuilderImpl implements OperationBuilder {

   private final String name;
   private final String pluginId;
   private OperationBehavior behavior;
   private OperationLogger logger;

   private List<Pair<Double, ? extends IOperation>> operations;
   private double runningTotal;
   private int itemsWithHints;

   public OperationBuilderImpl(String name, String pluginId) {
      this.name = name;
      this.pluginId = pluginId;
      reset();
   }

   private void reset() {
      behavior = OperationBehavior.TerminateOnError;
      logger = NullOperationLogger.getSingleton();
      operations = new ArrayList<>();
      runningTotal = 0;
      itemsWithHints = 0;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public OperationBehavior getExecutionBehavior() {
      return behavior;
   }

   @Override
   public OperationLogger getLogger() {
      return logger;
   }

   @Override
   public OperationBuilder executionBehavior(OperationBehavior behavior) {
      if (behavior != null) {
         this.behavior = behavior;
      }
      return this;
   }

   @Override
   public OperationBuilder logger(OperationLogger logger) {
      if (logger != null) {
         this.logger = logger;
      }
      return this;
   }

   @Override
   public OperationBuilder addOp(IOperation op) {
      operations.add(createEntryWithNoHint(op));
      return this;
   }

   @Override
   public OperationBuilder addOp(double weight, IOperation op) {
      double value = Math.abs(weight);
      runningTotal += value;
      itemsWithHints++;
      operations.add(createEntry(value, op));
      return this;
   }

   @Override
   public OperationBuilder addAll(List<? extends IOperation> operations) {
      for (IOperation op : operations) {
         addOp(op);
      }
      return this;
   }

   @Override
   public synchronized IOperation build() {
      IOperation operation = new WeightedCompositeOperation(getName(), pluginId, getExecutionBehavior(), getLogger(),
         runningTotal, itemsWithHints, operations);
      reset();
      return operation;
   }

   private Pair<Double, IOperation> createEntry(Double weight, IOperation op) {
      return new Pair<>(weight, op);
   }

   private Pair<Double, IOperation> createEntryWithNoHint(IOperation op) {
      return new Pair<>(WeightedCompositeOperation.EMPTY_WEIGHT_HINT, op);
   }

}
