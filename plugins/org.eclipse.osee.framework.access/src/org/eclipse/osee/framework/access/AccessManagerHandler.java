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
package org.eclipse.osee.framework.access;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.access.internal.Activator;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.lifecycle.IAccessCheckProvider;
import org.eclipse.osee.framework.lifecycle.LifecycleHandler;

/**
 * @author Jeff C. Phillips
 */
public class AccessManagerHandler implements LifecycleHandler {
   private final IStatus status = Status.OK_STATUS;
   private IBasicArtifact<?> userArtifact;
   private Collection<IBasicArtifact<?>> artsToCheck;
   private final IAccessCheckProvider accessCheckProvider;

   public AccessManagerHandler(IAccessCheckProvider accessCheckProvider) {
      this.accessCheckProvider = accessCheckProvider;
   }

   public void setData(IBasicArtifact<?> userArtifact, Collection<IBasicArtifact<?>> artsToCheck) {
      this.userArtifact = userArtifact;
      this.artsToCheck = artsToCheck;
   }

   public IStatus onCheck(IProgressMonitor monitor) {
      IStatus statusToReturn = status;

      for (IBasicArtifact<?> artifactToChk : artsToCheck) {
         if (!accessCheckProvider.canEdit(userArtifact, artifactToChk)) {
            statusToReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error");
            break;
         }
      }
      return statusToReturn;
   }

   //   @Override
   //   public IStatus onPostCondition(IProgressMonitor monitor) {
   //      return status;
   //   }
   //
   //   @Override
   //   public IStatus onPreCondition(IProgressMonitor monitor) {
   //      return status;
   //   }

}
