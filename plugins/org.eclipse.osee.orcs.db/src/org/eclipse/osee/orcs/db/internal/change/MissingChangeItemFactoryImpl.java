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
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   public Collection<ChangeItem> createMissingChanges(HasCancellation cancellation, OrcsSession session, List<ChangeItem> changes, TransactionToken sourceTx, TransactionToken destTx, ApplicabilityQuery applicQuery) throws OseeCoreException {
      if (changes != null && !changes.isEmpty()) {
         Set<Integer> modifiedArtIds = new HashSet<>();
         Multimap<Integer, Integer> modifiedAttrIds = LinkedListMultimap.create();
         Multimap<Integer, Integer> modifiedRels = LinkedListMultimap.create();

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
               default:
                  throw new OseeStateException("Unknonw change type detected [%s]", change);
            }
         }

         Set<Integer> allArtIds = new HashSet<>(modifiedArtIds);
         allArtIds.addAll(modifiedAttrIds.keySet());
         allArtIds.addAll(modifiedRels.keySet());

         Set<Integer> missingArtIds = determineWhichArtifactsNotOnDestination(cancellation, session, allArtIds, destTx);

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

   private Set<Integer> determineWhichArtifactsNotOnDestination(HasCancellation cancellation, OrcsSession session, Set<Integer> artIds, TransactionToken destTx) throws OseeCoreException {
      DataLoader loader = dataLoaderFactory.newDataLoaderFromIds(session, destTx.getBranchId(), artIds);
      final Set<Integer> missingArtIds = new LinkedHashSet<>(artIds);
      loader.includeDeletedArtifacts();
      loader.fromTransaction(destTx);

      loader.load(cancellation, new LoadDataHandlerAdapter() {

         @Override
         public void onData(ArtifactData data) {
            missingArtIds.remove(data.getLocalId());
         }
      });
      return missingArtIds;
   }

   private Collection<ChangeItem> createMissingChangeItems(HasCancellation cancellation, OrcsSession session, TransactionToken sourceTx, TransactionToken destTx, final Set<Integer> modifiedArtIds, final Multimap<Integer, Integer> modifiedAttrIds, final Multimap<Integer, Integer> modifiedRels, final Set<Integer> missingArtIds, final Set<Integer> allArtIds) throws OseeCoreException {
      final Set<ChangeItem> toReturn = new LinkedHashSet<>();
      final Set<RelationData> relations = new LinkedHashSet<>();

      DataLoader loader = dataLoaderFactory.newDataLoaderFromIds(session, sourceTx.getBranchId(), missingArtIds);
      loader.withLoadLevel(LoadLevel.ALL);
      loader.includeDeletedArtifacts();
      loader.fromTransaction(sourceTx);

      loader.load(cancellation, new LoadDataHandlerAdapter() {

         @Override
         public void onData(ArtifactData data) throws OseeCoreException {
            if (!modifiedArtIds.contains(data.getLocalId())) {
               toReturn.add(createArtifactChangeItem(data));
            }
         }

         @Override
         public void onData(RelationData data) {
            int localId = data.getLocalId();
            if (!modifiedRels.get(data.getArtIdA()).contains(localId) && !modifiedRels.get(data.getArtIdB()).contains(
               localId)) {
               relations.add(data);
            }
         }

         @Override
         public void onData(AttributeData data) throws OseeCoreException {
            if (!modifiedAttrIds.get(data.getArtifactId()).contains(data.getLocalId())) {
               toReturn.add(createAttributeChangeItem(data));
            }
         }

      });

      if (!relations.isEmpty()) {
         Multimap<Integer, RelationData> relationChangesToAdd = LinkedListMultimap.create();
         for (RelationData data : relations) {
            if (allArtIds.contains(data.getArtIdA())) {
               if (allArtIds.contains(data.getArtIdB())) {
                  toReturn.add(createRelationChangeItem(data));
               } else {
                  // check if artIdB exists on destination branch, addRelation if it does
                  relationChangesToAdd.put(data.getArtIdB(), data);
               }
            } else if (allArtIds.contains(data.getArtIdB())) {
               // if artIdA exists on destination, addRelation
               relationChangesToAdd.put(data.getArtIdA(), data);
            }
         }
         if (!relationChangesToAdd.isEmpty()) {
            toReturn.addAll(createExistingRelations(cancellation, session, destTx, relationChangesToAdd));
         }
      }
      return toReturn;
   }

   private Set<ChangeItem> createExistingRelations(HasCancellation cancellation, OrcsSession session, TransactionToken destTx, final Multimap<Integer, RelationData> relationChangesToAdd) throws OseeCoreException {
      final Set<ChangeItem> toReturn = new LinkedHashSet<>();

      DataLoader loader =
         dataLoaderFactory.newDataLoaderFromIds(session, destTx.getBranchId(), relationChangesToAdd.keySet());
      loader.fromTransaction(destTx);
      loader.load(cancellation, new LoadDataHandlerAdapter() {

         @Override
         public void onData(ArtifactData data) throws OseeCoreException {
            for (RelationData relData : relationChangesToAdd.get(data.getLocalId())) {
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

   private ChangeItem createArtifactChangeItem(ArtifactData data) throws OseeCoreException {
      ApplicabilityId appId = data.getApplicabilityId();
      ChangeItem artChange = ChangeItemUtil.newArtifactChange(data.getLocalId(), data.getTypeUuid(),
         data.getVersion().getGammaId(), determineModType(data), getApplicabilityToken(appId));
      return artChange;
   }

   private ChangeItem createAttributeChangeItem(AttributeData data) throws OseeCoreException {
      ApplicabilityId appId = data.getApplicabilityId();
      ChangeItem attrChange = ChangeItemUtil.newAttributeChange(data.getLocalId(), data.getTypeUuid(),
         data.getArtifactId(), data.getVersion().getGammaId(), determineModType(data),
         data.getDataProxy().getDisplayableString(), getApplicabilityToken(appId));
      attrChange.getNetChange().copy(attrChange.getCurrentVersion());
      return attrChange;
   }

   private ChangeItem createRelationChangeItem(RelationData data) throws OseeCoreException {
      ApplicabilityId appId = data.getApplicabilityId();
      return ChangeItemUtil.newRelationChange(data.getLocalId(), data.getTypeUuid(), data.getVersion().getGammaId(),
         determineModType(data), data.getArtIdA(), data.getArtIdB(), data.getRationale(), getApplicabilityToken(appId));
   }

}
