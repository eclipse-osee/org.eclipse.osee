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

import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.hyper.IHyperArtifact;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;

public abstract class ATSArtifact extends Artifact implements IHyperArtifact, IATSArtifact {

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    */
   public ATSArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public String toString() {
      return getInternalDescriptiveName();
   }

   public String getHyperlinkHtml() {
      return Overview.getOpenHyperlinkHtml(this);
   }

   /**
    * Recursively retrieve artifacts and all it's ATS related artifacts such as tasks, notes, subscriptions, etc... for
    * deletion
    * 
    * @param deleteArts
    * @param allRelated
    * @throws OseeCoreException
    */
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      deleteArts.add(this);
      for (Artifact artifact : getRelatedArtifactsAll()) {
         allRelated.put(artifact, this);
      }
   }
}
