/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.script.dsl;

import com.google.common.base.Supplier;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver.OsField;

/**
 * @author Roberto E. Escobar
 */
public enum OsFieldEnum implements OsField {
   branch_id("id"),
   branch_type("type"),
   branch_name("name"),
   branch_state("state"),
   branch_archive_state("archived"),
   branch_parent_id("parent-id"),
   branch_parent_tx_id("parent-tx-id"),
   branch_baseline_tx_id("baseline-tx-id"),
   branch_inherit_access_cntrl("inherits-access-control"),
   branch_associated_art_id("assoc-id"),

   tx_id("id"),
   tx_current("current"),
   tx_type("type"),
   tx_comment("comment"),
   tx_date("date"),
   tx_branch_id("branch-id"),
   tx_author_id("author-id"),
   tx_commit_id("commit-id"),

   art_id("id"),
   art_type("type"),
   art_gamma_id("gamma-id"),
   art_guid("guid"),
   art_mod_type("mod-type"),
   art_txs("txs", Family.ARTIFACT, Family.ARTIFACT_TX),

   art_tx_id("id"),
   art_tx_current("current"),
   art_tx_branch_id("branch-id"),
   art_tx_type("type"),
   art_tx_comment("comment"),
   art_tx_date("date"),
   art_tx_author_id("author-id"),
   art_tx_commit_id("commit-id"),

   attributes("attributes", Family.ARTIFACT, Family.ATTRIBUTE),
   attr_id("id"),
   attr_type("type"),
   attr_gamma_id("gamma-id"),
   attr_ds_value("ds-value"),
   attr_ds_uri("ds-uri"),
   attr_value("value"),
   attr_mod_type("mod-type"),

   attr_txs("txs", Family.ATTRIBUTE, Family.ATTRIBUTE_TX),
   attr_tx_id("id"),
   attr_tx_current("current"),
   attr_tx_branch_id("branch-id"),
   attr_tx_type("type"),
   attr_tx_comment("comment"),
   attr_tx_date("date"),
   attr_tx_author_id("author-id"),
   attr_tx_commit_id("commit-id"),

   relations("relations", Family.ARTIFACT, Family.RELATION),
   rel_id("id"),
   rel_type("type"),
   rel_gamma_id("gamma-id"),
   rel_rationale("rationale"),
   rel_mod_type("mod-type"),
   rel_a_art_id("side-A-id"),
   rel_b_art_id("side-B-id"),
   rel_txs("txs", Family.RELATION, Family.RELATION_TX),
   rel_tx_id("id"),
   rel_tx_current("current"),
   rel_tx_branch_id("branch-id"),
   rel_tx_type("type"),
   rel_tx_comment("comment"),
   rel_tx_date("date"),
   rel_tx_author_id("author-id"),
   rel_tx_commit_id("commit-id");

   public static enum Family {
      UNDEFINED,
      BRANCH,
      TX,
      ARTIFACT,
      ARTIFACT_TX,
      ATTRIBUTE,
      ATTRIBUTE_TX,
      RELATION,
      RELATION_TX;
   }

   private static final Comparator<OsField> FIELD_COMPARATOR = new FieldComparator();

   private static SortedSetMultimap<Family, ? extends OsField> FAMILY_TO_FIELDS;
   private static Table<Family, String, OsField> FAMILY_AND_NAME_TO_FIELDS;
   private final Family family;
   private final String fieldName;
   private final Family childFamily;

   private OsFieldEnum(String fieldName) {
      this(fieldName, null, Family.UNDEFINED);
   }

   private OsFieldEnum(String fieldName, Family family, Family childFamily) {
      this.fieldName = fieldName;
      this.family = family != null ? family : family(this);
      this.childFamily = childFamily;
   }

   @Override
   public String getLiteral() {
      return fieldName;
   }

   public Family getFamily() {
      return family;
   }

   @Override
   public boolean hasChildren() {
      return childFamily != Family.UNDEFINED;
   }

   @Override
   public Set<? extends OsField> getChildren() {
      return getFieldsFor(childFamily);
   }

   @Override
   public String getId() {
      return this.name();
   }

