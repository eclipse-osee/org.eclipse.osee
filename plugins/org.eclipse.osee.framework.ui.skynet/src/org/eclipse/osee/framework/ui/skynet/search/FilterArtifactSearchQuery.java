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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModel;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModelList;

/**
 * @author Ryan D. Brooks
 */
public class FilterArtifactSearchQuery extends AbstractLegacyArtifactSearchQuery {
   private final FilterModelList filterList;
   private final BranchId branch;
   private String criteriaLabel = "";

   public FilterArtifactSearchQuery(FilterModelList filterList, BranchId branch) {
      this.filterList = filterList;
      this.branch = branch;
   }

   @Override
   public Collection<Artifact> getArtifacts()  {
      boolean firstTime = true;

      QueryBuilderArtifact queryBuilderArtifact = ArtifactQuery.createQueryBuilder(branch);

      for (FilterModel model : filterList.getFilters()) {
         model.getSearchPrimitive().addToQuery(queryBuilderArtifact);

         if (!firstTime) {
            criteriaLabel += " and ";
         }
         criteriaLabel += model;
         firstTime = false;
      }

      List<Artifact> toReturn = new LinkedList<>();
      for (Artifact art : queryBuilderArtifact.getResults()) {
         toReturn.add(art);
      }

      return toReturn;
   }

   @Override
   public String getCriteriaLabel() {
      return criteriaLabel;
   }
}