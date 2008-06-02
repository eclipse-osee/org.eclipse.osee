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

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.ISearchConfirmer;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModel;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModelList;

/**
 * @author Ryan D. Brooks
 */
public class FilterArtifactSearchQuery extends AbstractArtifactSearchQuery {
   private FilterModelList filterList;
   private Branch branch;
   private String criteriaLabel = "";

   /**
    * @param filterList
    * @param branch
    */
   public FilterArtifactSearchQuery(FilterModelList filterList, Branch branch) {
      this.filterList = filterList;
      this.branch = branch;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getArtifacts()
    */
   @Override
   public Collection<Artifact> getArtifacts() throws SQLException {
      boolean firstTime = true;
      List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();

      for (FilterModel model : filterList.getFilters()) {
         criteria.add(model.getSearchPrimitive());

         if (!firstTime) {
            if (filterList.isAllSelected())
               criteriaLabel += " and ";
            else
               criteriaLabel += " or ";
         }

         criteriaLabel += model;
         firstTime = false;
      }

      ISearchConfirmer confirmer = new MaxMatchCountConfirmer();
      return ArtifactPersistenceManager.getInstance().getArtifacts(criteria, filterList.isAllSelected(), branch,
            confirmer);
   }

   public String getCriteriaLabel() {
      return criteriaLabel;
   }
}