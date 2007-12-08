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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;

/**
 * @author Ryan D. Brooks
 */
public class HistoricalArtifactSearchQuery extends AbstractArtifactSearchQuery {
   private String attributePattern;

   public HistoricalArtifactSearchQuery(String attributePattern) {
      this.attributePattern = attributePattern;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getArtifacts()
    */
   @Override
   public Collection<Artifact> getArtifacts() throws SQLException {
      return ArtifactPersistenceManager.getInstance().getHistoricalArtifactsFromAttribute(attributePattern);
   }

   public String getCriteriaLabel() {
      return attributePattern;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#showBranch()
    */
   @Override
   public boolean showBranch() {
      return true;
   }
}