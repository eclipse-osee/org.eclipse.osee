/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.change;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;

/**
 * @author John Misinco
 */
public class MissingChangeItemFactoryImpl implements MissingChangeItemFactory {

   private final DataLoaderFactory dataLoaderFactory;
   private HashMap<Long, ApplicabilityToken> applicTokensForMissingArts = null;

   public MissingChangeItemFactoryImpl(DataLoaderFactory dataModuleFactory) {
      super();
      this.dataLoaderFactory = dataModuleFactory;
   }

   @Override
   public Collection<ChangeItem> createMissingChanges(HasCancellation cancellation, OrcsSession session, List<ChangeItem> changes, TransactionToken sourceTx, TransactionToken destTx, ApplicabilityQuery applicQuery) {
      if (changes != null && !changes.isEmpty()) {
         Set<ArtifactId> modifiedArtIds = new HashSet<>();
         Multimap<ArtifactId, Id> modifiedAttrIds = LinkedListMultimap.create();
         Multimap<ArtifactId, Id> modifiedRels = LinkedListMultimap.create();
         Multimap<Long, Long> modifiedTuples = LinkedListMultimap.create();

         for (ChangeItem change : changes) {
            switch (change.getChangeType()) {
               case ARTIFACT_CHANGE:
                  if (!change.isSynthetic()) {
                     modifiedArtIds.add(change.getArtId());
                  }
                  break;
               case ATTRIBUTE_CHANGE:
                  modifiedAttrIds.put(change.getArtId(), change.getItemId());
                  break;
               case RELATION_CHANGE:
                  modifiedRels.put(change.getArtId(), change.getItemId());
                  modifiedRels.put(change.getArtIdB(), change.getItemId());
                  break;
               case TUPLE_CHANGE:
                  modifiedTuples.put(0L, 1L);
                  break;
               default:
                  throw new OseeStateException("Unknonw change type detected [%s]", change);
            }
         }

         Set<ArtifactId> allArtIds = new HashSet<>(modifiedArtIds);
         allArtIds.addAll(modifiedAttrIds.keySet());
         allArtIds.addAll(modifiedRels.keySet());

         Set<ArtifactId> missingArtIds =
            determineWhichArtifactsNotOnDestination(cancellation, session, allArtIds, destTx);

         if (!missingArtIds.isEmpty()) {
            applicTokensForMissingArts = applicQuery.getApplicabilityTokens(sourceTx.getBranch(), destTx.getBranch());
            return createMissingChangeItems(cancellation, session, sourceTx, destTx, modifiedArtIds, modifiedAttrIds,
               modifiedRels, missingArtIds, allArtIds);
         }
      }
      return Collections.emptyList();
   }

   private ApplicabilityToken getApplicabilityToken(ApplicabilityId appId) {
      Conditions.checkNotNull(appId, "ApplicabilityId");
      ApplicabilityToken toReturn = ApplicabilityToken.BASE;
      if (applicTokensForMissingArts != null) {
         toReturn = applicTokensForMissingArts.get(appId.getId());
         if (toReturn == null) {
            toReturn = ApplicabilityToken.BASE;
         }
      }

      return toReturn;
   }

   private Set<ArtifactId> determineWhichArtifactsNotOnDestination(HasCancellation cancellation, OrcsSession session, Set<ArtifactId> artIds, TransactionToken destTx) {
      DataLoader loader = dataLoaderFactory.newDataLoader(session, destTx.getBranch(), artIds);
      final Set<ArtifactId> missingArtIds = new LinkedHashSet<>(artIds);
      loader.includeDeletedArtifacts();
      loader.fromTransaction(destTx);
      loader.fromBranchView(destTx.getBranch().getViewId());

      loader.load(cancellation, new LoadDataHandlerAdapter() {

         @Override
         public void onData(ArtifactData data) {
            missingArtIds.remove(data);
         }
      });
      return missingArtIds;
   }

