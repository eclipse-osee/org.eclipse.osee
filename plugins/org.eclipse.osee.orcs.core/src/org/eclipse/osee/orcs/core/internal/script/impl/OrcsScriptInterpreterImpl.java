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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.core.ds.DynamicObject;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptAssembler;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptInterpreter;
import org.eclipse.osee.orcs.script.dsl.IExpressionResolver;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver.OsField;
import org.eclipse.osee.orcs.script.dsl.OsFieldEnum;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactGuidCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryAll;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactQueryByPredicate;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeEqualsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsArtifactTypeInstanceOfClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsAttributeExistClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchiveFilter;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchivedCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchChildOfClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNameEqualsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchNamePatternClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchParentOfClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryAll;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryById;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchQueryByPredicate;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchState;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchStateCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchType;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchTypeCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectAllFieldsExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectFieldExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsCollectObjectExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExistenceOperator;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsFollowRelationType;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsLimitClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsNonEqualOperator;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsOperator;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryOption;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQueryStatement;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelatedToClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationExistClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsRelationSide;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxAuthorIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxBranchIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentEqualsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommentPatternClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdEqualsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxCommitIdIsNullClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxHeadOfBranchIdCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdEqualsClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdOpClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxIdRangeClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryAll;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryById;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryByPredicate;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampOpClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTimestampRangeClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxType;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxTypeCriteria;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OseAttributeOpClause;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.ScriptVersion;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.util.OrcsScriptDslSwitch;
import org.eclipse.osee.orcs.search.ArtifactQueryBuilder;
import org.eclipse.osee.orcs.search.BranchQueryBuilder;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.TxQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptInterpreterImpl implements OrcsScriptInterpreter {

   private final OrcsTypes orcsTypes;
   private final IExpressionResolver resolver;
   private final IFieldResolver fieldResolver;

   public OrcsScriptInterpreterImpl(OrcsTypes orcsTypes, IExpressionResolver resolver, IFieldResolver fieldResolver) {
      super();
      this.orcsTypes = orcsTypes;
      this.resolver = resolver;
      this.fieldResolver = fieldResolver;
   }

   @Override
   public void interpret(OrcsScript model, OrcsScriptAssembler assembler) {
      try {
         assembler.onCompileStart(model);
         OrcsScriptModelVisitor translator = new OrcsScriptModelVisitor(assembler);
         Iterator<EObject> iterator = EcoreUtil.getAllContents(model, true);
         while (iterator.hasNext()) {
            EObject next = iterator.next();
            translator.doSwitch(next);
         }
      } catch (Exception ex) {
         assembler.onError(ex);
      } finally {
         assembler.onCompileEnd();
      }
   }

   private RelationTypeToken getRelationType(String name) {
      RelationTypeToken toReturn = null;
      for (RelationTypeToken type : orcsTypes.getRelationTypes().getAll()) {
         if (type.getName().equals(name)) {
            toReturn = type;
            break;
         }
      }
      return toReturn;
   }

   private ArtifactTypeToken getArtifactType(String name) {
      ArtifactTypeToken toReturn = null;
      for (ArtifactTypeToken type : orcsTypes.getArtifactTypes().getAll()) {
         if (type.getName().equals(name)) {
            toReturn = type;
            break;
         }
      }
      return toReturn;
   }

   private final class OrcsScriptModelVisitor extends OrcsScriptDslSwitch<Void> {

      private final OrcsScriptAssembler assembler;

      public OrcsScriptModelVisitor(OrcsScriptAssembler assembler) {
         super();
         this.assembler = assembler;
      }

      @Override
      public Void caseScriptVersion(ScriptVersion object) {
         String version = object.getVersion();
         assembler.onScriptVersion(version);
         return null;
      }

      @Override
      public Void caseOsQueryStatement(OsQueryStatement object) {
         assembler.onQueryEnd();
         assembler.onQueryStart();
         return null;
      }

      ////////////////////////////// Collect

      @Override
      public Void caseOsCollectClause(OsCollectClause object) {
         int startLevel = assembler.getSelectSetIndex();

         DynamicObject parent = new DynamicObject("root", null);
         parent.setLevel(startLevel - 1);
         resolveCollectExpression(object.getExpression(), parent, startLevel);
         DynamicData data = Iterables.getFirst(parent.getChildren(), null);
         data.setParent(null);

         long limit = -1;
         OsLimitClause limitClause = object.getLimit();
         if (limitClause != null) {
            limit = resolver.resolveSingle(Long.class, limitClause.getLimit());
         }
         assembler.addCollect(data, limit);
         return null;
      }

      private void resolveCollectExpression(OsCollectExpression expression, DynamicObject parent, int level) {
         if (expression instanceof OsCollectAllFieldsExpression) {
            for (OsField field : fieldResolver.getAllowedFields(expression)) {
               resolveOsField(field, parent, level);
            }
         } else {
            DynamicData child = null;
            if (expression instanceof OsCollectFieldExpression) {
               OsCollectFieldExpression fieldExpr = (OsCollectFieldExpression) expression;
               String alias = resolveAlias(fieldExpr.getAlias());
               String fieldLiteral = fieldExpr.getName();

               Set<? extends OsField> declaredFields = fieldResolver.getDeclaredFields(fieldExpr);
               OsField field = findField(declaredFields, fieldLiteral);
               if (field != null) {
                  child = new DynamicData(field.getId(), alias);
                  child.setFieldName(field.getLiteral());
                  child.setLevel(level);
               } else {
                  throw new OseeStateException("unable to find field for [%s] - declared [%s]", fieldLiteral,
                     declaredFields);
               }
            } else if (expression instanceof OsCollectObjectExpression) {
               OsCollectObjectExpression objectExpr = (OsCollectObjectExpression) expression;
               String alias = resolveAlias(objectExpr.getAlias());
               String objectType = objectExpr.getName();

               String id = objectType;
               String literal = objectType;

               if ("txs".equals(objectType)) {
                  // determine whether its art_txs, attr_txs, or rel_txs
                  if (!(objectExpr.eContainer() instanceof OsCollectClause)) {
                     String value = parent.getGuid();
                     OsFieldEnum field = null;
                     if ("artifacts".equals(value)) {
                        field = OsFieldEnum.art_txs;
                     } else if ("attributes".equals(value)) {
                        field = OsFieldEnum.attr_txs;
                     } else if ("relations".equals(value)) {
                        field = OsFieldEnum.rel_txs;
                     }

                     if (field != null) {
                        id = field.getId();
                        literal = field.getLiteral();
                     }
                  }
               }

               DynamicObject dynamicObject = new DynamicObject(id, alias);
               dynamicObject.setFieldName(literal);
               dynamicObject.setLevel(level);

               for (OsCollectExpression childExpr : objectExpr.getExpressions()) {
                  resolveCollectExpression(childExpr, dynamicObject, level);
               }
               child = dynamicObject;
            }
            if (parent != null && child != null) {
               parent.addChild(child);
            }
         }
      }

      private OsField findField(Set<? extends OsField> fields, String literal) {
         OsField found = null;
         for (OsField field : fields) {
            if (literal.equals(field.getLiteral())) {
               found = field;
               break;
            }
         }
         return found;
      }

      private void resolveOsField(OsField field, DynamicObject parent, int level) {
         String fieldName = field.getLiteral();
         if (field.hasChildren()) {
            int newObjectLevel = level;
            DynamicObject child = new DynamicObject(field.getId(), null);
            child.setFieldName(fieldName);

            child.setLevel(newObjectLevel);
            for (OsField childField : field.getChildren()) {
               resolveOsField(childField, child, newObjectLevel);
            }
            parent.addChild(child);
         } else {
            DynamicData child = new DynamicData(field.getId(), null);
            child.setFieldName(fieldName);
            child.setLevel(level);
            parent.addChild(child);
         }
      }

      private String resolveAlias(OsExpression aliasExpr) {
         String alias = null;
         if (aliasExpr != null) {
            alias = resolver.resolveSingle(String.class, aliasExpr);
         }
         return alias;
      }

      ////////////////////////////// Tx Query;
      private TxQueryBuilder<?> newTxQuery() {
         return assembler.newTxQuery();
      }

      private TxQueryBuilder<?> getTxQuery() {
         return assembler.getTxQuery();
      }

      @Override
      public Void caseOsTxQueryById(OsTxQueryById object) {
         int id = resolver.resolveSingle(Integer.class, object.getName());
         newTxQuery().andTxId(TransactionId.valueOf(id));
         return null;
      }

      @Override
      public Void caseOsTxQueryAll(OsTxQueryAll object) {
         newTxQuery();
         return null;
      }

      @Override
      public Void caseOsTxQueryByPredicate(OsTxQueryByPredicate object) {
         newTxQuery();
         return null;
      }

      @Override
      public Void caseOsTxIdEqualsClause(OsTxIdEqualsClause object) {
         List<Long> ids = resolver.resolve(Long.class, object.getIds());
         getTxQuery().andTxIds(Lists.transform(ids, TransactionId::valueOf));
         return null;
      }

      @Override
      public Void caseOsTxIdOpClause(OsTxIdOpClause object) {
         Operator op = asOperator(object.getOp());
         int id = resolver.resolveSingle(Integer.class, object.getId());
         getTxQuery().andTxId(op, id);
         return null;
      }

      @Override
      public Void caseOsTxIdRangeClause(OsTxIdRangeClause object) {
         int fromId = resolver.resolveSingle(Integer.class, object.getFromId());
         int toId = resolver.resolveSingle(Integer.class, object.getToId());
         getTxQuery().andTxId(Operator.GREATER_THAN_EQ, fromId, Operator.LESS_THAN_EQ, toId);
         return null;
      }

      @Override
      public Void caseOsTxTypeCriteria(OsTxTypeCriteria object) {
         List<OsTxType> states = object.getTypes();
         getTxQuery().andIs(asTxTypes(states));
         return null;
      }

      @Override
      public Void caseOsTxCommentEqualsClause(OsTxCommentEqualsClause object) {
         String value = resolver.resolveSingle(String.class, object.getValue());
         getTxQuery().andCommentEquals(value);
         return null;
      }

      @Override
      public Void caseOsTxCommentPatternClause(OsTxCommentPatternClause object) {
         String value = resolver.resolveSingle(String.class, object.getValue());
         getTxQuery().andCommentPattern(value);
         return null;
      }

      @Override
      public Void caseOsTxBranchIdCriteria(OsTxBranchIdCriteria object) {
         Collection<Long> ids = resolver.resolve(Long.class, object.getIds());
         Set<BranchId> values = new LinkedHashSet<>();
         for (Long id : ids) {
            values.add(BranchId.valueOf(id));
         }
         getTxQuery().andBranchIds(values);
         return null;
      }

      @Override
      public Void caseOsTxAuthorIdCriteria(OsTxAuthorIdCriteria object) {
         Collection<Integer> ids = resolver.resolve(Integer.class, object.getIds());
         getTxQuery().andAuthorIds(Collections.transform(ids, ArtifactId::valueOf));
         return null;
      }

      @Override
      public Void caseOsTxCommitIdEqualsClause(OsTxCommitIdEqualsClause object) {
         Collection<Integer> ids = resolver.resolve(Integer.class, object.getIds());
         getTxQuery().andCommitIds(Collections.transform(ids, ArtifactId::valueOf));
         return null;
      }

      @Override
      public Void caseOsTxCommitIdIsNullClause(OsTxCommitIdIsNullClause object) {
         getTxQuery().andNullCommitId();
         return null;
      }

      @Override
      public Void caseOsTxTimestampOpClause(OsTxTimestampOpClause object) {
         Operator op = asOperator(object.getOp());
         Date date = resolver.resolveSingle(Date.class, object.getTimestamp());
         Timestamp timestamp = asTimestamp(date);
         getTxQuery().andDate(op, timestamp);
         return null;
      }

      @Override
      public Void caseOsTxTimestampRangeClause(OsTxTimestampRangeClause object) {
         Date from = resolver.resolveSingle(Date.class, object.getFrom());
         Date to = resolver.resolveSingle(Date.class, object.getTo());
         getTxQuery().andDate(asTimestamp(from), asTimestamp(to));
         return null;
      }

      @Override
      public Void caseOsTxHeadOfBranchIdCriteria(OsTxHeadOfBranchIdCriteria object) {
         Long id = resolver.resolveSingle(Long.class, object.getId());
         getTxQuery().andIsHead(BranchId.valueOf(id));
         return null;
      }

      ////////////////////////////// Branch Query;
      private BranchQueryBuilder<?> newBranchQuery() {
         return assembler.newBranchQuery();
      }

      private BranchQueryBuilder<?> getBranchQuery() {
         return assembler.getBranchQuery();
      }

      @Override
      public Void caseOsBranchQueryById(OsBranchQueryById object) {
         OsExpression expression = object.getName();
         Class<?> resolvedType = resolver.resolveTypeSingle(expression);
         if (String.class.isAssignableFrom(resolvedType)) {
            String value = resolver.resolveSingle(String.class, expression);
            newBranchQuery().andNameEquals(value);
         } else {
            Long id = resolver.resolveSingle(Long.class, expression);
            newBranchQuery().andId(BranchId.valueOf(id));
         }
         return null;
      }

      @Override
      public Void caseOsBranchQueryAll(OsBranchQueryAll object) {
         newBranchQuery();
         return null;
      }

      @Override
      public Void caseOsBranchQueryByPredicate(OsBranchQueryByPredicate object) {
         newBranchQuery();
         return null;
      }

      @Override
      public Void caseOsBranchIdCriteria(OsBranchIdCriteria object) {
         Collection<Long> ids = resolver.resolve(Long.class, object.getIds());
         Collection<BranchId> branchIds = new ArrayList<>(ids.size());
         ids.forEach(id -> branchIds.add(BranchId.valueOf(id)));
         getBranchQuery().andIds(branchIds);
         return null;
      }

      @Override
      public Void caseOsBranchChildOfClause(OsBranchChildOfClause object) {
         Long id = resolver.resolveSingle(Long.class, object.getId());
         BranchId branch = asBranch(id);
         getBranchQuery().andIsChildOf(branch);
         return null;
      }

      @Override
      public Void caseOsBranchParentOfClause(OsBranchParentOfClause object) {
         Long id = resolver.resolveSingle(Long.class, object.getId());
         BranchId branch = asBranch(id);
         getBranchQuery().andIsAncestorOf(branch);
         return null;
      }

      @Override
      public Void caseOsBranchNameEqualsClause(OsBranchNameEqualsClause object) {
         String value = resolver.resolveSingle(String.class, object.getValue());
         getBranchQuery().andNameEquals(value);
         return null;
      }

      @Override
      public Void caseOsBranchNamePatternClause(OsBranchNamePatternClause object) {
         String value = resolver.resolveSingle(String.class, object.getValue());
         getBranchQuery().andNamePattern(value);
         return null;
      }

      @Override
      public Void caseOsBranchStateCriteria(OsBranchStateCriteria object) {
         List<OsBranchState> states = object.getStates();
         getBranchQuery().andStateIs(asStates(states));
         return null;
      }

      @Override
      public Void caseOsBranchTypeCriteria(OsBranchTypeCriteria object) {
         List<OsBranchType> states = object.getTypes();
         getBranchQuery().andIsOfType(asBranchTypes(states));
         return null;
      }

      @Override
      public Void caseOsBranchArchivedCriteria(OsBranchArchivedCriteria object) {
         OsBranchArchiveFilter filter = object.getFilter();
         if (OsBranchArchiveFilter.ARCHIVED_EXCLUDED == filter) {
            getBranchQuery().excludeArchived();
         } else {
            getBranchQuery().includeArchived();
         }
         return null;
      }

      //////////////////////////////Artifact Query;
      private ArtifactQueryBuilder<?> newArtifactQuery() {
         return assembler.newArtifactQuery();
      }

      private ArtifactQueryBuilder<?> getArtifactQuery() {
         return assembler.getArtifactQuery();
      }

      @Override
      public Void caseOsArtifactQueryAll(OsArtifactQueryAll object) {
         newArtifactQuery();
         return null;
      }

      @Override
      public Void caseOsArtifactQueryByPredicate(OsArtifactQueryByPredicate object) {
         newArtifactQuery();
         return null;
      }

      @Override
      public Void caseOsArtifactIdCriteria(OsArtifactIdCriteria object) {
         Collection<Long> ids = resolver.resolve(Long.class, object.getIds());
         getArtifactQuery().andUuids(ids);
         return null;
      }

      @Override
      public Void caseOsArtifactGuidCriteria(OsArtifactGuidCriteria object) {
         Collection<String> ids = resolver.resolve(String.class, object.getIds());
         getArtifactQuery().andGuids(ids);
         return null;
      }

      @Override
      public Void caseOsArtifactTypeEqualsClause(OsArtifactTypeEqualsClause object) {
         getArtifactQuery().andTypeEquals(asArtifactTypes(object.getTypes()));
         return null;
      }

      @Override
      public Void caseOsArtifactTypeInstanceOfClause(OsArtifactTypeInstanceOfClause object) {
         getArtifactQuery().andIsOfType(asArtifactTypes(object.getTypes()));
         return null;
      }

      @Override
      public Void caseOseAttributeOpClause(OseAttributeOpClause object) {
         Collection<AttributeTypeId> types = asAttributeTypes(object.getTypes());
         Collection<String> values = resolver.resolve(String.class, object.getValues());
         QueryOption[] options = asQueryOptions(object.getOptions());
         getArtifactQuery().and(types, values, options);
         return null;
      }

      @Override
      public Void caseOsAttributeExistClause(OsAttributeExistClause object) {
         getArtifactQuery().andExists(asAttributeTypes(object.getTypes()));
         return null;
      }

      @Override
      public Void caseOsRelationExistClause(OsRelationExistClause object) {
         OsExistenceOperator op = object.getOp();
         if (object.getSide() == null) {
            IRelationType type = asRelationType(object.getType());
            if (OsExistenceOperator.EXISTS == op) {
               getArtifactQuery().andExists(type);
            } else {
               getArtifactQuery().andNotExists(type);
            }
         } else {
            RelationTypeSide typeSide = asRelationTypeSide(object.getType(), object.getSide());
            if (OsExistenceOperator.EXISTS == op) {
               getArtifactQuery().andExists(typeSide);
            } else {
               getArtifactQuery().andNotExists(typeSide);
            }
         }
         return null;
      }

      @Override
      public Void caseOsRelatedToClause(OsRelatedToClause object) {
         RelationTypeSide typeSide = asRelationTypeSide(object.getType(), object.getSide());
         Collection<Long> ids = resolver.resolve(Long.class, object.getIds());
         getArtifactQuery().andRelatedTo(typeSide,
            ids.stream().map(id -> ArtifactId.valueOf(id)).collect(Collectors.toList()));
         return null;
      }

      @Override
      public Void caseOsFollowRelationType(OsFollowRelationType object) {
         RelationTypeSide typeSide = asRelationTypeSide(object.getType(), object.getSide());
         getArtifactQuery().followRelation(typeSide);
         return null;
      }

      ////////////////////////////// Call handler to get actual types ?;
      private Collection<ArtifactTypeId> asArtifactTypes(List<OsExpression> expressions) {
         Set<ArtifactTypeId> toReturn = new LinkedHashSet<>();

         List<Class<?>> resolvedTypes = resolver.resolveTypes(expressions);
         for (int index = 0; index < resolvedTypes.size(); index++) {
            Class<?> clazz = resolvedTypes.get(index);
            OsExpression expression = expressions.get(index);
            if (clazz.isAssignableFrom(String.class)) {
               String name = resolver.resolveSingle(String.class, expression);
               toReturn.add(getArtifactType(name));
            } else {
               long typeId = resolver.resolveSingle(Long.class, expression);
               toReturn.add(orcsTypes.getArtifactTypes().get(typeId));
            }
         }
         return toReturn;
      }

      private Collection<AttributeTypeId> asAttributeTypes(List<OsExpression> expressions) {
         Set<AttributeTypeId> toReturn = new LinkedHashSet<>();
         List<Class<?>> resolvedTypes = resolver.resolveTypes(expressions);
         for (int index = 0; index < resolvedTypes.size(); index++) {
            Class<?> clazz = resolvedTypes.get(index);
            OsExpression expression = expressions.get(index);
            if (clazz.isAssignableFrom(String.class)) {
               String name = resolver.resolveSingle(String.class, expression);
               toReturn.add(orcsTypes.getAttributeTypes().getByName(name));
            } else {
               long typeId = resolver.resolveSingle(Long.class, expression);
               toReturn.add(orcsTypes.getAttributeTypes().get(typeId));
            }
         }
         return toReturn;
      }

      private IRelationType asRelationType(OsExpression expression) {
         IRelationType toReturn;
         Class<?> clazz = resolver.resolveTypeSingle(expression);
         if (clazz.isAssignableFrom(String.class)) {
            String name = resolver.resolveSingle(String.class, expression);
            toReturn = getRelationType(name);
         } else {
            long typeId = resolver.resolveSingle(Long.class, expression);
            toReturn = orcsTypes.getRelationTypes().get(typeId);
         }
         return toReturn;
      }

      private RelationTypeSide asRelationTypeSide(OsExpression expression, OsRelationSide side) {
         RelationTypeSide toReturn;
         Class<?> clazz = resolver.resolveTypeSingle(expression);
         if (clazz.isAssignableFrom(String.class)) {
            String name = resolver.resolveSingle(String.class, expression);
            RelationTypeToken type = getRelationType(name);
            toReturn = RelationTypeSide.create(type, asSide(side));
         } else {
            long typeId = resolver.resolveSingle(Long.class, expression);
            toReturn = RelationTypeSide.create(asSide(side), typeId, "N/A");
         }
         return toReturn;
      }

      ////////////////////////////// Functions;
      private BranchId asBranch(Long typeId) {
         return BranchId.valueOf(typeId);
      }

      private Timestamp asTimestamp(Date date) {
         return new Timestamp(date.getTime());
      }

      private Operator asOperator(OsOperator op) {
         return Operator.valueOf(op.getName());
      }

      private Operator asOperator(OsNonEqualOperator op) {
         return Operator.valueOf(op.getName());
      }

      private RelationSide asSide(OsRelationSide side) {
         return RelationSide.valueOf(side.getName());
      }

      private TransactionDetailsType[] asTxTypes(List<OsTxType> values) {
         Set<TransactionDetailsType> toReturn = new LinkedHashSet<>();
         for (OsTxType value : values) {
            TransactionDetailsType type = TransactionDetailsType.Baselined;
            if (OsTxType.NON_BASELINE == value) {
               type = TransactionDetailsType.NonBaselined;
            }
            toReturn.add(type);
         }
         return toReturn.toArray(new TransactionDetailsType[toReturn.size()]);
      }

      private BranchState[] asStates(List<OsBranchState> values) {
         Set<BranchState> toReturn = new LinkedHashSet<>();
         for (OsBranchState value : values) {
            BranchState state = BranchState.fromName(value.getName());
            toReturn.add(state);
         }
         return toReturn.toArray(new BranchState[toReturn.size()]);
      }

      private BranchType[] asBranchTypes(List<OsBranchType> values) {
         Set<BranchType> toReturn = new LinkedHashSet<>();
         for (OsBranchType value : values) {
            BranchType type = BranchType.fromName(value.getName());
            toReturn.add(type);
         }
         return toReturn.toArray(new BranchType[toReturn.size()]);
      }

      private QueryOption[] asQueryOptions(List<OsQueryOption> options) {
         Set<QueryOption> toReturn = new LinkedHashSet<>();
         for (OsQueryOption op : options) {
            if (OsQueryOption.CONTAINS == op) {
               toReturn.add(QueryOption.CASE__IGNORE);
               toReturn.add(QueryOption.TOKEN_MATCH_ORDER__MATCH);
               toReturn.add(QueryOption.TOKEN_DELIMITER__ANY);
               toReturn.add(QueryOption.TOKEN_COUNT__IGNORE);
            } else {
               toReturn.add(QueryOption.valueOf(op.getName()));
            }
         }
         return toReturn.toArray(new QueryOption[options.size()]);
      }

   }
}
