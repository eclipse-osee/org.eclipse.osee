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
package org.eclipse.osee.framework.lifecycle.test.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleOperation;
import org.eclipse.osee.framework.lifecycle.LifecycleService;
import org.eclipse.osee.framework.lifecycle.LifecycleServiceImpl;
import org.eclipse.osee.framework.lifecycle.test.mock.access.ChangeMgmtChkPoint;
import org.eclipse.osee.framework.lifecycle.test.mock.access.ChangeMgmtHandler;
import org.eclipse.osee.framework.lifecycle.test.mock.access.OnEditOperation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AbstractLifecycleOperation}
 * 
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class TestOnEditOperation {

   @Test
   public void testOperation() throws OseeCoreException {
      LifecycleService service = new LifecycleServiceImpl();

      service.addHandler(ChangeMgmtChkPoint.TYPE, new ChangeMgmtHandler());

      Assert.assertEquals(1, service.getHandlerCount(ChangeMgmtChkPoint.TYPE));
      Assert.assertFalse(service.getHandlerTypes().isEmpty());

      IBasicArtifact<?> user = new DefaultBasicArtifact(0, "1", "user");
      List<IBasicArtifact<?>> artsToChk = new ArrayList<IBasicArtifact<?>>();
      artsToChk.add(new DefaultBasicArtifact(1, "2", "check me out"));
      IOperation op = new OnEditOperation(service, user, artsToChk);
      Operations.executeWork(op, new LogProgressMonitor(), -1.0);

      IStatus status = op.getStatus();
      Assert.assertTrue(status.isOK());

   }
}
