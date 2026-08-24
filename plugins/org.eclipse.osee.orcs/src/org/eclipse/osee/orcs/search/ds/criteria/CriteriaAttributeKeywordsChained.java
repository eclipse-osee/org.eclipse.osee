/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.search.ds.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.search.ds.Criteria;
import org.eclipse.osee.orcs.search.ds.Options;

/**
 * Criteria that holds multiple attribute type/value pairs to be chained together in a single query. Each entry
 * represents one attribute constraint. The resulting SQL chains CTEs so that each subsequent constraint narrows the
 * art_id result set from the previous one.
 */
public class CriteriaAttributeKeywordsChained extends Criteria {

   private final List<AttributeConstraint> constraints;

   public CriteriaAttributeKeywordsChained() {
      this.constraints = new ArrayList<>();
   }

   public CriteriaAttributeKeywordsChained(List<AttributeConstraint> constraints) {
      this.constraints = constraints;
   }

   public void addConstraint(AttributeTypeToken attributeType, Collection<String> values, QueryOption... options) {
      constraints.add(new AttributeConstraint(attributeType, values, options));
   }

   public List<AttributeConstraint> getConstraints() {
      return constraints;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNullOrEmpty(constraints, "attribute constraints");
      for (AttributeConstraint constraint : constraints) {
         Conditions.checkNotNull(constraint.getAttributeType(), "attribute type");
         Conditions.checkNotNullOrEmpty(constraint.getValues(), "search values");
      }
   }

   @Override
   public String toString() {
      return String.format("CriteriaAttributeKeywordsChained [constraints=%s]", constraints);
   }

   /**
    * Represents a single attribute type + values constraint within the chain.
    */
   public static class AttributeConstraint {
      private AttributeTypeToken attributeType;
      private Collection<String> values;
      private QueryOption[] options;

      public AttributeConstraint() {
         // for Jackson deserialization
      }

      public AttributeConstraint(AttributeTypeToken attributeType, Collection<String> values, QueryOption... options) {
         this.attributeType = attributeType;
         this.values = values;
         this.options = options;
      }

      public AttributeTypeToken getAttributeType() {
         return attributeType;
      }

      public Collection<String> getValues() {
         return values;
      }

      public QueryOption[] getOptions() {
         return options;
      }

      @Override
      public String toString() {
         return String.format("[type=%s, values=%s]", attributeType, values);
      }
   }
}
