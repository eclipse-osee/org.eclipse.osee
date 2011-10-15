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
package org.eclipse.osee.orcs.core.internal.session;

import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Roberto E. Escobar
 */
public class SessionContextImpl implements SessionContext {

   private final String sessionId;

   public SessionContextImpl(String sessionId) {
      super();
      this.sessionId = sessionId;
   }

   @Override
   public String getSessionId() {
      return sessionId;
   }

   @Override
   public <T extends ReadableArtifact> T getHistorical(int artId, int stripeId) {
      return null;
   }

   @Override
   public <T extends ReadableArtifact> T getActive(int artId, int branchId) {
      return null;
   }

}
