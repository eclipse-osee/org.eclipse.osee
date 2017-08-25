/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.util;

import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.contains;
import static com.google.common.base.Predicates.containsPattern;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.HasLocalId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.data.HasDeleteState;
import org.eclipse.osee.orcs.data.Modifiable;

/**
 * @author Roberto E. Escobar
 */
public final class OrcsPredicates {

   private OrcsPredicates() {
      // Utility class
   }

   public static <T extends HasDeleteState> Predicate<T> includeDeleted() {
      return deletionFlagEquals(DeletionFlag.INCLUDE_DELETED);
   }

   public static <T extends HasDeleteState> Predicate<T> excludeDeleted() {
      return deletionFlagEquals(DeletionFlag.EXCLUDE_DELETED);
   }

   public static Predicate<Modifiable> isNotDirty() {
      return not(isDirty());
   }

   public static <T extends Modifiable> Predicate<T> isDirty() {
      return new Predicate<T>() {

         @Override
         public boolean apply(T data) {
            return data.isDirty();
         }
      };
   }

   public static <T extends HasDeleteState> Predicate<T> deletionFlagEquals(DeletionFlag includeDeleted) {
      return new DeletedMatcher<T>(includeDeleted);
   }

   public static Predicate<Attribute<String>> attributeStringEquals(String target) {
      return attributeString(equalTo(target));
   }

   public static <T> Predicate<Attribute<T>> attributeValueEquals(T target) {
      return attributeValue(equalTo(target));
   }

   public static Predicate<Attribute<CharSequence>> attributeContainsPattern(String pattern) {
      return attributeString(containsPattern(pattern));
   }

   public static Predicate<Attribute<CharSequence>> attributeContainsPattern(Pattern pattern) {
      return attributeString(contains(pattern));
   }

   public static <T extends CharSequence> Predicate<Attribute<T>> attributeString(Predicate<T> predicate) {
      return compose(predicate, new Function<Attribute<T>, T>() {

         @Override
         public T apply(Attribute<T> input) {
            T value = null;
            try {
               Object rawValue = input.getValue();
               if (rawValue != null) {
                  value = asString(rawValue);
               }
            } catch (OseeCoreException ex) {
               // Do nothing;
            }
            return value;
         }

         @SuppressWarnings("unchecked")
         private T asString(Object rawValue) {
            return (T) String.valueOf(rawValue);
         }
      });
   }

   public static <T> Predicate<Attribute<T>> attributeValue(Predicate<T> predicate) {
      return compose(predicate, new Function<Attribute<T>, T>() {

         @Override
         public T apply(Attribute<T> input) {
            T value = null;
            try {
               value = input.getValue();
            } catch (OseeCoreException ex) {
               // Do nothing;
            }
            return value;
         }

      });
   }

   public static Predicate<Attribute<?>> attributeId(final Integer attributeId) {
      return new Predicate<Attribute<?>>() {

         @Override
         public boolean apply(Attribute<?> input) {
            return attributeId.equals(input.getLocalId());
         }
      };
   }

   private static class DeletedMatcher<T extends HasDeleteState> implements Predicate<T> {

      DeletionFlag flag;

      public DeletedMatcher(DeletionFlag includeDeleted) {
         flag = includeDeleted;
      }

      @Override
      public boolean apply(T data) {
         boolean result = false;
         ModificationType modificationType = data.getModificationType();

         if (flag == DeletionFlag.INCLUDE_HARD_DELETED) {
            result = true;
         } else if (flag == DeletionFlag.INCLUDE_DELETED && !modificationType.isHardDeleted()) {
            result = true;
         } else {
            result = !data.isDeleted();
         }

         return result;
      }
   }

   public static Predicate<Relation> nodeIdOnSideEquals(final HasLocalId<Integer> localId, final RelationSide side) {
      return new Predicate<Relation>() {

         @Override
         public boolean apply(Relation relation) {
            return relation.getLocalIdForSide(side).equals(localId.getLocalId());
         }
      };
   }

   public static Predicate<Relation> nodeIdsEquals(final HasLocalId<Integer> aId, final HasLocalId<Integer> bId) {
      return nodeIdsEquals(aId.getLocalId(), bId.getLocalId());
   }

   public static Predicate<Relation> nodeIdsEquals(final Integer aId, final Integer bId) {
      return new Predicate<Relation>() {

         @Override
         public boolean apply(Relation relation) {
            return aId.equals(relation.getLocalIdForSide(RelationSide.SIDE_A)) && //
            bId.equals(relation.getLocalIdForSide(RelationSide.SIDE_B));
         }
      };
   }
}
