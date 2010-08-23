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
package org.eclipse.osee.framework.access.test.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.test.mocks.Asserts;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleOperation;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.lifecycle.LifecycleServiceImpl;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionCheckPoint;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AbstractLifecycleOperation}
 * 
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class OnEditOperationTest {

   @Test
   public void testOperation() {
      ILifecycleService service = new LifecycleServiceImpl();

      //      service.addHandler(ChangeMgmtChkPoint.TYPE, new ChangeMgmtHandler(new MockAccessCheckProvider()));

      Assert.assertEquals(1, service.getHandlerCount(SkynetTransactionCheckPoint.TYPE));
      Assert.assertFalse(service.getHandlerTypes().isEmpty());

      IBasicArtifact<?> user = new DefaultBasicArtifact(0, "1", "user");
      List<IBasicArtifact<?>> artsToChk = new ArrayList<IBasicArtifact<?>>();
      artsToChk.add(new DefaultBasicArtifact(1, "2", "check me out"));
      IOperation operation = new OnEditOperation(service, user, artsToChk);
      Asserts.testOperation(operation, IStatus.OK);
   }
}
