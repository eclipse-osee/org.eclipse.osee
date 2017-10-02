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
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaTxArtifactIds extends Criteria implements TxCriteria {

   private final Collection<ArtifactId> artifactIds;
   private final ArtifactId artifactId;

   public CriteriaTxArtifactIds(ArtifactId artifactId) {
      this.artifactId = artifactId;
      this.artifactIds = null;
   }

   public CriteriaTxArtifactIds(Collection<ArtifactId> artifactIds) {
      this.artifactIds = artifactIds;
      this.artifactId = null;
   }

   @Override
   public void checkValid(Options options)  {
      Conditions.checkNotNullOrEmpty(artifactIds, "artifact ids");
   }

   public Collection<ArtifactId> getIds() {
      return artifactIds;
   }

   public ArtifactId getId() {
      return artifactId;
   }

   @Override
   public String toString() {
      return "CriteriaTxArtifactIds [" + artifactIds + "]";
   }

   public boolean hasMultiple() {
      return artifactId == null;
   }
}