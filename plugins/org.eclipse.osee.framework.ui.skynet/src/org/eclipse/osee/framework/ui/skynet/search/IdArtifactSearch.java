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
package org.eclipse.osee.framework.ui.skynet.search;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;

/**
 * @author Roberto E. Escobar
 */
final class IdArtifactSearch extends AbstractLegacyArtifactSearchQuery {
   private final String searchString;
   private final IOseeBranch branchToSearch;
   private final DeletionFlag allowDeleted;

   IdArtifactSearch(String searchString, IOseeBranch branchToSearch, DeletionFlag allowDeleted) {
      super();
      this.searchString = searchString;
      this.branchToSearch = branchToSearch;
      this.allowDeleted = allowDeleted;
   }

   @Override
   public Collection<Artifact> getArtifacts() throws Exception {
      List<Integer> artIds = new LinkedList<>();
      List<String> guids = new LinkedList<>();
      for (String id : Arrays.asList(searchString.split("[\\s,]+"))) {
         if (Strings.isNumeric(id)) {
            artIds.add(Integer.parseInt(id));
         } else {
            guids.add(id);
         }
      }

      List<Artifact> toReturn = new LinkedList<>();

      if (!artIds.isEmpty()) {
         QueryBuilderArtifact query = ArtifactQuery.createQueryBuilder(branchToSearch);
         query.andLocalIds(artIds);
         Iterables.addAll(toReturn, query.getResults());
      }

      if (!guids.isEmpty()) {
         QueryBuilderArtifact query = ArtifactQuery.createQueryBuilder(branchToSearch);
         query.andGuids(guids);
         Iterables.addAll(toReturn, query.getResults());
      }

      return toReturn;
   }

   @Override
   public String getCriteriaLabel() {
      return String.format("%s%s", searchString, allowDeleted == INCLUDE_DELETED ? " - Options:[Include Deleted]" : "");
   }
}
