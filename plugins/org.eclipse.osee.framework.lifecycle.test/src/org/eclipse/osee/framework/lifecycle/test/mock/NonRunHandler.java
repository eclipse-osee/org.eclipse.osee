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
package org.eclipse.osee.framework.lifecycle.test.mock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.lifecycle.LifecycleOpHandler;

/**
 * @author Roberto E. Escobar
 */
public class NonRunHandler implements LifecycleOpHandler {
   private boolean hasRun;
   IStatus status;

   public NonRunHandler() {
      super();
      this.status = Status.OK_STATUS;
      hasRun = false;
   }

   @Override
   public IStatus onCheck(IProgressMonitor monitor) {
      hasRun = true;
      return status;
   }

   @Override
   public IStatus onPostCondition(IProgressMonitor monitor) {
      hasRun = true;
      return status;
   }

   @Override
   public IStatus onPreCondition(IProgressMonitor monitor) {
      hasRun = true;
      return status;
   }

   public boolean hasRan() {
      return hasRun;
   }
}