   private Collection<ChangeItem> createMissingChangeItems(HasCancellation cancellation, OrcsSession session, TransactionToken sourceTx, TransactionToken destTx, final Set<ArtifactId> modifiedArtIds, final Multimap<ArtifactId, Id> modifiedAttrIds, final Multimap<ArtifactId, Id> modifiedRels, final Set<ArtifactId> missingArtIds, final Set<ArtifactId> allArtIds) {
      final Set<ChangeItem> toReturn = new LinkedHashSet<>();
      final Set<RelationData> relations = new LinkedHashSet<>();

      DataLoader loader = dataLoaderFactory.newDataLoader(session, sourceTx.getBranch(), missingArtIds);
      loader.withLoadLevel(LoadLevel.ALL);
      loader.includeDeletedArtifacts();
      loader.fromTransaction(sourceTx);
      loader.fromBranchView(sourceTx.getBranch().getViewId());

      loader.load(cancellation, new LoadDataHandlerAdapter() {

         @Override
         public void onData(ArtifactData data) {
            if (!modifiedArtIds.contains(data)) {
               toReturn.add(createArtifactChangeItem(data));
            }
         }

         @Override
         public void onData(RelationData data) {
            if (!modifiedRels.get(data.getArtifactIdA()).contains(
               data) && !modifiedRels.get(data.getArtifactIdB()).contains(data)) {
               relations.add(data);
            }
         }

         @Override
         public <T> void onData(AttributeData<T> data) {
            if (!modifiedAttrIds.get(ArtifactId.valueOf(data.getArtifactId())).contains(data)) {
               toReturn.add(createAttributeChangeItem(data));
            }
         }

      });

      if (!relations.isEmpty()) {
         Multimap<ArtifactId, RelationData> relationChangesToAdd = LinkedListMultimap.create();
         for (RelationData data : relations) {
            if (allArtIds.contains(data.getArtifactIdA())) {
               if (allArtIds.contains(data.getArtifactIdB())) {
                  toReturn.add(createRelationChangeItem(data));
               } else {
                  // check if artIdB exists on destination branch, addRelation if it does
                  relationChangesToAdd.put(data.getArtifactIdB(), data);
               }
            } else if (allArtIds.contains(data.getArtifactIdB())) {
               // if artIdA exists on destination, addRelation
               relationChangesToAdd.put(data.getArtifactIdA(), data);
            }
         }
         if (!relationChangesToAdd.isEmpty()) {
            toReturn.addAll(createExistingRelations(cancellation, session, destTx, relationChangesToAdd));
         }
      }
      return toReturn;
   }

   private Set<ChangeItem> createExistingRelations(HasCancellation cancellation, OrcsSession session, TransactionToken destTx, final Multimap<ArtifactId, RelationData> relationChangesToAdd) {
      final Set<ChangeItem> toReturn = new LinkedHashSet<>();

      DataLoader loader = dataLoaderFactory.newDataLoader(session, destTx.getBranch(), relationChangesToAdd.keySet());
      loader.fromTransaction(destTx);
      loader.fromBranchView(destTx.getBranch().getViewId());
      loader.load(cancellation, new LoadDataHandlerAdapter() {

         @Override
         public void onData(ArtifactData data) {
            for (RelationData relData : relationChangesToAdd.get(data)) {
               toReturn.add(createRelationChangeItem(relData));
            }
         }
      });
      return toReturn;
   }

   private ModificationType determineModType(OrcsData data) {
      if (data.getModType().matches(ModificationType.DELETED, ModificationType.ARTIFACT_DELETED)) {
         return data.getModType();
      } else {
         return ModificationType.INTRODUCED;
      }
   }

   private ChangeItem createArtifactChangeItem(ArtifactData data) {
      ApplicabilityId appId = data.getApplicabilityId();
      ChangeItem artChange = ChangeItemUtil.newArtifactChange(ArtifactId.valueOf(data.getLocalId()),
         ArtifactTypeId.valueOf(data.getTypeUuid()), data.getVersion().getGammaId(), determineModType(data),
         getApplicabilityToken(appId));
      return artChange;
   }

   private ChangeItem createAttributeChangeItem(AttributeData data) {
      ApplicabilityId appId = data.getApplicabilityId();
      ChangeItem attrChange = ChangeItemUtil.newAttributeChange(data, AttributeTypeId.valueOf(data.getTypeUuid()),
         ArtifactId.valueOf(data.getArtifactId()), data.getVersion().getGammaId(), determineModType(data),
         data.getDataProxy().getDisplayableString(), getApplicabilityToken(appId));
      attrChange.getNetChange().copy(attrChange.getCurrentVersion());
      return attrChange;
   }

   private ChangeItem createRelationChangeItem(RelationData data) {
      ApplicabilityId appId = data.getApplicabilityId();
      return ChangeItemUtil.newRelationChange(RelationId.valueOf(Long.valueOf(data.getLocalId())),
         RelationTypeId.valueOf(data.getTypeUuid()), data.getVersion().getGammaId(), determineModType(data),
         data.getArtifactIdA(), data.getArtifactIdB(), data.getRationale(), getApplicabilityToken(appId));
   }

}
