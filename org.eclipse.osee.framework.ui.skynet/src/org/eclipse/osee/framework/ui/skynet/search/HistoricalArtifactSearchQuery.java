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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Ryan D. Brooks
 */
public class HistoricalArtifactSearchQuery extends AbstractLegacyArtifactSearchQuery {
   private String attributePattern;
   private final Branch branch;

   public HistoricalArtifactSearchQuery(String attributePattern, Branch branch) {
      this.attributePattern = attributePattern;
      this.branch = branch;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getArtifacts()
    */
   @Override
   public Collection<Artifact> getArtifacts() throws OseeCoreException {
      return ArtifactQuery.getArtifactsFromHistoricalAttributeValue(attributePattern, branch);
   }

   public String getCriteriaLabel() {
      return attributePattern;
   }
}