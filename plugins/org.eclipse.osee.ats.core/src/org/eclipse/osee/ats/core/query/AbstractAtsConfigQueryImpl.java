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
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.query.IAtsConfigQuery;
import org.eclipse.osee.ats.api.query.IAtsQueryFilter;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsConfigQueryImpl implements IAtsConfigQuery {

   protected final List<AtsAttributeQuery> andAttr;
   protected List<IArtifactType> artifactTypes;
   protected ArtifactId artifactId;
   protected final IAtsServices services;
   protected Collection<Long> aiUuids;
   protected List<ArtifactId> onlyIds = null;
   protected final List<IAtsQueryFilter> queryFilters;

   public AbstractAtsConfigQueryImpl(IAtsServices services) {
      this.services = services;
      andAttr = new ArrayList<>();
      aiUuids = new ArrayList<>();
      queryFilters = new ArrayList<>();
   }

   @Override
   public Collection<ArtifactId> getItemIds() throws OseeCoreException {
      onlyIds = new LinkedList<>();
      getItems();
      return onlyIds;
   }

   public abstract void createQueryBuilder();

   @Override
   public <T extends IAtsConfigObject> Collection<T> getItems() {
      createQueryBuilder();

      if (artifactTypes != null) {
         queryAndIsOfType(artifactTypes);
      }

      if (artifactId != null) {
         queryAndArtifactId(artifactId);
      }

      addAttributeCriteria();

      Set<T> allResults = new HashSet<>();
      collectResults(allResults, artifactTypes);

      return allResults;
   }

   public abstract Collection<ArtifactId> runQuery();

   @SuppressWarnings("unchecked")
   private <T> Collection<T> collectResults(Set<T> allResults, List<IArtifactType> artifactTypes) {
      Set<T> results = new HashSet<>();
      if (isOnlyIds()) {
         onlyIds.addAll(queryGetIds());
      }
      // filter on original artifact types
      else {
         Collection<ArtifactId> artifacts = runQuery();
         for (ArtifactId artifact : artifacts) {
            if (artifactTypes != null || isArtifactTypeMatch(artifact, artifactTypes)) {
               results.add((T) createFromFactory(artifact));
            }
         }
      }
      addtoResultsWithNullCheck(allResults, results);

      return results;
   }

   @SuppressWarnings("unchecked")
   private <T> T createFromFactory(ArtifactId artifact) {
      return (T) services.getConfigItemFactory().getConfigObject(artifact);
   }

   private <T> void addtoResultsWithNullCheck(Set<T> allResults, Collection<? extends T> configObjects) {
      if (configObjects.contains(null)) {
         OseeLog.log(AbstractAtsConfigQueryImpl.class, Level.SEVERE, "Null found in results");
      } else {
         allResults.addAll(configObjects);
      }
   }

   private boolean isArtifactTypeMatch(ArtifactId artifact, List<IArtifactType> artTypes) {
      if (artTypes == null || artTypes.isEmpty()) {
         return true;
      }
      for (IArtifactType artType : artTypes) {
         if (services.getArtifactResolver().isOfType(artifact, artType)) {
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
   public IAtsConfigQuery andAttr(AttributeTypeId attributeType, Collection<String> values, QueryOption... queryOptions) throws OseeCoreException {
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

   @Override
   public <T extends IAtsConfigObject> ResultSet<T> getResults() {
      return ResultSets.newResultSet(getItems());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends ArtifactId> ResultSet<T> getResultArtifacts() {
      List<T> items = new ArrayList<>();
      ResultSet<IAtsConfigObject> results = getResults();
      for (IAtsConfigObject configObject : results) {
         if (configObject == null) {
            OseeLog.log(AbstractAtsConfigQueryImpl.class, Level.SEVERE, "Null found in results");
         } else {
            items.add((T) services.getArtifact(configObject));
         }
      }
      // filter on original artifact types
      List<T> artifacts = new LinkedList<>();
      for (ArtifactId artifact : items) {
         boolean artifactTypeMatch = isArtifactTypeMatch(artifact, artifactTypes);
         if (artifactTypeMatch) {
            artifacts.add((T) artifact);
         }
      }
      return ResultSets.newResultSet(artifacts);
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
   public IAtsConfigQuery andProgram(Long uuid) {
      return andAttr(AtsAttributeTypes.ProgramUuid, Collections.singleton(String.valueOf(uuid)));
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
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getItems());
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
