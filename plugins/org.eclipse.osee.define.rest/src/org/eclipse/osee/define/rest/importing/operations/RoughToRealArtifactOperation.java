/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.operations;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.define.api.importing.IArtifactExtractor;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.define.api.importing.RoughRelation;
import org.eclipse.osee.define.rest.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 * @author David W. Miller
 */
public class RoughToRealArtifactOperation {
   private final OrcsApi orcsApi;
   private final XResultData results;
   private final RoughArtifactCollector rawData;
   private final IArtifactImportResolver artifactResolver;
   private final Map<RoughArtifact, ArtifactReadable> roughToRealArtifacts;
   private final Collection<ArtifactId> createdArtifacts;
   private final ArtifactReadable destinationArtifact;
   private final RelationSorter importArtifactOrder;
   private final boolean deleteUnmatchedArtifacts;
   private Collection<ArtifactReadable> unmatchedArtifacts;
   private final IArtifactExtractor extractor;
   private final TransactionBuilder transaction;
   private boolean addRelation = true;
   private final List<Pair<ArtifactId, String>> artifactWordContent;
   private final HashMap<String, HashMap<ArtifactReadable, ArtifactId>> doorsIdArtIdMap;
   private static final Pattern referencePattern = Pattern.compile("\\[[^\\[]*\\]");

   public RoughToRealArtifactOperation(OrcsApi orcsApi, XResultData results, TransactionBuilder transaction, ArtifactReadable destinationArtifact, RoughArtifactCollector rawData, IArtifactImportResolver artifactResolver, boolean deleteUnmatchedArtifacts, IArtifactExtractor extractor) {
      this.results = results;
      this.orcsApi = orcsApi;
      this.rawData = rawData;
      this.transaction = transaction;
      this.artifactResolver = artifactResolver;
      this.destinationArtifact = destinationArtifact;
      this.importArtifactOrder = USER_DEFINED;
      this.roughToRealArtifacts = new HashMap<>();
      this.createdArtifacts = new LinkedList<>();
      this.deleteUnmatchedArtifacts = deleteUnmatchedArtifacts;
      this.extractor = extractor;
      roughToRealArtifacts.put(rawData.getParentRoughArtifact(), this.destinationArtifact);
      this.artifactWordContent = new LinkedList<>();
      this.doorsIdArtIdMap = new HashMap<>();
   }

   public void doWork() {
      if (deleteUnmatchedArtifacts) {
         this.unmatchedArtifacts = destinationArtifact.getDescendants();
      }
      int count = 0;
      for (RoughArtifact roughArtifact : rawData.getParentRoughArtifact().getChildren()) {
         ArtifactId child = createArtifact(roughArtifact, destinationArtifact);
         createdArtifacts.add(child);
         if (addRelation && child != null && noParent(child)) {
            transaction.relate(destinationArtifact, CoreRelationTypes.DefaultHierarchical_Child, child,
               importArtifactOrder);
         }

         for (RoughRelation roughRelation : rawData.getRoughRelations()) {
            createRelation(roughRelation);
         }
      }

      if (deleteUnmatchedArtifacts) {
         for (ArtifactReadable toDelete : unmatchedArtifacts) {
            transaction.deleteArtifact(toDelete);
         }
      }

   }

   private boolean noParent(ArtifactId artifact) {
      ArtifactReadable art = getArtifactReadable(artifact);
      ArtifactReadable parent = art.getParent();
      if (parent == null) {
         return true;
      }
      if (!parent.isValid()) {
         return true;
      }
      return false;
   }

