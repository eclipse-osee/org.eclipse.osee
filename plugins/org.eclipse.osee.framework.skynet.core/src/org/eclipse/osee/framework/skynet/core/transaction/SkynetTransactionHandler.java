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
package org.eclipse.osee.framework.skynet.core.transaction;

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
public class SkynetTransactionHandler implements LifecycleOpHandler {
   private IBasicArtifact<?> userArtifact;
   private Collection<IBasicArtifact<?>> artifactToPersist;

   public SkynetTransactionHandler() {
      super();
   }

   public void setData(IBasicArtifact<?> userArtifact, Collection<IBasicArtifact<?>> artifactToPersist) {
      this.userArtifact = userArtifact;
      this.artifactToPersist = artifactToPersist;
   }

   public IBasicArtifact<?> getUserArtifact() {
      return userArtifact;
   }

   public Collection<IBasicArtifact<?>> getItemsToPersist() {
      return artifactToPersist;
   }

   @Override
   public IStatus onPreCondition(IProgressMonitor monitor) {
      return Status.OK_STATUS;
   }

   @Override
   public IStatus onPostCondition(IProgressMonitor monitor) {
      return Status.OK_STATUS;
   }

   @Override
   public IStatus onCheck(IProgressMonitor monitor) {
      return Status.OK_STATUS;
   }

}
