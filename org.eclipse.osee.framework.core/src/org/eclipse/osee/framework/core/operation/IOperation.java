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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author Roberto E. Escobar
 */
public interface IOperation {

   public final static int TOTAL_WORK = Integer.MAX_VALUE;

   public String getName();

   public IStatus getStatus();

   public boolean wasExecuted();

   public ISchedulingRule getSchedulingRule();

   public IOperation run(IProgressMonitor monitor);

   /**
    * @return the total work units as used by a progress monitor
    */
   public int getTotalWorkUnits();
}
