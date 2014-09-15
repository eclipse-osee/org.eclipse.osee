/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.UuidNamedIdentity;

public abstract class AbstractAtsProgram extends UuidNamedIdentity<Long> implements IAtsProgram {

   public AbstractAtsProgram(Long uid, String name) {
      super(uid, name);
   }

   @Override
   public String toStringWithId() {
      return String.format("[%s][%s]", getUuid(), getName());
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      for (Identity<?> identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

}
