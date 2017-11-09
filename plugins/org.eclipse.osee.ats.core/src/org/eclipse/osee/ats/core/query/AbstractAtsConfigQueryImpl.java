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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsConfigQueryImpl implements IAtsConfigQuery {

   protected final List<AtsAttributeQuery> andAttr;
   protected List<IArtifactType> artifactTypes;
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
         IAtsConfigObject configObj = atsApi.getConfigItemFactory().getConfigObject(artifact);
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

   public abstract Collection<ArtifactId> runQuery();

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
         Collection<ArtifactId> artifacts = runQuery();
         for (ArtifactId artifact : artifacts) {
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

   private boolean isArtifactTypeMatch(ArtifactId artifact, List<IArtifactType> artTypes) {
      if (artTypes == null || artTypes.isEmpty()) {
         return true;
      }
      for (IArtifactType artType : artTypes) {
         if (atsApi.getArtifactResolver().isOfType(artifact, artType)) {
            return true;
         }
      }
      return false;
   }

   public abstract void queryAndNotExists(RelationTypeSide relationTypeSide);

   public abstract void queryAndExists(RelationTypeSide relationTypeSide);

   public abstract void queryAndIsOfType(IArtifactType artifactType);

   public boolean isOnlyIds() {
      return onlyIds != null;
   }

   public abstract List<? extends ArtifactId> queryGetIds();

   @Override
   public IAtsConfigQuery isOfType(IArtifactType... artifactType) {
      if (this.artifactTypes != null) {
         throw new OseeArgumentException("Can only specify one artifact type");
      }
      this.artifactTypes = new LinkedList<>();
      for (IArtifactType type : artifactType) {
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
   public IAtsConfigQuery andId(ArtifactId artifactId) {
      this.artifactId = artifactId;
      return this;
   }

   @Override
   public IAtsConfigQuery andAttr(AttributeTypeId attributeType, String value, QueryOption... queryOption) {
      return andAttr(attributeType, Collections.singleton(value), queryOption);
   }

   public abstract void queryAndIsOfType(List<IArtifactType> artTypes);

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

   public Collection<IArtifactType> getArtifactTypes() {
      return artifactTypes;
   }

   public void setArtifactType(List<IArtifactType> artifactTypes) {
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

   @Override
   public <T extends IAtsConfigObject> T getOneOrNull(Class<T> clazz) {
      Collection<T> items = getItems(clazz);
      if (!items.isEmpty()) {
         return items.iterator().next();
      }
      return null;
   }

}
