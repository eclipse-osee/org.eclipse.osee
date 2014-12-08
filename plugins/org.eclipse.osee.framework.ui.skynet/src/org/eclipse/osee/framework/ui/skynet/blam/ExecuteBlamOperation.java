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