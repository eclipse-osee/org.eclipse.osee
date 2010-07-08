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
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.lifecycle.AbstractLifecyclePoint;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class SkynetTransactionCheckPoint extends AbstractLifecyclePoint<SkynetTransactionHandler> {

   public static final Type<SkynetTransactionHandler> TYPE = new Type<SkynetTransactionHandler>();
   private final IBasicArtifact<?> userArtifact;
   private final Collection<IBasicArtifact<?>> artsToCheck;

   public SkynetTransactionCheckPoint(IBasicArtifact<?> userArtifact, Collection<IBasicArtifact<?>> artsToCheck) {
      super();
      this.userArtifact = userArtifact;
      this.artsToCheck = artsToCheck;
   }

   @Override
   protected void initializeHandlerData(SkynetTransactionHandler handler) {
      handler.setData(userArtifact, artsToCheck);
   }

   @Override
   public Type<SkynetTransactionHandler> getAssociatedType() {
      return TYPE;
   }

}
