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
package org.eclipse.osee.orcs.core.internal.script.impl;

import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_gamma_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_guid;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_mod_type;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_tx_author_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_tx_branch_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_tx_comment;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_tx_commit_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_tx_current;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_tx_date;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_tx_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_tx_type;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_txs;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.art_type;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_ds_uri;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_ds_value;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_gamma_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_mod_type;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_tx_author_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_tx_branch_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_tx_comment;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_tx_commit_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_tx_current;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_tx_date;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_tx_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_tx_type;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_txs;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_type;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attr_value;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.attributes;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.branch_archive_state;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.branch_associated_art_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.branch_baseline_tx_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.branch_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.branch_inherit_access_cntrl;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.branch_name;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.branch_parent_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.branch_parent_tx_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.branch_state;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.branch_type;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_a_art_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_b_art_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_gamma_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_mod_type;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_rationale;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_tx_author_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_tx_branch_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_tx_comment;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_tx_commit_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_tx_current;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_tx_date;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_tx_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_tx_type;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_txs;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.rel_type;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.relations;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.tx_author_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.tx_branch_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.tx_comment;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.tx_commit_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.tx_current;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.tx_date;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.tx_id;
import static org.eclipse.osee.orcs.script.dsl.OsFieldEnum.tx_type;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.core.ds.DynamicObject;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.SelectSet;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeFollow;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIds;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptInterpreter;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptOutputHandler;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.eclipse.osee.orcs.script.dsl.IExpressionResolver;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslResource;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.OsFieldEnum;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link OrcsScriptInterpreterImpl} {@link OrcsScriptAssemblerImpl}
 *
 * @author Roberto E. Escobar
 */
public class OrcsScriptInterpreterTest {

   //@formatter:off
   @Mock private DataModule dataModule;
   @Mock private OrcsTypes orcsTypes;
   @Mock private OrcsScriptOutputHandler output;

   @Mock private ArtifactTypes artTypes;
   @Mock private AttributeTypes attrTypes;
   @Mock private RelationTypes relTypes;

   //@formatter:on

   private OrcsScriptAssemblerImpl assembler;
   private OrcsScriptInterpreter interpreter;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      IExpressionResolver resolver = OrcsScriptUtil.getExpressionResolver();
      IFieldResolver fieldResolver = OrcsScriptUtil.getFieldResolver();

      assembler = new OrcsScriptAssemblerImpl(dataModule, orcsTypes, output);
      interpreter = new OrcsScriptInterpreterImpl(orcsTypes, resolver, fieldResolver);

      when(orcsTypes.getArtifactTypes()).thenReturn(artTypes);
      when(orcsTypes.getAttributeTypes()).thenReturn(attrTypes);
      when(orcsTypes.getRelationTypes()).thenReturn(relTypes);

      List<IArtifactType> artTypeList = Collections.emptyList();
      List<AttributeTypeId> attrTypeList = Collections.emptyList();
      List<IRelationType> relTypeList = Arrays.asList((IRelationType) CoreRelationTypes.Default_Hierarchical__Child);

