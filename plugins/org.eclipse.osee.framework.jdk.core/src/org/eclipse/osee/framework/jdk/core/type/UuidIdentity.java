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

package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Donald G. Dunne
 */
public interface UuidIdentity {

   Long getUuid();

   default boolean matches(UuidIdentity... identities) {
      for (UuidIdentity identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

}
