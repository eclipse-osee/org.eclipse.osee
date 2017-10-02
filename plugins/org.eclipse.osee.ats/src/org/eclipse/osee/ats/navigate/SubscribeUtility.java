/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.navigate;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;

/**
 * @author Donald G. Dunne
 */
public class SubscribeUtility {

   /**
    * Creates new subscriptions that don't already exist and removes relations to subscriptions that are not in
    * collection
    * 
    * @param artifacts to subscribe or empty to remove all
    */
   public static final void setSubcriptionsAndPersist(Artifact userArtifact, RelationTypeSide relationSide, Collection<? extends Artifact> artifacts, IArtifactType artifactType, String persistComment)  {
      RelationTypeSideSorter sorter =
         RelationManager.createTypeSideSorter(userArtifact, relationSide, relationSide.getSide());
      Collection<Artifact> currentlyRelated = userArtifact.getRelatedArtifacts(relationSide, Artifact.class);
      // Add new relations if don't exist
      for (Artifact art : artifacts) {
         if (art.isOfType(artifactType) && !currentlyRelated.contains(art)) {
            userArtifact.addRelation(sorter.getSorterId(), relationSide, art);
         }
      }
      // Remove relations that have been removed
      for (Artifact artifact : currentlyRelated) {
         if (artifact.isOfType(artifactType) && !artifacts.contains(artifact)) {
            userArtifact.deleteRelation(relationSide, artifact);
         }
      }
      userArtifact.persist(persistComment);
   }
}
