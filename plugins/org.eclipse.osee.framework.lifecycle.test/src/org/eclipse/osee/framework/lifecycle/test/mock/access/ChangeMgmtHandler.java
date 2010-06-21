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
package org.eclipse.osee.framework.lifecycle.test.mock.access;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.lifecycle.LifecycleOpHandler;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class ChangeMgmtHandler implements LifecycleOpHandler {
   private final IStatus status = Status.OK_STATUS;

   public static interface IAccessDataProvider {
      public boolean canEdit(IBasicArtifact<?> user, IBasicArtifact<?> artTcheck);
   }

   private class AccessDataProvider implements IAccessDataProvider {
      public boolean canEdit(IBasicArtifact<?> user, IBasicArtifact<?> artTcheck) {
         return true;
      }
   }

   private IBasicArtifact<?> userArtifact;
   private Collection<IBasicArtifact<?>> artsToCheck;
   private final AccessDataProvider dataProvider;

   public ChangeMgmtHandler() {
      this.dataProvider = new AccessDataProvider();
   }

   public void setData(IBasicArtifact<?> userArtifact, Collection<IBasicArtifact<?>> artsToCheck) {
      this.userArtifact = userArtifact;
      this.artsToCheck = artsToCheck;
   }

   @Override
   public IStatus onCheck(IProgressMonitor monitor) {
      IStatus statusToReturn = status;

      for (IBasicArtifact<?> artifactToChk : artsToCheck) {
         if (!dataProvider.canEdit(userArtifact, artifactToChk)) {
            statusToReturn = Status.CANCEL_STATUS;
            break;
         }
      }
      return statusToReturn;
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
