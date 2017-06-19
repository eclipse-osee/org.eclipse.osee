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

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;

/**
 * @author Roberto E. Escobar
 */
final class IdArtifactSearch extends AbstractLegacyArtifactSearchQuery {
   private final String searchString;
   private final BranchId branchToSearch;
   private final boolean allowDeleted;

   IdArtifactSearch(String searchString, BranchId branchToSearch, boolean allowDeleted) {
      super();
      this.searchString = searchString;
      this.branchToSearch = branchToSearch;
      this.allowDeleted = allowDeleted;
   }

   @Override
   public Collection<Artifact> getArtifacts() throws Exception {
      List<ArtifactId> artIds = new LinkedList<>();
      List<String> guids = new LinkedList<>();
      for (String id : Arrays.asList(searchString.split("[\\s,]+"))) {
         if (Strings.isNumeric(id)) {
            artIds.add(ArtifactId.valueOf(id));
         } else {
            guids.add(id);
         }
      }

      List<Artifact> toReturn = new LinkedList<>();

      QueryBuilderArtifact query = ArtifactQuery.createQueryBuilder(branchToSearch);
      if (!artIds.isEmpty()) {
         query.andLocalIds(artIds);
      }
      if (!guids.isEmpty()) {
         query.andGuids(guids);
      }
      query.includeDeleted(allowDeleted);
      Iterables.addAll(toReturn, query.getResults());

      return toReturn;
   }

   @Override
   public String getCriteriaLabel() {
      return String.format("%s%s", searchString, allowDeleted ? " - Options:[Include Deleted]" : "");
   }
}
