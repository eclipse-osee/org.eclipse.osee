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
package org.eclipse.osee.orcs.core.mocks;

import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class MockSessionContext implements SessionContext {

   @Override
   public <T extends ArtifactReadable> T getHistorical(int artId, int stripeId) {
      return null;
   }

   @Override
   public <T extends ArtifactReadable> T getActive(int artId, int branchId) {
      return null;
   }

   @Override
   public String getSessionId() {
      return null;
   }

}
