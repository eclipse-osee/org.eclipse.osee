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
package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Operation to return if no work is to be done.
 * 
 * @author Donald G. Dunne
 */
public class EmptyOperation extends AbstractOperation {

   public EmptyOperation(String operationName, String pluginId) {
      super(operationName, pluginId);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      monitor.worked(calculateWork(1.0));
   }

}
