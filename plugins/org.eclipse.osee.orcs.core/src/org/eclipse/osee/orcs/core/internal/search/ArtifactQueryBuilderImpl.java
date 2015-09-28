/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.HasLocalId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.BranchCriteria;
import org.eclipse.osee.orcs.core.ds.criteria.TxCriteria;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.ArtifactQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactQueryBuilderImpl<T> implements ArtifactQueryBuilder<T> {

   private final CriteriaFactory criteriaFactory;
   private final QueryData queryData;

   public ArtifactQueryBuilderImpl(CriteriaFactory criteriaFactory, QueryData queryData) {
      this.criteriaFactory = criteriaFactory;
      this.queryData = queryData;
   }

   private QueryData getQueryData() {
      return queryData;
   }

   private Options getOptions() {
      return queryData.getOptions();
   }

   @Override
   public T includeDeletedAttributes() {
      return includeDeletedAttributes(true);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T includeDeletedAttributes(boolean enabled) {
      OptionsUtil.setIncludeDeletedAttributes(getOptions(), enabled);
      return (T) this;
   }

   @Override
   public boolean areDeletedAttributesIncluded() {
      return OptionsUtil.areDeletedAttributesIncluded(getOptions());
   }

   @Override
   public T includeDeletedRelations() {
      return includeDeletedRelations(true);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T includeDeletedRelations(boolean enabled) {
      OptionsUtil.setIncludeDeletedRelations(getOptions(), enabled);
      return (T) this;
   }

   @Override
   public boolean areDeletedRelationsIncluded() {
      return OptionsUtil.areDeletedRelationsIncluded(getOptions());
   }

   @Override
   public T includeDeletedArtifacts() {
      return includeDeletedArtifacts(true);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T includeDeletedArtifacts(boolean enabled) {
      OptionsUtil.setIncludeDeletedArtifacts(getOptions(), enabled);
      return (T) this;
   }

   @Override
   public boolean areDeletedArtifactsIncluded() {
      return OptionsUtil.areDeletedArtifactsIncluded(getOptions());
   }

   @SuppressWarnings("unchecked")
   @Override
   public T fromTransaction(int transactionId) {
      OptionsUtil.setFromTransaction(getOptions(), transactionId);
      return (T) this;
   }

   @Override
   public int getFromTransaction() {
      return OptionsUtil.getFromTransaction(getOptions());
   }

   @SuppressWarnings("unchecked")
   @Override
   public T headTransaction() {
      OptionsUtil.setHeadTransaction(getOptions());
      return (T) this;
   }

   @Override
   public boolean isHeadTransaction() {
      return OptionsUtil.isHeadTransaction(getOptions());
   }

   @SuppressWarnings("unchecked")
   @Override
   public T excludeDeleted() {
      includeDeletedArtifacts(false);
      return (T) this;
   }

   @Override
   public T andUuid(long... uuids) throws OseeCoreException {
      List<Long> ids = new ArrayList<>(uuids.length);
      for (long id : uuids) {
         ids.add(id);
      }
      return andUuids(ids);
   }

   @Override
   public T andUuids(Collection<Long> artifactIds) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createArtifactIdCriteria(artifactIds);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andGuid(String id) throws OseeCoreException {
      return andGuids(Collections.singleton(id));
   }

   @SuppressWarnings("unchecked")
   @Override
   public T andGuids(Collection<String> ids) throws OseeCoreException {
      Set<String> guids = new HashSet<>();
      Set<String> invalids = new HashSet<>();
      for (String id : ids) {
         if (GUID.isValid(id)) {
            guids.add(id);
         } else {
            invalids.add(id);
         }
      }

      Conditions.checkExpressionFailOnTrue(!invalids.isEmpty(), "Invalid guids detected - %s", invalids);
      if (!guids.isEmpty()) {
         Criteria guidCriteria = criteriaFactory.createArtifactGuidCriteria(guids);
         addAndCheck(getQueryData(), guidCriteria);
      }
      return (T) this;
   }

   @Override
   public T andIsOfType(IArtifactType... artifactType) throws OseeCoreException {
      return andIsOfType(Arrays.asList(artifactType));
   }

   @Override
   public T andIsOfType(Collection<? extends IArtifactType> artifactType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createArtifactTypeCriteriaWithInheritance(artifactType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andTypeEquals(IArtifactType... artifactType) throws OseeCoreException {
      return andTypeEquals(Arrays.asList(artifactType));
   }

   @Override
   public T andTypeEquals(Collection<? extends IArtifactType> artifactType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createArtifactTypeCriteria(artifactType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andExists(IAttributeType... attributeType) throws OseeCoreException {
      return andExists(Arrays.asList(attributeType));
   }

   @Override
   public T andExists(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createExistsCriteria(attributeTypes);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNotExists(IAttributeType attributeType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(attributeType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andExists(IRelationType relationType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createExistsCriteria(relationType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNotExists(IRelationType relationType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(relationType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNotExists(IRelationTypeSide relationTypeSide) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(relationTypeSide);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andExists(IRelationTypeSide relationTypeSide) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createExistsCriteria(relationTypeSide);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T and(IAttributeType attributeType, Collection<String> values, QueryOption... options) throws OseeCoreException {
      return and(Collections.singleton(attributeType), values, options);
   }

   @Override
   public T and(IAttributeType attributeType, String value, QueryOption... options) throws OseeCoreException {
      return and(Collections.singleton(attributeType), Collections.singleton(value), options);
   }

   @Override
   public T and(Collection<IAttributeType> attributeTypes, String value, QueryOption... options) throws OseeCoreException {
      return and(attributeTypes, Collections.singleton(value), options);
   }

   @Override
   public T and(Collection<IAttributeType> attributeTypes, Collection<String> value, QueryOption... options) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createAttributeCriteria(attributeTypes, value, options);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNameEquals(String artifactName) throws OseeCoreException {
      return and(CoreAttributeTypes.Name, artifactName);
   }

   @Override
   public T andIds(Identifiable<String>... ids) throws OseeCoreException {
      return andIds(Arrays.asList(ids));
   }

   @Override
   public T andIds(Collection<? extends Identifiable<String>> ids) throws OseeCoreException {
      Set<String> guids = new HashSet<>();
      for (Identity<String> id : ids) {
         guids.add(id.getGuid());
      }
      return andGuids(guids);
   }

   @Override
   public T andRelatedTo(IRelationTypeSide relationTypeSide, ArtifactReadable... artifacts) throws OseeCoreException {
      return andRelatedTo(relationTypeSide, Arrays.asList(artifacts));
   }

   @Override
   public T andRelatedTo(IRelationTypeSide relationTypeSide, Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException {
      Set<Integer> ids = new HashSet<>();
      for (HasLocalId<Integer> token : artifacts) {
         ids.add(token.getLocalId());
      }
      return andRelatedToLocalIds(relationTypeSide, ids);
   }

   @Override
   public T andRelatedToLocalIds(IRelationTypeSide relationTypeSide, int... artifactIds) throws OseeCoreException {
      Set<Integer> ids = new HashSet<>();
      for (Integer id : artifactIds) {
         ids.add(id);
      }
      return andRelatedToLocalIds(relationTypeSide, ids);
   }

   @Override
   public T andRelatedToLocalIds(IRelationTypeSide relationTypeSide, Collection<Integer> artifactIds) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createRelatedToCriteria(relationTypeSide, artifactIds);
      return addAndCheck(getQueryData(), criteria);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T followRelation(IRelationTypeSide relationTypeSide) {
      Criteria criteria = criteriaFactory.createFollowRelationType(relationTypeSide);
      addAndCheck(getQueryData(), criteria);
      queryData.newCriteriaSet();
      return (T) this;
   }

   @SuppressWarnings("unchecked")
   private T addAndCheck(QueryData queryData, Criteria criteria) throws OseeCoreException {
      criteria.checkValid(getOptions());
      queryData.addCriteria(criteria);
      return (T) this;
   }

   private boolean hasOnlyBranchOrTxCriterias(Collection<Criteria> criterias) {
      boolean result = true;
      for (Criteria criteria : criterias) {
         if (!(criteria instanceof TxCriteria) && !(criteria instanceof BranchCriteria)) {
            result = false;
            break;
         }
      }
      return result;
   }

   public QueryData buildAndCopy() {
      return build(true);
   }

   public QueryData build() {
      return build(false);
   }

   private QueryData build(boolean clone) {
      QueryData queryData = clone ? getQueryData().clone() : getQueryData();
      Collection<Criteria> criterias = queryData.getAllCriteria();
      if (criterias.isEmpty() || hasOnlyBranchOrTxCriterias(criterias)) {
         addAndCheck(queryData, criteriaFactory.createAllArtifactsCriteria());
      }
      return queryData;
   }

   @SuppressWarnings("unchecked")
   @Override
   public T andIsHeirarchicalRootArtifact() {
      andIds(CoreArtifactTokens.DefaultHierarchyRoot);
      return (T) this;
   }

}