   @Override
   public String toString() {
      return getLiteral();
   }

   public static Comparator<OsField> getComparator() {
      return FIELD_COMPARATOR;
   }

   public static SortedSet<? extends OsField> getFieldsFor(Family family) {
      if (FAMILY_TO_FIELDS == null) {
         SortedSetMultimap<Family, OsField> familyToFields = newSetMultimap(FIELD_COMPARATOR);
         for (OsFieldEnum field : OsFieldEnum.values()) {
            familyToFields.put(field.getFamily(), field);
         }
         OsFieldEnum.FAMILY_TO_FIELDS = familyToFields;
      }
      return FAMILY_TO_FIELDS.get(family);
   }

   public static OsField getField(Family family, String fieldName) {
      if (FAMILY_AND_NAME_TO_FIELDS == null) {
         Table<Family, String, OsField> table = HashBasedTable.create();
         for (OsFieldEnum field : OsFieldEnum.values()) {
            table.put(field.getFamily(), field.getLiteral(), field);
         }
         OsFieldEnum.FAMILY_AND_NAME_TO_FIELDS = table;
      }
      return FAMILY_AND_NAME_TO_FIELDS.get(family, fieldName);
   }

   private static Family family(OsFieldEnum value) {
      Family family = Family.UNDEFINED;
      String name = value.name();
      if (name.startsWith("br")) {
         family = Family.BRANCH;
      } else if (name.startsWith("tx")) {
         family = Family.TX;
      } else if (name.startsWith("art_tx")) {
         family = Family.ARTIFACT_TX;
      } else if (name.startsWith("attr_tx")) {
         family = Family.ATTRIBUTE_TX;
      } else if (name.startsWith("rel_tx")) {
         family = Family.RELATION_TX;
      } else if (name.startsWith("art")) {
         family = Family.ARTIFACT;
      } else if (name.startsWith("attr")) {
         family = Family.ATTRIBUTE;
      } else if (name.startsWith("rel")) {
         family = Family.RELATION;
      }
      return family;
   }

   private static <K, V> SortedSetMultimap<K, V> newSetMultimap(final Comparator<V> comparator) {
      Map<K, Collection<V>> map = Maps.newLinkedHashMap();
      return Multimaps.newSortedSetMultimap(map, new Supplier<SortedSet<V>>() {
         @Override
         public SortedSet<V> get() {
            return Sets.newTreeSet(comparator);
         }
      });
   }

   private static class FieldComparator implements Comparator<OsField> {

      @Override
      public int compare(OsField o1, OsField o2) {
         String literal1 = normalize(o1.getLiteral());
         String literal2 = normalize(o2.getLiteral());
         return literal1.compareTo(literal2);
      }

      private String normalize(String value) {
         String toReturn = value;
         if (toReturn.equals("attributes")) {
            toReturn = "z1" + toReturn;
         } else if (toReturn.equals("relations")) {
            toReturn = "z2" + toReturn;
         } else if (toReturn.equals("txs")) {
            toReturn = "z0" + toReturn;
         }
         return toReturn;
      }
   }

   public static OsField newField(String value) {
      return new OsFieldImpl(value);
   }

   private static final class OsFieldImpl implements OsField, Comparable<OsField> {

      private final String fieldName;

      public OsFieldImpl(String fieldName) {
         super();
         this.fieldName = fieldName;
      }

      @Override
      public String getId() {
         return fieldName;
      }

      @Override
      public int compareTo(OsField o) {
         return this.fieldName.compareTo(o.getLiteral());
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (fieldName == null ? 0 : fieldName.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         OsFieldImpl other = (OsFieldImpl) obj;
         if (fieldName == null) {
            if (other.fieldName != null) {
               return false;
            }
         } else if (!fieldName.equals(other.fieldName)) {
            return false;
         }
         return true;
      }

      @Override
      public String getLiteral() {
         return fieldName;
      }

      @Override
      public String toString() {
         return getLiteral();
      }

      @Override
      public boolean hasChildren() {
         return false;
      }

      @Override
      public Set<? extends OsField> getChildren() {
         return Collections.emptySet();
      }

   }
}