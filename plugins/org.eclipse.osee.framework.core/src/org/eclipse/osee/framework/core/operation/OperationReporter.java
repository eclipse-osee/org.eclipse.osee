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

import org.eclipse.core.runtime.IStatus;

/**
 * @author Ryan D. Brooks
 */
public abstract class OperationReporter {

   public abstract void report(String... row);

   public abstract void report(Throwable th);

   public void report(IStatus status) {
      if (status.getSeverity() == IStatus.ERROR) {
         report(status.getException());
      }
   }
}
