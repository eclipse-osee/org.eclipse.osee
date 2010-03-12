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

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;

/**
 * @author Roberto E. Escobar
 */
public class CompositeOperation extends AbstractOperation {

   private final Collection<IOperation> operations;

   public CompositeOperation(String name, String pluginId, Collection<IOperation> operations) {
      super(name, pluginId);
      this.operations = operations;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (operations == null || operations.isEmpty()) {
         throw new OseeArgumentException("Sub-operations not available.");
      }
      double workPercentage = 1.00 / operations.size();
      for (IOperation operation : operations) {
         doSubWork(operation, monitor, workPercentage);
      }
   }
}
