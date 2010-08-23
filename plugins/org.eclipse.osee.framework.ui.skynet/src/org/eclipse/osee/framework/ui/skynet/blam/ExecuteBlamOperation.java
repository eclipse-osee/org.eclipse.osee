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
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Ryan D. Brooks
 */
public class ExecuteBlamOperation extends AbstractOperation {
   private final AbstractBlam blamOperation;
   private final VariableMap variableMap;
   private final Appendable output;

   public ExecuteBlamOperation(String name, Appendable output, VariableMap variableMap, AbstractBlam blamOperation) {
      super(name, SkynetGuiPlugin.PLUGIN_ID);
      this.variableMap = variableMap;
      this.blamOperation = blamOperation;
      this.output = output;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      blamOperation.setOseeDatabaseService(SkynetGuiPlugin.getInstance().getOseeDatabaseService());
      blamOperation.setOutput(output);
      doSubWork(new InternalOperationAdapter(blamOperation), monitor, 1);
   }

   private final class InternalOperationAdapter extends AbstractOperation {
      private final AbstractBlam operation;

      public InternalOperationAdapter(AbstractBlam operation) {
         super(operation.getName(), SkynetGuiPlugin.PLUGIN_ID);
         this.operation = operation;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         operation.runOperation(variableMap, monitor);
      }
   }
}