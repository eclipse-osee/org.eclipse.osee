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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 */
final class RemoteArtifactSearch extends AbstractArtifactSearchQuery {
   private String queryString;
   private boolean nameOnly;
   private boolean includeDeleted;
   private Branch branch;

   RemoteArtifactSearch(String queryString, Branch branch, boolean nameOnly, boolean includeDeleted) {
      this.branch = branch;
      this.includeDeleted = includeDeleted;
      this.nameOnly = nameOnly;
      this.queryString = queryString;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getArtifacts()
    */
   @Override
   public Collection<Artifact> getArtifacts() throws Exception {
      return ArtifactQuery.getArtifactsFromAttributeWithKeywords(queryString, nameOnly, includeDeleted, branch);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getCriteriaLabel()
    */
   @Override
   public String getCriteriaLabel() {
      return String.format("(%s) - Options:[%s%s]", queryString, nameOnly ? "Name Only " : "",
            includeDeleted ? "Include Deleted " : "");
   }
}
