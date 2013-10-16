/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.mocks;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.display.api.search.AsyncSearchListener;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.ResultSetList;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author John R. Misinco
 */
public class MockArtifactProvider implements ArtifactProvider {

   private final Map<String, ArtifactReadable> artifacts = new HashMap<String, ArtifactReadable>();
   private List<Match<ArtifactReadable, AttributeReadable<?>>> resultList;

   public void addArtifact(ArtifactReadable artifact) {
      artifacts.put(artifact.getGuid(), artifact);
   }

   public void setResultList(List<Match<ArtifactReadable, AttributeReadable<?>>> resultList) {
      this.resultList = resultList;
   }

   @Override
   public ArtifactReadable getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) {
      return artifacts.get(token.getGuid());
   }

   @Override
   public ArtifactReadable getArtifactByGuid(IOseeBranch branch, String guid) {
      return artifacts.get(guid);
   }

   //   @Override
   //   public List<Match<ReadableArtifact, ReadableAttribute<?>>> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) {
   //      return resultList;
   //   }

   @Override
   public ResultSet<ArtifactReadable> getRelatedArtifacts(ArtifactReadable art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
      if (art instanceof MockArtifact) {
         MockArtifact mArt = (MockArtifact) art;
         ResultSet<ArtifactReadable> result = mArt.getRelatedArtifacts(relationTypeSide);
         return result;
      } else {
         return new ResultSetList<ArtifactReadable>();
      }
   }

   @Override
   public ArtifactReadable getRelatedArtifact(ArtifactReadable art, IRelationTypeSide relationTypeSide) {
      if (art instanceof MockArtifact) {
         MockArtifact mArt = (MockArtifact) art;
         return mArt.getRelatedArtifacts(relationTypeSide).iterator().next();
      } else {
         return null;
      }
   }

   @Override
   public ArtifactReadable getParent(ArtifactReadable art) {
      if (art instanceof MockArtifact) {
         MockArtifact mArt = (MockArtifact) art;
         return null;
      } else {
         return null;
      }
   }

   @Override
   public Collection<? extends IRelationType> getValidRelationTypes(ArtifactReadable art) {
      if (art instanceof MockArtifact) {
         return ((MockArtifact) art).getValidRelationTypes();
      } else {
         return Collections.emptyList();
      }
   }

   @Override
   public void getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase, AsyncSearchListener callback) {
      callback.onSearchComplete(resultList);
   }

   @Override
   public void cancelSearch() {
      //do nothing
   }

   @Override
   public String getSideAName(IRelationType type) {
      return String.format("%s:%s", type, RelationSide.SIDE_A);
   }

   @Override
   public String getSideBName(IRelationType type) {
      return String.format("%s:%s", type, RelationSide.SIDE_B);
   }
}