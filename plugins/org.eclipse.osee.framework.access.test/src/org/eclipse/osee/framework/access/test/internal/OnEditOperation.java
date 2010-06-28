/*
 * Created on Jun 17, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
