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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.change.ArtifactChangeItem;
import org.eclipse.osee.framework.core.model.change.AttributeChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.RelationChangeItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author John Misinco
 */
public class MissingChangeItemFactoryImpl implements MissingChangeItemFactory {

   private final DataLoaderFactory dataLoaderFactory;
   private final IdentityLocator identityService;

   public MissingChangeItemFactoryImpl(IdentityLocator identityService, DataLoaderFactory dataModuleFactory) {
      super();
      this.identityService = identityService;
      this.dataLoaderFactory = dataModuleFactory;
   }

   @Override
   public Collection<ChangeItem> createMissingChanges(HasCancellation cancellation, OrcsSession session, List<ChangeItem> changes, TransactionRecord sourceTx, TransactionRecord destTx) throws OseeCoreException {
      if (changes != null && !changes.isEmpty()) {
         Set<Integer> modifiedArtIds = new HashSet<Integer>();
         Multimap<Integer, Integer> modifiedAttrIds = LinkedListMultimap.create();
         Multimap<Integer, Integer> modifiedRels = LinkedListMultimap.create();

         for (ChangeItem change : changes) {
            if (change instanceof AttributeChangeItem) {
               modifiedAttrIds.put(change.getArtId(), change.getItemId());
            } else if (change instanceof ArtifactChangeItem) {
               if (!change.isSynthetic()) {
                  modifiedArtIds.add(change.getArtId());
               }
            } else if (change instanceof RelationChangeItem) {
               modifiedRels.put(change.getArtId(), change.getItemId());
               modifiedRels.put(((RelationChangeItem) change).getBArtId(), change.getItemId());
            }
         }

         Set<Integer> allArtIds = new HashSet<Integer>(modifiedArtIds);
         allArtIds.addAll(modifiedAttrIds.keySet());
         allArtIds.addAll(modifiedRels.keySet());

         Set<Integer> missingArtIds = determineWhichArtifactsNotOnDestination(cancellation, session, allArtIds, destTx);

         if (!missingArtIds.isEmpty()) {
            return createMissingChangeItems(cancellation, session, sourceTx, destTx, modifiedArtIds, modifiedAttrIds,
               modifiedRels, missingArtIds, allArtIds);
         }
      }
      return Collections.emptyList();
   }

   private Set<Integer> determineWhichArtifactsNotOnDestination(HasCancellation cancellation, OrcsSession session, Set<Integer> artIds, TransactionRecord destTx) throws OseeCoreException {
      DataLoader loader = dataLoaderFactory.newDataLoaderFromIds(session, destTx.getBranch(), artIds);
      final Set<Integer> missingArtIds = new LinkedHashSet<Integer>(artIds);
      loader.includeDeletedArtifacts();
      loader.fromTransaction(destTx.getId());

      loader.load(cancellation, new LoadDataHandlerAdapter() {

         @Override
         public void onData(ArtifactData data) {
            missingArtIds.remove(data.getLocalId());
         }
      });
      return missingArtIds;
   }

   private Collection<ChangeItem> createMissingChangeItems(HasCancellation cancellation, OrcsSession session, TransactionRecord sourceTx, TransactionRecord destTx, final Set<Integer> modifiedArtIds, final Multimap<Integer, Integer> modifiedAttrIds, final Multimap<Integer, Integer> modifiedRels, final Set<Integer> missingArtIds, final Set<Integer> allArtIds) throws OseeCoreException {
      final Set<ChangeItem> toReturn = new LinkedHashSet<ChangeItem>();
      final Set<RelationData> relations = new LinkedHashSet<RelationData>();

      DataLoader loader = dataLoaderFactory.newDataLoaderFromIds(session, sourceTx.getBranch(), missingArtIds);
      loader.withLoadLevel(LoadLevel.ALL);
      loader.includeDeletedArtifacts();
      loader.fromTransaction(sourceTx.getId());

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

   private Set<RelationChangeItem> createExistingRelations(HasCancellation cancellation, OrcsSession session, TransactionRecord destTx, final Multimap<Integer, RelationData> relationChangesToAdd) throws OseeCoreException {
      final Set<RelationChangeItem> toReturn = new LinkedHashSet<RelationChangeItem>();

      DataLoader loader =
         dataLoaderFactory.newDataLoaderFromIds(session, destTx.getBranch(), relationChangesToAdd.keySet());
      loader.fromTransaction(destTx.getId());
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

   private ArtifactChangeItem createArtifactChangeItem(ArtifactData data) throws OseeCoreException {
      ArtifactChangeItem artChange =
         new ArtifactChangeItem(data.getLocalId(), identityService.getLocalId(data.getTypeUuid()),
            data.getVersion().getGammaId(), determineModType(data));
      return artChange;
   }

   private AttributeChangeItem createAttributeChangeItem(AttributeData data) throws OseeCoreException {
      AttributeChangeItem attrChange =
         new AttributeChangeItem(data.getLocalId(), identityService.getLocalId(data.getTypeUuid()),
            data.getArtifactId(), data.getVersion().getGammaId(), determineModType(data),
            data.getDataProxy().getDisplayableString());
      attrChange.getNetChange().copy(attrChange.getCurrentVersion());
      return attrChange;
   }

   private RelationChangeItem createRelationChangeItem(RelationData data) throws OseeCoreException {
      return new RelationChangeItem(data.getLocalId(), identityService.getLocalId(data.getTypeUuid()),
         data.getVersion().getGammaId(), determineModType(data), data.getArtIdA(), data.getArtIdB(),
         data.getRationale());
   }

}
