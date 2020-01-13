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
package org.eclipse.osee.ats.core.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.query.IAtsConfigQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryFilter;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.MultipleItemsExist;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsConfigQueryImpl implements IAtsConfigQuery {

   protected final List<AtsAttributeQuery> andAttr;
   protected List<ArtifactTypeToken> artifactTypes;
   protected ArtifactId artifactId;
   protected final AtsApi atsApi;
   protected Collection<Long> aiIds;
   protected List<ArtifactId> onlyIds = null;
   protected final List<IAtsQueryFilter> queryFilters;

   public AbstractAtsConfigQueryImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
      andAttr = new ArrayList<>();
      aiIds = new ArrayList<>();
      queryFilters = new ArrayList<>();
   }

   @Override
   public Collection<ArtifactId> getIds() {
      onlyIds = new LinkedList<>();
      getArtifacts();
      return onlyIds;
   }

   public abstract void createQueryBuilder();

   @SuppressWarnings("unchecked")
   @Override
   public <T extends IAtsConfigObject> Collection<T> getConfigObjects() {
      Set<T> allResults = new HashSet<>();
      for (ArtifactToken artifact : getArtifacts()) {
         IAtsConfigObject configObj = AtsObjects.getConfigObject(artifact, atsApi);
         if (configObj == null) {
            throw new OseeArgumentException("Non-AtsConfigObject Artifact Returned %s", artifact.toStringWithId());
         }
         allResults.add((T) configObj);
      }
      return allResults;
   }

   @Override
   public <T extends IAtsConfigObject> ResultSet<T> getConfigObjectResultSet() {
      return ResultSets.newResultSet(getConfigObjects());
   }

   public abstract Collection<ArtifactToken> runQuery();

   @SuppressWarnings("unchecked")
   @Override
   public <T extends ArtifactToken> Collection<T> getArtifacts() {
      Set<T> results = new HashSet<>();
      createQueryBuilder();

      if (artifactTypes != null) {
         queryAndIsOfType(artifactTypes);
      }

      if (artifactId != null) {
         queryAndArtifactId(artifactId);
      }

      addAttributeCriteria();

      if (isOnlyIds()) {
         onlyIds.addAll(queryGetIds());
      }
      // filter on original artifact types
      else {
         Collection<ArtifactToken> artifacts = runQuery();
         for (ArtifactToken artifact : artifacts) {
            if (artifactTypes != null || isArtifactTypeMatch(artifact, artifactTypes)) {
               results.add((T) artifact);
            }
         }
      }
      return results;
   }

   @Override
   public <T extends ArtifactToken> ResultSet<T> getArtifactResultSet() {
      return ResultSets.newResultSet(getArtifacts());
   }

   private boolean isArtifactTypeMatch(ArtifactToken artifact, List<ArtifactTypeToken> artTypes) {
      if (artTypes == null || artTypes.isEmpty()) {
         return true;
      }
      for (ArtifactTypeToken artType : artTypes) {
         if (atsApi.getArtifactResolver().isOfType(artifact, artType)) {
            return true;
         }
      }
      return false;
   }

   public abstract void queryAndNotExists(RelationTypeSide relationTypeSide);

   public abstract void queryAndExists(RelationTypeSide relationTypeSide);

   public abstract void queryAndIsOfType(ArtifactTypeToken artifactType);

   public boolean isOnlyIds() {
      return onlyIds != null;
   }

   public abstract List<ArtifactId> queryGetIds();

   @Override
   public IAtsConfigQuery isOfType(ArtifactTypeToken... artifactType) {
      if (this.artifactTypes != null) {
         throw new OseeArgumentException("Can only specify one artifact type");
      }
      this.artifactTypes = new LinkedList<>();
      for (ArtifactTypeToken type : artifactType) {
         this.artifactTypes.add(type);
      }
      return this;
   }

   @Override
   public IAtsConfigQuery andAttr(AttributeTypeId attributeType, Collection<String> values, QueryOption... queryOptions) {
      andAttr.add(new AtsAttributeQuery(attributeType, values, queryOptions));
      return this;
   }

   @Override
   public IAtsConfigQuery isActive() {
      andAttr.add(new AtsAttributeQuery(AtsAttributeTypes.Active, "true"));
      return this;
   }

   @Override
   public IAtsConfigQuery andId(ArtifactId artifactId) {
      this.artifactId = artifactId;
      return this;
   }

   @Override
   public IAtsConfigQuery andAttr(AttributeTypeId attributeType, String value, QueryOption... queryOption) {
      return andAttr(attributeType, Collections.singleton(value), queryOption);
   }

   public abstract void queryAndIsOfType(List<ArtifactTypeToken> artTypes);

   public abstract void queryAnd(AttributeTypeId attrType, String value);

   private void addAttributeCriteria() {
      if (!andAttr.isEmpty()) {
         for (AtsAttributeQuery attrQuery : andAttr) {
            queryAnd(attrQuery.getAttrType(), attrQuery.getValues(), attrQuery.getQueryOption());
         }
      }
   }

   public abstract void queryAnd(AttributeTypeId attrType, Collection<String> values, QueryOption[] queryOption);

   public abstract void queryAnd(AttributeTypeId attrType, String value, QueryOption[] queryOption);

   public abstract void queryAndArtifactId(ArtifactId artifactId);

   public abstract void queryAnd(AttributeTypeId attrType, Collection<String> values);

   public Collection<ArtifactTypeToken> getArtifactTypes() {
      return artifactTypes;
   }

   public void setArtifactType(List<ArtifactTypeToken> artifactTypes) {
      this.artifactTypes = artifactTypes;
   }

   @Override
   public IAtsConfigQuery andProgram(IAtsProgram program) {
      return andProgram(program.getId());
   }

   @Override
   public IAtsConfigQuery andProgram(Long id) {
      return andAttr(AtsAttributeTypes.ProgramId, Collections.singleton(String.valueOf(id)));
   }

   @Override
   public IAtsConfigQuery andWorkType(WorkType workType, WorkType... workTypes) {
      List<String> workTypeStrs = new LinkedList<>();
      workTypeStrs.add(workType.name());
      for (WorkType workType2 : workTypes) {
         workTypeStrs.add(workType2.name());
      }
      return andAttr(AtsAttributeTypes.WorkType, workTypeStrs);
   }

   @Override
   public IAtsConfigQuery andCsci(Collection<String> cscis) {
      return andAttr(AtsAttributeTypes.CSCI, cscis);
   }

   @Override
   public IAtsConfigQuery andName(String name) {
      return andAttr(CoreAttributeTypes.Name, name);
   }

   @Override
   public IAtsConfigQuery andWorkType(Collection<WorkType> workTypes) {
      List<String> workTypeStrs = new LinkedList<>();
      for (WorkType workType2 : workTypes) {
         workTypeStrs.add(workType2.name());
      }
      return andAttr(AtsAttributeTypes.WorkType, workTypeStrs);
   }

   @Override
   public IAtsConfigQuery andTag(String... tags) {
      List<String> values = Arrays.asList(tags);
      return andAttr(CoreAttributeTypes.StaticId, values, QueryOption.EXACT_MATCH_OPTIONS);
   }

   @Override
   public IAtsConfigQuery andActive(boolean active) {
      return andAttr(AtsAttributeTypes.Active, active ? "true" : "false");
   }

   @Override
   public <T extends IAtsConfigObject> Collection<T> getItems(Class<T> clazz) {
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getConfigObjects());
   }

   protected OseeCoreException createManyExistException(int count) {
      return new MultipleItemsExist("Multiple items found - total [%s]", count);
   }

   protected OseeCoreException createDoesNotExistException() {
      return new ItemDoesNotExist("No item found");
   }

   @Override
   public <T extends IAtsConfigObject> T getOneOrNull(Class<T> clazz) {
      Collection<T> items = getItems(clazz);
      if (!items.isEmpty()) {
         return items.iterator().next();
      }
      return null;
   }

   @Override
   public <T extends IAtsConfigObject> T getAtMostOneOrNull(Class<T> clazz) {
      T result = null;
      Collection<T> items = getItems(clazz);
      if (items != null) {
         int size = items.size();
         if (size > 1) {
            throw createManyExistException(size);
         } else if (size == 1) {
            result = items.iterator().next();
         }
      }
      return result;
   }

   @Override
   public <T extends IAtsConfigObject> T getExactlyOne(Class<T> clazz) {

      T result = getAtMostOneOrNull(clazz);
      if (result == null) {
         throw createDoesNotExistException();
      }
      return result;
   }

   @Override
   public <T extends IAtsConfigObject> T getOneOrDefault(Class<T> clazz, T defaultValue) {
      Collection<T> items = getItems(clazz);
      if (items != null) {
         int size = items.size();
         if (size > 0) {
            defaultValue = items.iterator().next();
         }
      }
      return defaultValue;
   }

}
