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
package org.eclipse.osee.framework.access.test.internal;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleOperation;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionCheckPoint;

public class OnEditOperation extends AbstractLifecycleOperation {

   public OnEditOperation(ILifecycleService service, IBasicArtifact<?> userArtifact, Collection<IBasicArtifact<?>> artsToCheck) {
      super(service, new SkynetTransactionCheckPoint(userArtifact, artsToCheck), "On Edit Op", "TestBundle");
   }

   @Override
   protected void doCoreWork(IProgressMonitor monitor) throws Exception {
      System.out.println("I am going to do some edit ...");
   }

}
