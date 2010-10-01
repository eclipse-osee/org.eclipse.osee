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
package org.eclipse.osee.ats.artifact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

public abstract class AbstractAtsArtifact extends Artifact implements IATSArtifact {

   public AbstractAtsArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public String getHyperlinkHtml() {
      return Overview.getOpenHyperlinkHtml(this);
   }

   /**
    * Recursively retrieve artifacts and all its ATS related artifacts such as tasks, notes, subscriptions, etc... for
    * deletion
    */
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      deleteArts.add(this);
      for (Artifact relative : getBSideArtifacts()) {
         allRelated.put(relative, this);
      }
   }

   private List<Artifact> getBSideArtifacts() throws OseeCoreException {
      List<Artifact> sideBArtifacts = new ArrayList<Artifact>();
      List<RelationLink> relatives = getRelationsAll(DeletionFlag.EXCLUDE_DELETED);
      for (RelationLink link : relatives) {
         Artifact sideB = link.getArtifactB();
         if (!sideB.equals(this)) {
            sideBArtifacts.add(sideB);
         }
      }

      return sideBArtifacts;
   }
}
