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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.data.Named;

/**
 * @author Roberto E. Escobar
 */
public interface IOperation extends Named {
   /**
    * @param subMonitor the progress monitor to use for reporting progress to the user. It is the caller's
    * responsibility to call done() on the given monitor. Accepts null, indicating that no progress should be reported
    * and that the operation cannot be cancelled.
    * @return a status with a severity of IStatus.OK when the operation completes normally. Returns a status with a
    * severity of IStatus.ERROR when the operation terminates due to an exception. Returns a status with a severity of
    * IStatus.CANCEL when monitor.isCanceled() is true
    */
   IStatus run(SubMonitor subMonitor);
}