/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.mocks;

import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.jdk.core.util.GUID;

public class MockBasicUser extends MockIArtifact implements IBasicUser {

   private final String userId;

   public MockBasicUser(String name, String userId) {
      super(234, name, GUID.create(), null, null);
      this.userId = userId;
   }

   @Override
   public String getUserId() {
      return userId;
   }

   @Override
   public boolean isActive() {
      return true;
   }

}