      when(artTypes.getAll()).thenAnswer(answer(artTypeList));
      when(attrTypes.getAll()).thenAnswer(answer(attrTypeList));
      when(relTypes.getAll()).thenAnswer(answer(relTypeList));
   }

   private static <T> Answer<T> answer(final T object) {
      return new Answer<T>() {
         @Override
         public T answer(InvocationOnMock invocation) throws Throwable {
            return object;
         }
      };
   }

   @Test
   public void testBranchQuery() {
      OrcsScript model = newModel("start from branch 570 collect branches {*};");
      interpreter.interpret(model, assembler);
      verify(output, times(0)).onError(Matchers.any(Throwable.class));

      QueryData queryData = Iterables.getFirst(assembler.getQueries(), null);
      assertNotNull(queryData);

      CriteriaSet criteriaSet = Iterables.getFirst(queryData.getCriteriaSets(), null);
      assertEquals(1, criteriaSet.getCriterias().size());
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaBranchIds.class));

      SelectSet selectSet = Iterables.getFirst(queryData.getSelectSets(), null);
      assertEquals(-1, selectSet.getLimit());

      DynamicObject data = asObject(selectSet.getData());
      assertEquals("branches", data.getName());
      assertEquals(intVal(0), data.getLevel());
      assertEquals(false, data.hasParent());
      assertEquals(true, data.hasChildren());

      checkNamesAndLevels(data.getChildren(), 0, //
         branch_archive_state, //
         branch_associated_art_id, //
         branch_baseline_tx_id, //
         branch_id, //
         branch_inherit_access_cntrl, //
         branch_name, //
         branch_parent_id, //
         branch_parent_tx_id, //
         branch_state, //
         branch_type //
      );
   }

   @Test
   public void testTxQuery() {
      OrcsScript model = newModel("start from tx 570 collect txs {*};");
      interpreter.interpret(model, assembler);
      verify(output, times(0)).onError(Matchers.any(Throwable.class));

      QueryData queryData = Iterables.getFirst(assembler.getQueries(), null);
      assertNotNull(queryData);

      CriteriaSet criteriaSet = Iterables.getFirst(queryData.getCriteriaSets(), null);
      assertEquals(1, criteriaSet.getCriterias().size());
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaTxIds.class));

      SelectSet selectSet = Iterables.getFirst(queryData.getSelectSets(), null);
      assertEquals(-1, selectSet.getLimit());

      DynamicObject data = asObject(selectSet.getData());
      assertEquals("txs", data.getName());
      assertEquals(intVal(0), data.getLevel());
      assertEquals(false, data.hasParent());
      assertEquals(true, data.hasChildren());

      checkNamesAndLevels(data.getChildren(), 0, //
         tx_author_id, //
         tx_branch_id, //
         tx_comment, //
         tx_commit_id, //
         tx_current, //
         tx_date, //
         tx_id, //
         tx_type //
      );
   }

   @Test
   public void testArtifactQuery1() {
      OrcsScript model = newModel("start from branch 570 find artifacts where art-id = 34 collect artifacts {*};");
      interpreter.interpret(model, assembler);
      verify(output, times(0)).onError(Matchers.any(Throwable.class));

      QueryData queryData = Iterables.getFirst(assembler.getQueries(), null);
      assertNotNull(queryData);

      CriteriaSet criteriaSet = Iterables.getFirst(queryData.getCriteriaSets(), null);
      assertEquals(2, criteriaSet.getCriterias().size());
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaBranchIds.class));
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaArtifactIds.class));

      SelectSet selectSet = Iterables.getFirst(queryData.getSelectSets(), null);
      assertEquals(-1, selectSet.getLimit());

      DynamicObject data = asObject(selectSet.getData());
      assertEquals("artifacts", data.getName());
      assertEquals(intVal(0), data.getLevel());
      assertEquals(false, data.hasParent());
      assertEquals(true, data.hasChildren());

      checkNamesAndLevels(data.getChildren(), 0, //
         art_gamma_id, //
         art_guid, //
         art_id, //
         art_mod_type, //
         art_type, //
         art_txs, //
         attributes, //
         relations);
   }

   @Test
   public void testArtifactQuery2() {
      OrcsScript model = newModel(
         "start from branch 570 find artifacts where art-id = 34 collect artifacts {id, txs{*}, attributes {*}, relations {*} };");
      interpreter.interpret(model, assembler);
      verify(output, times(0)).onError(Matchers.any(Throwable.class));

      QueryData queryData = Iterables.getFirst(assembler.getQueries(), null);
      assertNotNull(queryData);

      CriteriaSet criteriaSet = Iterables.getFirst(queryData.getCriteriaSets(), null);
      assertEquals(2, criteriaSet.getCriterias().size());
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaBranchIds.class));
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaArtifactIds.class));

      SelectSet selectSet = Iterables.getFirst(queryData.getSelectSets(), null);
      assertEquals(-1, selectSet.getLimit());

      DynamicObject data = asObject(selectSet.getData());
      assertEquals("artifacts", data.getName());
      assertEquals(intVal(0), data.getLevel());
      assertEquals(false, data.hasParent());
      assertEquals(true, data.hasChildren());

      checkNamesAndLevels(data.getChildren(), 0, //
         art_id, //
         art_txs, //
         attributes, //
         relations);

      DynamicObject artTxs = asObject(Iterables.get(data.getChildren(), 1, null));
      assertEquals(true, artTxs.hasParent());
      assertEquals(true, artTxs.hasChildren());
      checkNamesAndLevels(artTxs.getChildren(), 0, //
         art_tx_author_id, //
         art_tx_branch_id, //
         art_tx_comment, //
         art_tx_commit_id, //
         art_tx_current, //
         art_tx_date, //
         art_tx_id, //
         art_tx_type //
      );

      DynamicObject attrs = asObject(Iterables.get(data.getChildren(), 2, null));
      assertEquals(true, attrs.hasParent());
      assertEquals(true, attrs.hasChildren());
      checkNamesAndLevels(attrs.getChildren(), 0, //
         attr_ds_uri, //
         attr_ds_value, //
         attr_gamma_id, //
         attr_id, //
         attr_mod_type, //
         attr_type, //
         attr_value, //
         attr_txs);

      DynamicObject rels = asObject(Iterables.get(data.getChildren(), 3, null));
      assertEquals(true, rels.hasParent());
      assertEquals(true, rels.hasChildren());
      checkNamesAndLevels(rels.getChildren(), 0, //
         rel_gamma_id, //
         rel_id, //
         rel_mod_type, //
         rel_rationale, //
         rel_a_art_id, //
         rel_b_art_id, //
         rel_type, //
         rel_txs);
   }

   @Test
   public void testArtifactQuery3() {
      OrcsScript model = newModel(
         "start from branch 570 find artifacts where art-id = 34 collect artifacts {id, txs{*}, attributes {id, txs{*} }, relations {id, txs{*} } };");
      interpreter.interpret(model, assembler);
      verify(output, times(0)).onError(Matchers.any(Throwable.class));

      QueryData queryData = Iterables.getFirst(assembler.getQueries(), null);
      assertNotNull(queryData);

      CriteriaSet criteriaSet = Iterables.getFirst(queryData.getCriteriaSets(), null);
      assertEquals(2, criteriaSet.getCriterias().size());
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaBranchIds.class));
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaArtifactIds.class));

      SelectSet selectSet = Iterables.getFirst(queryData.getSelectSets(), null);
      assertEquals(-1, selectSet.getLimit());

      DynamicObject data = asObject(selectSet.getData());
      assertEquals("artifacts", data.getName());
      assertEquals(intVal(0), data.getLevel());
      assertEquals(false, data.hasParent());
      assertEquals(true, data.hasChildren());

      checkNamesAndLevels(data.getChildren(), 0, //
         art_id, //
         art_txs, //
         attributes, //
         relations);

      DynamicObject artTxs = asObject(Iterables.get(data.getChildren(), 1, null));
      assertEquals(true, artTxs.hasParent());
      assertEquals(true, artTxs.hasChildren());
      checkNamesAndLevels(artTxs.getChildren(), 0, //
         art_tx_author_id, //
         art_tx_branch_id, //
         art_tx_comment, //
         art_tx_commit_id, //
         art_tx_current, //
         art_tx_date, //
         art_tx_id, //
         art_tx_type //
      );

      DynamicObject attrs = asObject(Iterables.get(data.getChildren(), 2, null));
      assertEquals(true, attrs.hasParent());
      assertEquals(true, attrs.hasChildren());
      checkNamesAndLevels(attrs.getChildren(), 0, //
         attr_id, //
         attr_txs);

      DynamicObject attrTxs = asObject(Iterables.get(attrs.getChildren(), 1, null));
      assertEquals(true, attrTxs.hasParent());
      assertEquals(true, attrTxs.hasChildren());
      checkNamesAndLevels(attrTxs.getChildren(), 0, //
         attr_tx_author_id, //
         attr_tx_branch_id, //
         attr_tx_comment, //
         attr_tx_commit_id, //
         attr_tx_current, //
         attr_tx_date, //
         attr_tx_id, //
         attr_tx_type //
      );

      DynamicObject rels = asObject(Iterables.get(data.getChildren(), 3, null));
      assertEquals(true, rels.hasParent());
      assertEquals(true, rels.hasChildren());
      checkNamesAndLevels(rels.getChildren(), 0, //
         rel_id, //
         rel_txs);

      DynamicObject relTxs = asObject(Iterables.get(rels.getChildren(), 1, null));
      assertEquals(true, relTxs.hasParent());
      assertEquals(true, relTxs.hasChildren());
      checkNamesAndLevels(relTxs.getChildren(), 0, //
         rel_tx_author_id, //
         rel_tx_branch_id, //
         rel_tx_comment, //
         rel_tx_commit_id, //
         rel_tx_current, //
         rel_tx_date, //
         rel_tx_id, //
         rel_tx_type //
      );
   }

   @Test
   public void testFollowQuery() {
      OrcsScript model = newModel(
         "start from branch 570 find artifacts where art-id = 34 follow relation type = 'Default Hierarchical' to side-B collect artifacts {id, txs{ id }, attributes {id, txs{id} }, relations {id, txs{id} } }; ");
      interpreter.interpret(model, assembler);
      verify(output, times(0)).onError(Matchers.any(Throwable.class));

      QueryData queryData = Iterables.getFirst(assembler.getQueries(), null);
      assertNotNull(queryData);

      CriteriaSet criteriaSet = Iterables.getFirst(queryData.getCriteriaSets(), null);
      assertEquals(3, criteriaSet.getCriterias().size());
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaBranchIds.class));
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaArtifactIds.class));
      assertEquals(true, criteriaSet.hasCriteriaType(CriteriaRelationTypeFollow.class));

      List<SelectSet> selectSets = queryData.getSelectSets();
      assertEquals(2, selectSets.size());

      SelectSet selectSet1 = selectSets.get(0);
      assertNull(selectSet1.getData());
      assertEquals(-1, selectSet1.getLimit());

      SelectSet selectSet2 = selectSets.get(1);
      assertEquals(-1, selectSet2.getLimit());

      DynamicObject data = asObject(selectSet2.getData());
      assertEquals("artifacts", data.getName());
      assertEquals(intVal(1), data.getLevel());
      assertEquals(false, data.hasParent());
      assertEquals(true, data.hasChildren());
      checkNamesAndLevels(data.getChildren(), 1, //
         art_id, //
         art_txs, //
         attributes, //
         relations);

      DynamicObject artTxs = asObject(Iterables.get(data.getChildren(), 1, null));
      assertEquals(true, artTxs.hasParent());
      assertEquals(true, artTxs.hasChildren());
      checkNamesAndLevels(artTxs.getChildren(), 1, //
         art_tx_id //
      );

      DynamicObject attrs = asObject(Iterables.get(data.getChildren(), 2, null));
      assertEquals(true, attrs.hasParent());
      assertEquals(true, attrs.hasChildren());
      checkNamesAndLevels(attrs.getChildren(), 1, //
         attr_id, //
         attr_txs);

      DynamicObject attrTxs = asObject(Iterables.get(attrs.getChildren(), 1, null));
      assertEquals(true, attrTxs.hasParent());
      assertEquals(true, attrTxs.hasChildren());
      checkNamesAndLevels(attrTxs.getChildren(), 1, //
         attr_tx_id //
      );

      DynamicObject rels = asObject(Iterables.get(data.getChildren(), 3, null));
      assertEquals(true, rels.hasParent());
      assertEquals(true, rels.hasChildren());
      checkNamesAndLevels(rels.getChildren(), 1, //
         rel_id, //
         rel_txs);

      DynamicObject relTxs = asObject(Iterables.get(rels.getChildren(), 1, null));
      assertEquals(true, relTxs.hasParent());
      assertEquals(true, relTxs.hasChildren());
      checkNamesAndLevels(relTxs.getChildren(), 1, //
         rel_tx_id //
      );
   }

   private Integer intVal(int integer) {
      return integer;
   }

   private void checkNamesAndLevels(Iterable<DynamicData> data, Integer level, OsFieldEnum... vals) {
      List<OsFieldEnum> fields = Arrays.asList(vals);
      Iterator<DynamicData> iterator = data.iterator();
      Iterator<OsFieldEnum> fieldIt = fields.iterator();
      while (iterator.hasNext()) {
         DynamicData child = iterator.next();
         OsFieldEnum field = fieldIt.next();

         assertEquals(field.getLiteral(), child.getName());
         assertEquals(field.getId(), child.getGuid());
         assertEquals(field.getLiteral(), child.getFieldName());

         assertEquals("level error for [" + child.getName() + "]", level, child.getLevel());
      }
   }

   private DynamicObject asObject(DynamicData data) {
      return (DynamicObject) data;
   }

   private OrcsScript newModel(String script) {
      OrcsScriptDslResource resource =
         OrcsScriptUtil.loadModel(new ByteArrayInputStream(script.getBytes(Charsets.UTF_8)), "dummy:/dummy.orcs");
      return resource.getModel();
   }
}
