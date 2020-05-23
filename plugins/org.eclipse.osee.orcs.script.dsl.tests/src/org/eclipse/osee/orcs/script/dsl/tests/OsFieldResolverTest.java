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

package org.eclipse.osee.orcs.script.dsl.tests;

import static org.eclipse.osee.orcs.script.dsl.tests.OsFieldUtil.newCollectClause;
import static org.eclipse.osee.orcs.script.dsl.tests.OsFieldUtil.newCollectObject;
import static org.junit.Assert.assertEquals;
import com.google.inject.Inject;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver.OsField;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslInjectorProvider;
import org.eclipse.osee.orcs.script.dsl.OsCollectType;
import org.eclipse.osee.orcs.script.dsl.OsFieldEnum;
import org.eclipse.osee.orcs.script.dsl.OsFieldEnum.Family;
import org.eclipse.osee.orcs.script.dsl.formatting.OrcsScriptDslFormatter;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression;
import org.eclipse.osee.orcs.script.dsl.tests.OsFieldUtil.Scope;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Case for {@link OrcsScriptDslFormatter}
 *
 * @author Roberto E. Escobar
 */
@InjectWith(OrcsScriptDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class OsFieldResolverTest {

   private static final OsField ERROR_FIELD = OsFieldEnum.newField("error");

   @Inject
   private IFieldResolver resolver;

   @Test
   public void testGetCollectTypeBranches() {
      EObject object = newCollectObject("branches");
      OsCollectType type = resolver.getCollectType(object);
      assertEquals(OsCollectType.BRANCHES, type);
   }

   @Test
   public void testGetCollectTypeTxs() {
      EObject object = newCollectObject("txs");
      OsCollectType type = resolver.getCollectType(object);
      assertEquals(OsCollectType.TXS, type);
   }

   @Test
   public void testGetCollectTypeArtifact() {
      EObject object = newCollectObject("artifacts");
      OsCollectType type = resolver.getCollectType(object);
      assertEquals(OsCollectType.ARTIFACTS, type);
   }

   @Test
   public void testGetCollectTypeAttributes() {
      EObject object = newCollectObject("attributes");
      OsCollectType type = resolver.getCollectType(object);
      assertEquals(OsCollectType.ATTRIBUTES, type);
   }

   @Test
   public void testGetCollectTypeRelations() {
      EObject object = newCollectObject("relations");
      OsCollectType type = resolver.getCollectType(object);
      assertEquals(OsCollectType.RELATIONS, type);
   }

   @Test
   public void testGetCollectTypeFromField() {
      OsCollectObjectExpression expression = newCollectObject("artifacts", "field-1");
      OsCollectType type = resolver.getCollectType(expression.getExpressions().get(0));
      assertEquals(OsCollectType.ARTIFACTS, type);
   }

   @Test
   public void testGetCollectTypeFromAllFields() {
      OsCollectObjectExpression expression = newCollectObject("artifacts", "*");
      OsCollectType type = resolver.getCollectType(expression.getExpressions().get(0));
      assertEquals(OsCollectType.ARTIFACTS, type);
   }

   @Test
   public void testGetAllowedCollectTypes() {
      OsCollectType[] expecteds =
         {OsCollectType.BRANCHES, OsCollectType.TXS, OsCollectType.ARTIFACTS, OsCollectType.ARTIFACTS};
      int index = 0;
      for (Scope scope : Scope.values()) {
         OsCollectClause clause = newCollectClause(scope, "object-name");
         Set<OsCollectType> actual = resolver.getAllowedCollectTypes(clause);
         assertEquals(1, actual.size());
         OsCollectType expected = expecteds[index++];
         assertEquals(expected, actual.iterator().next());
      }
   }

   @Test
   public void testBranchFields() {
      OsCollectObjectExpression object = newCollectObject("branches", "*");

      SortedSet<? extends OsField> expected = OsFieldEnum.getFieldsFor(Family.BRANCH);

      Set<? extends OsField> allowedFields = resolver.getAllowedFields(object);
      assertEquals(10, allowedFields.size());
      assertEquals(expected, allowedFields);

      // spot check
      assertEquals(true, allowedFields.contains(OsFieldEnum.branch_inherit_access_cntrl));
      assertEquals(true, allowedFields.contains(OsFieldEnum.branch_type));

      Set<? extends OsField> declaredFields = resolver.getDeclaredFields(object);
      assertEquals(expected, declaredFields);

      Set<? extends OsField> notAllowed = resolver.getNotAllowedDeclaredFields(object);
      assertEquals(0, notAllowed.size());

      Set<? extends OsField> remaining = resolver.getRemainingAllowedFields(object);
      assertEquals(0, remaining.size());

      ///// Individual Fields
      object = newCollectObject("branches", "id", "archived", "parent-tx-id", "inherits-access-control", "error");

      declaredFields = resolver.getDeclaredFields(object);
      assertEquals(5, declaredFields.size());
      Iterator<? extends OsField> iterator = declaredFields.iterator();
      assertEquals(OsFieldEnum.branch_id, iterator.next());
      assertEquals(OsFieldEnum.branch_archive_state, iterator.next());
      assertEquals(OsFieldEnum.branch_parent_tx_id, iterator.next());
      assertEquals(OsFieldEnum.branch_inherit_access_cntrl, iterator.next());
      assertEquals(ERROR_FIELD, iterator.next());

      notAllowed = resolver.getNotAllowedDeclaredFields(object);
      assertEquals(1, notAllowed.size());
      assertEquals(true, notAllowed.contains(ERROR_FIELD));

      remaining = resolver.getRemainingAllowedFields(object);
      assertEquals(6, remaining.size());
      assertEquals(true, !remaining.contains(OsFieldEnum.branch_archive_state));
   }

   @Test
   public void testTxsFields() {
      OsCollectObjectExpression object = newCollectObject("txs", "*");

      SortedSet<? extends OsField> expected = OsFieldEnum.getFieldsFor(Family.TX);

      Set<? extends OsField> allowedFields = resolver.getAllowedFields(object);
      assertEquals(8, allowedFields.size());
      assertEquals(expected, allowedFields);

      // spot check
      assertEquals(true, allowedFields.contains(OsFieldEnum.tx_author_id));
      assertEquals(true, allowedFields.contains(OsFieldEnum.tx_type));

      Set<? extends OsField> declaredFields = resolver.getDeclaredFields(object);
      assertEquals(expected, declaredFields);

      Set<? extends OsField> notAllowed = resolver.getNotAllowedDeclaredFields(object);
      assertEquals(0, notAllowed.size());

      Set<? extends OsField> remaining = resolver.getRemainingAllowedFields(object);
      assertEquals(0, remaining.size());

      ///// Individual Fields
      object = newCollectObject("txs", "id", "current", "author-id", "commit-id", "error");

      declaredFields = resolver.getDeclaredFields(object);
      assertEquals(5, declaredFields.size());
      Iterator<? extends OsField> iterator = declaredFields.iterator();
      assertEquals(OsFieldEnum.tx_id, iterator.next());
      assertEquals(OsFieldEnum.tx_current, iterator.next());
      assertEquals(OsFieldEnum.tx_author_id, iterator.next());
      assertEquals(OsFieldEnum.tx_commit_id, iterator.next());
      assertEquals(ERROR_FIELD, iterator.next());

      notAllowed = resolver.getNotAllowedDeclaredFields(object);
      assertEquals(1, notAllowed.size());
      assertEquals(true, notAllowed.contains(ERROR_FIELD));

      remaining = resolver.getRemainingAllowedFields(object);
      assertEquals(4, remaining.size());
      assertEquals(true, !remaining.contains(OsFieldEnum.tx_author_id));
   }

   @Test
   public void testArtifactFields() {
      OsCollectObjectExpression object = newCollectObject("artifacts", "*");

      SortedSet<? extends OsField> expected = OsFieldEnum.getFieldsFor(Family.ARTIFACT);

      Set<? extends OsField> allowedFields = resolver.getAllowedFields(object);
      assertEquals(8, allowedFields.size());
      assertEquals(expected, allowedFields);

      // spot check
      assertEquals(true, allowedFields.contains(OsFieldEnum.art_guid));
      assertEquals(true, allowedFields.contains(OsFieldEnum.attributes));
      assertEquals(true, allowedFields.contains(OsFieldEnum.relations));
      assertEquals(true, allowedFields.contains(OsFieldEnum.art_txs));

      Set<? extends OsField> declaredFields = resolver.getDeclaredFields(object);
      assertEquals(expected, declaredFields);

      Set<? extends OsField> notAllowed = resolver.getNotAllowedDeclaredFields(object);
      assertEquals(0, notAllowed.size());

      Set<? extends OsField> remaining = resolver.getRemainingAllowedFields(object);
      assertEquals(0, remaining.size());

      ///// Individual Fields
      object = newCollectObject("artifacts", "id", "guid", "attributes", "relations", "txs", "error");

      declaredFields = resolver.getDeclaredFields(object);
      assertEquals(6, declaredFields.size());
      Iterator<? extends OsField> iterator = declaredFields.iterator();
      assertEquals(OsFieldEnum.art_id, iterator.next());
      assertEquals(OsFieldEnum.art_guid, iterator.next());
      assertEquals(OsFieldEnum.attributes, iterator.next());
      assertEquals(OsFieldEnum.relations, iterator.next());
      assertEquals(OsFieldEnum.art_txs, iterator.next());
      assertEquals(ERROR_FIELD, iterator.next());

      notAllowed = resolver.getNotAllowedDeclaredFields(object);
      assertEquals(1, notAllowed.size());
      assertEquals(true, notAllowed.contains(ERROR_FIELD));

      remaining = resolver.getRemainingAllowedFields(object);
      assertEquals(3, remaining.size());
      assertEquals(true, !remaining.contains(OsFieldEnum.art_guid));
   }

   @Test
   public void testAttributeFields() {
      OsCollectObjectExpression object = newCollectObject("attributes", "*");

      SortedSet<? extends OsField> expected = OsFieldEnum.getFieldsFor(Family.ATTRIBUTE);

      Set<? extends OsField> allowedFields = resolver.getAllowedFields(object);
      assertEquals(8, allowedFields.size());
      assertEquals(expected, allowedFields);

      // spot check
      assertEquals(true, allowedFields.contains(OsFieldEnum.attr_ds_uri));
      assertEquals(true, allowedFields.contains(OsFieldEnum.attr_ds_value));
      assertEquals(true, allowedFields.contains(OsFieldEnum.attr_type));
      assertEquals(true, allowedFields.contains(OsFieldEnum.attr_txs));

      Set<? extends OsField> declaredFields = resolver.getDeclaredFields(object);
      assertEquals(expected, declaredFields);

      Set<? extends OsField> notAllowed = resolver.getNotAllowedDeclaredFields(object);
      assertEquals(0, notAllowed.size());

      Set<? extends OsField> remaining = resolver.getRemainingAllowedFields(object);
      assertEquals(0, remaining.size());

      ///// Individual Fields
      object = newCollectObject("attributes", "ds-value", "ds-uri", "id", "txs", "error");

      declaredFields = resolver.getDeclaredFields(object);
      assertEquals(5, declaredFields.size());
      Iterator<? extends OsField> iterator = declaredFields.iterator();
      assertEquals(OsFieldEnum.attr_ds_value, iterator.next());
      assertEquals(OsFieldEnum.attr_ds_uri, iterator.next());
      assertEquals(OsFieldEnum.attr_id, iterator.next());
      assertEquals(OsFieldEnum.attr_txs, iterator.next());
      assertEquals(ERROR_FIELD, iterator.next());

      notAllowed = resolver.getNotAllowedDeclaredFields(object);
      assertEquals(1, notAllowed.size());
      assertEquals(true, notAllowed.contains(ERROR_FIELD));

      remaining = resolver.getRemainingAllowedFields(object);
      assertEquals(4, remaining.size());
      assertEquals(true, !remaining.contains(OsFieldEnum.attr_ds_uri));
   }

   @Test
   public void testRelationFields() {
      OsCollectObjectExpression object = newCollectObject("relations", "*");

      SortedSet<? extends OsField> expected = OsFieldEnum.getFieldsFor(Family.RELATION);

      Set<? extends OsField> allowedFields = resolver.getAllowedFields(object);
      assertEquals(8, allowedFields.size());
      assertEquals(expected, allowedFields);

      // spot check
      assertEquals(true, allowedFields.contains(OsFieldEnum.rel_type));
      assertEquals(true, allowedFields.contains(OsFieldEnum.rel_a_art_id));
      assertEquals(true, allowedFields.contains(OsFieldEnum.rel_b_art_id));
      assertEquals(true, allowedFields.contains(OsFieldEnum.rel_rationale));
      assertEquals(true, allowedFields.contains(OsFieldEnum.rel_txs));

      Set<? extends OsField> declaredFields = resolver.getDeclaredFields(object);
      assertEquals(expected, declaredFields);

      Set<? extends OsField> notAllowed = resolver.getNotAllowedDeclaredFields(object);
      assertEquals(0, notAllowed.size());

      Set<? extends OsField> remaining = resolver.getRemainingAllowedFields(object);
      assertEquals(0, remaining.size());

      ///// Individual Fields
      object = newCollectObject("relations", "id", "rationale", "side-A-id", "txs", "error");

      declaredFields = resolver.getDeclaredFields(object);
      assertEquals(5, declaredFields.size());
      Iterator<? extends OsField> iterator = declaredFields.iterator();
      assertEquals(OsFieldEnum.rel_id, iterator.next());
      assertEquals(OsFieldEnum.rel_rationale, iterator.next());
      assertEquals(OsFieldEnum.rel_a_art_id, iterator.next());
      assertEquals(OsFieldEnum.rel_txs, iterator.next());
      assertEquals(ERROR_FIELD, iterator.next());

      notAllowed = resolver.getNotAllowedDeclaredFields(object);
      assertEquals(1, notAllowed.size());
      assertEquals(true, notAllowed.contains(ERROR_FIELD));

      remaining = resolver.getRemainingAllowedFields(object);
      assertEquals(4, remaining.size());
      assertEquals(true, !remaining.contains(OsFieldEnum.rel_rationale));
   }

}
