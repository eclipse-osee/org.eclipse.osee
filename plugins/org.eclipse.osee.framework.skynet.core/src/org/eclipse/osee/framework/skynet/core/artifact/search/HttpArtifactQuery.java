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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.SearchRequest;
import org.eclipse.osee.framework.core.message.SearchResponse;
import org.eclipse.osee.framework.core.message.SearchResponse.ArtifactMatchMetaData;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;
import org.eclipse.osee.framework.skynet.core.artifact.LoadLevel;
import org.eclipse.osee.framework.skynet.core.artifact.LoadType;

/**
 * @author Roberto E. Escobar
 */
final class HttpArtifactQuery {
   private final SearchRequest searchRequest;

   protected HttpArtifactQuery(SearchRequest searchRequest) {
      this.searchRequest = searchRequest;
   }

   private SearchResponse executeSearch() throws OseeCoreException {
      SearchResponse response =
         HttpClientMessage.send(OseeServerContext.SEARCH_CONTEXT, new HashMap<String, String>(),
            CoreTranslatorId.SEARCH_REQUEST, searchRequest, CoreTranslatorId.SEARCH_RESPONSE);
      String errorMessage = response.getErrorMessage();
      Conditions.checkExpressionFailOnTrue(Strings.isValid(errorMessage), errorMessage);
      return response;
   }

   public List<Artifact> getArtifacts(LoadLevel loadLevel, LoadType reload) throws OseeCoreException {
      SearchResponse response = executeSearch();
      return loadArtifacts(response, loadLevel, reload);
   }

   public List<ArtifactMatch> getArtifactsWithMatches(LoadLevel loadLevel, LoadType reload) throws OseeCoreException {
      List<ArtifactMatch> toReturn = new ArrayList<ArtifactMatch>();
      SearchResponse response = executeSearch();
      List<Artifact> artifacts = loadArtifacts(response, loadLevel, reload);
      for (Artifact artifact : artifacts) {
         ArtifactMatchMetaData match = response.getArtifactMatch(artifact.getBranch().getId(), artifact.getArtId());
         toReturn.add(new ArtifactMatch(artifact, match));
      }
      return toReturn;
   }

   private List<Artifact> loadArtifacts(SearchResponse response, LoadLevel loadLevel, LoadType reload) throws OseeCoreException {
      List<Artifact> toReturn = new ArrayList<Artifact>();
      for (Integer branchId : response.getBranchIds()) {
         IOseeBranch branch = BranchManager.getBranch(branchId);
         Collection<Integer> artsIds = response.getArtifactIds(branchId);
         DeletionFlag deletionFlag = searchRequest.getOptions().getDeletionFlag();
         toReturn.addAll(ArtifactLoader.loadArtifacts(artsIds, branch, loadLevel, reload, deletionFlag));
      }
      return toReturn;
   }
}
