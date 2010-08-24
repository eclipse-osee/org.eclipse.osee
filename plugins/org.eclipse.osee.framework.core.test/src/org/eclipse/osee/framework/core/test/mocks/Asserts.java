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
package org.eclipse.osee.framework.core.test.mocks;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.junit.Assert;

/**
 * @author Ryan D. Brooks
 */
public final class Asserts {
   public static IStatus testOperation(IOperation operation, int expectedSeverity) {
      IStatus status = Operations.executeWork(operation);
      Assert.assertEquals(status.toString(), expectedSeverity, status.getSeverity());
      return status;
   }
}