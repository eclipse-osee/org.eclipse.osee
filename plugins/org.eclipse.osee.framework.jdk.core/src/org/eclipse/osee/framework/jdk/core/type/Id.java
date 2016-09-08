/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.jdk.core.type;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface Id {
   public static final Long SENTINEL = -1L;

   Long getId();

   default boolean matches(Id... identities) {
      for (Id identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

   default boolean equals(Long id) {
      return getId().equals(id);
   }

   default boolean notEqual(Id id) {
      return !equals(id);
   }

   default boolean isValid() {
      return getId().longValue() > 0;
   }

   default boolean isInvalid() {
      return !isValid();
   }
}