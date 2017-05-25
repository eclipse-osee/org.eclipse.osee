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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Ryan D. Brooks
 */
public interface Id {
   public static final Long SENTINEL = -1L;

   public static Id valueOf(String id) {
      return valueOf(Long.valueOf(id));
   }

   @JsonCreator
   public static Id valueOf(long id) {
      return new BaseId(id);
   }

   Long getId();

   default String getIdString() {
      return getId().toString();
   }

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

   default boolean notEqual(long id) {
      return !equals(id);
   }

   default boolean notEqual(Id id) {
      return !equals(id);
   }

   @JsonIgnore
   default boolean isValid() {
      return getId().longValue() > 0;
   }

   @JsonIgnore
   default boolean isInvalid() {
      return !isValid();
   }

   default <T extends Id> T minus(T id) {
      return clone(getId() - id.getId());
   }

   default <T extends Id> T plus(T id) {
      return clone(getId() + id.getId());
   }

   default <T extends Id> T clone(Long id) {
      throw new UnsupportedOperationException();
   }

   default boolean isLessThan(Id other) {
      return getId() < other.getId();
   }

   default boolean isGreaterThan(Id other) {
      return getId() > other.getId();
   }
}