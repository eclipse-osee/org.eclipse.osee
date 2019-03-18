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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.function.Function;

/**
 * @author Ryan D. Brooks
 */
public interface Id {
   public static final Long SENTINEL = -1L;

   public static <R> R valueOf(String id, Function<Long, R> function) {
      if (id == null || id.equals("") || id.equals("null")) {
         return function.apply(SENTINEL);
      }

      return function.apply(Long.valueOf(id));
   }

   public static Id valueOf(String id) {
      return valueOf(id, BaseId::new);
   }

   public static Id valueOf(int id) {
      return new BaseId(Long.valueOf(id));
   }

   public static Id valueOf(Long id) {
      return new BaseId(id);
   }

   Long getId();

   default int getIdIntValue() {
      return getId().intValue();
   }

   default String getIdString() {
      return String.valueOf(getId());
   }

   default boolean matches(Id... ids) {
      for (Id id : ids) {
         if (equals(id)) {
            return true;
         }
      }
      return false;
   }

   default boolean equals(Long id) {
      return getId().equals(id);
   }

   default boolean notEqual(Long id) {
      return !equals(id);
   }

   default boolean notEqual(Id id) {
      return !equals(id);
   }

   @JsonIgnore
   default boolean isValid() {
      Long id = getId();
      return id != null && id.longValue() > 0;
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

   default <T extends Id> T increment(long increment) {
      return clone(getId() + increment);
   }

   default <T extends Id> T increment() {
      return clone(getId() + 1);
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