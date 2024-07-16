/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;

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

   public static @NonNull Id valueOf(Long id) {
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

   default boolean matches(Iterable<? extends Id> ids) {
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

   /**
    * this method must be overridden in subclasses, i.e. BaseId
    */
   default <T extends Id> T clone(Long id) {
      throw new UnsupportedOperationException();
   }

   default boolean isLessThan(Id other) {
      return getId() < other.getId();
   }

   default boolean isGreaterThan(Id other) {
      return getId() > other.getId();
   }

   public static List<String> getIs(Collection<? extends Id> idObjects) {
      return idObjects.stream().map(Id::getIdString).collect(Collectors.toCollection(ArrayList::new));
   }
}