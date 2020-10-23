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

package org.eclipse.osee.framework.access.internal;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.access.AccessData;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.lifecycle.AbstractLifecycleVisitor;

/**
 * @author Jeff C. Phillips
 */
public class AccessProviderVisitor extends AbstractLifecycleVisitor<IAccessProvider> {

   public static final Type<IAccessProvider> TYPE = new Type<>();

   private final ArtifactToken userArtifact;
   private final Collection<?> artsToCheck;
   private final AccessData mainAccessData;

   public AccessProviderVisitor(ArtifactToken userArtifact, Collection<?> artsToCheck, AccessData mainAccessData) {
      super();
      this.userArtifact = userArtifact;
      this.artsToCheck = artsToCheck;
      this.mainAccessData = mainAccessData;
   }

   @Override
   public Type<IAccessProvider> getAssociatedType() {
      return TYPE;
   }

   @Override
   protected IStatus dispatch(IProgressMonitor monitor, IAccessProvider accessProvider, String sourceId) {
      IStatus status = Status.OK_STATUS;
      try {
         accessProvider.computeAccess(userArtifact, artsToCheck, mainAccessData);
      } catch (OseeCoreException ex) {
         status =
            new Status(IStatus.ERROR, AccessControlHelper.PLUGIN_ID, "Error during access control computation", ex);
      }
      return status;
   }

}
