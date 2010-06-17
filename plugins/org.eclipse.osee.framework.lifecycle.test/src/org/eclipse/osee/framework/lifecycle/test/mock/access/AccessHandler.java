package org.eclipse.osee.framework.lifecycle.test.mock.access;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.lifecycle.LifecycleOpHandler;

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
/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class AccessHandler implements LifecycleOpHandler {
   private final IStatus status = Status.OK_STATUS;

   @Override
   public IStatus onCheck(IProgressMonitor monitor) {
      return status;
   }

   @Override
   public IStatus onPostCondition(IProgressMonitor monitor) {
      return status;
   }

   @Override
   public IStatus onPreCondition(IProgressMonitor monitor) {
      return status;
   }

}
