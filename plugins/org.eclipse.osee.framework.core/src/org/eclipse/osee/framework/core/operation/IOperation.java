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

package org.eclipse.osee.framework.core.operation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Roberto E. Escobar
 */
public interface IOperation extends Named {
   /**
    * <b>This method should not be called by clients, use {@link Operations#executeWorkAndCheckStatus(IOperation)
    * Operations.execute... methods} instead.</b>
    * 
    * @param subMonitor the progress monitor to use for reporting progress to the user. It is the caller's
    * responsibility to call done() on the given monitor. Accepts null, indicating that no progress should be reported
    * and that the operation cannot be cancelled.
    * @return a status with a severity of IStatus.OK when the operation completes normally. Returns a status with a
    * severity of IStatus.ERROR when the operation terminates due to an exception. Returns a status with a severity of
    * IStatus.CANCEL when monitor.isCanceled() is true
    */
   IStatus run(SubMonitor subMonitor);

   OperationLogger getLogger();
}