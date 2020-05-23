/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public class ExecuteBlamOperation extends AbstractOperation {
   private final AbstractBlam blamOperation;
   private final VariableMap variableMap;

   public ExecuteBlamOperation(AbstractBlam blamOperation, VariableMap variableMap, OperationLogger logger) {
      super(blamOperation.getName(), Activator.PLUGIN_ID, logger);
      this.variableMap = variableMap;
      this.blamOperation = blamOperation;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      blamOperation.runOperation(variableMap, monitor);
   }
}