   private ArtifactId createArtifact(RoughArtifact roughArtifact, ArtifactId realParent) {
      ArtifactReadable realArtifact = roughToRealArtifacts.get(roughArtifact);

      if (realArtifact != null) {
         return realArtifact;
      }
      ArtifactId realArtifactId =
         artifactResolver.resolve(roughArtifact, transaction.getBranch(), realParent, destinationArtifact);

      //creates list of artifacts and their word template content
      String roughWTC = roughArtifact.getRoughAttribute(CoreAttributeTypes.WordTemplateContent.getName());
      if (roughWTC != null) {
         Matcher matcher = referencePattern.matcher(roughWTC);
         if (matcher.find()) {
            artifactWordContent.add(new Pair<>(realArtifactId, roughWTC));
         }
      }

      //creates map of object ids to the artifacts that contain them and each destination folder
      Collection<String> objIds = roughArtifact.getRoughAttributeAsList(CoreAttributeTypes.DoorsId.getName());
      if (objIds != null) {
         for (String objId : objIds) {
            if (!doorsIdArtIdMap.containsKey(objId)) {
               HashMap<ArtifactReadable, ArtifactId> artMap = new HashMap<>();
               artMap.put(destinationArtifact, realArtifactId);
               doorsIdArtIdMap.put(objId, artMap);
            } else {
               HashMap<ArtifactReadable, ArtifactId> artMap = doorsIdArtIdMap.get(objId);
               if (!artMap.containsKey(destinationArtifact)) {
                  doorsIdArtIdMap.get(objId).put(destinationArtifact, realArtifactId);
               }
            }
         }
      }
      if (deleteUnmatchedArtifacts) {
         unmatchedArtifacts.remove(realArtifactId);
      }

      for (RoughArtifact childRoughArtifact : roughArtifact.getDescendants()) {
         ArtifactId childArtifact = createArtifact(childRoughArtifact, realArtifactId);
         if (areValid(realArtifactId, childArtifact)) {
            replaceParent(childArtifact, realArtifactId);
         }
         extractor.artifactCreated(transaction, childArtifact, childRoughArtifact);
      }
      return realArtifactId;
   }

   private void replaceParent(ArtifactId childId, ArtifactId parentId) {
      ArtifactReadable child = getArtifactReadable(childId);
      ArtifactReadable parent = getArtifactReadable(parentId);

      if (parent == null || child == null) {
         return;
      }

      if (hasDifferentParent(child, parent)) {
         transaction.unrelate(child.getParent(), CoreRelationTypes.DefaultHierarchical_Child, child);
      }
      transaction.relate(parent, CoreRelationTypes.DefaultHierarchical_Child, child, importArtifactOrder);
   }

   private ArtifactReadable getArtifactReadable(ArtifactId art) {
      if (art instanceof ArtifactReadable) {
         return (ArtifactReadable) art;
      }
      ArtifactReadable realArt = null;
      try {
         realArt = orcsApi.getQueryFactory().fromBranch(transaction.getBranch()).andId(art).getArtifact();
      } catch (Exception ex) {
         // leave null
      }
      return realArt;
   }

   private boolean hasDifferentParent(ArtifactReadable art, ArtifactReadable parent) {
      ArtifactReadable knownParent = art.getParent();
      return knownParent != null && knownParent.notEqual(parent);
   }

   private boolean isValid(ArtifactId art) {
      return art != null;
   }

   private boolean areValid(ArtifactId... artifacts) {
      boolean returnValue = true;
      for (ArtifactId art : artifacts) {
         returnValue &= isValid(art);
      }
      return returnValue;
   }

   private void createRelation(RoughRelation roughRelation) {
      RelationTypeToken relationType = getRelationType(roughRelation.getRelationTypeName());

      ArtifactReadable aArt = orcsApi.getQueryFactory().fromBranch(transaction.getBranch()).andGuid(
         roughRelation.getAartifactGuid()).getArtifact();
      ArtifactReadable bArt = orcsApi.getQueryFactory().fromBranch(transaction.getBranch()).andGuid(
         roughRelation.getBartifactGuid()).getArtifact();

      if (aArt == null || bArt == null) {
         results.warningf("The relation of type %s could not be created.", roughRelation.getRelationTypeName());

         if (aArt == null) {
            results.warningf("The artifact with guid: %s does not exist.", roughRelation.getAartifactGuid());
         }
         if (bArt == null) {
            results.warningf("The artifact with guid: %s does not exist.", roughRelation.getBartifactGuid());
         }
      } else {
         try {
            transaction.relate(aArt, relationType, bArt, roughRelation.getRationale(), importArtifactOrder);
         } catch (IllegalArgumentException ex) {
            results.error(ex.toString());
         }
      }
   }

   private RelationTypeToken getRelationType(String relationName) {
      for (RelationTypeToken type : orcsApi.getOrcsTypes().getRelationTypes().getAll()) {
         if (type.getName().equals(relationName)) {
            return type;
         }
      }
      return null;
   }

   public boolean isAddRelation() {
      return addRelation;
   }

   public void setAddRelation(boolean addRelation) {
      this.addRelation = addRelation;
   }

   public Collection<ArtifactId> getCreatedArtifacts() {
      return createdArtifacts;
   }

   public List<Pair<ArtifactId, String>> getArtifactWordContent() {
      return artifactWordContent;
   }

   public HashMap<String, HashMap<ArtifactReadable, ArtifactId>> getDoorsIdArtIdMap() {
      return doorsIdArtIdMap;
   }
}
