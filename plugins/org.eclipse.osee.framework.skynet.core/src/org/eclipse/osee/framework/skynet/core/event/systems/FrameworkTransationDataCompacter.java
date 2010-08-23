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
package org.eclipse.osee.framework.skynet.core.event.systems;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.LoadedRelation;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * @author Donald G. Dunne
 */
public class FrameworkTransationDataCompacter {

   public static void compact(FrameworkTransactionData data) {
      // Roll-up change information

      for (ArtifactTransactionModifiedEvent xModifiedEvent : data.getXModifiedEvents()) {
         if (xModifiedEvent instanceof ArtifactModifiedEvent) {
            ArtifactModifiedEvent xArtifactModifiedEvent = (ArtifactModifiedEvent) xModifiedEvent;
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Added) {
               if (xArtifactModifiedEvent.artifact != null) {
                  data.cacheAddedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (data.branchId == -1) {
                     data.branchId = xArtifactModifiedEvent.artifact.getBranch().getId();
                  }
               } else {
                  data.unloadedAddedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (data.branchId == -1) {
                     data.branchId = xArtifactModifiedEvent.unloadedArtifact.getBranchId();
                  }
               }
            }
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Deleted) {
               if (xArtifactModifiedEvent.artifact != null) {
                  data.cacheDeletedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (data.branchId == -1) {
                     data.branchId = xArtifactModifiedEvent.artifact.getBranch().getId();
                  }
               } else {
                  data.unloadedDeletedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (data.branchId == -1) {
                     data.branchId = xArtifactModifiedEvent.unloadedArtifact.getBranchId();
                  }
               }
            }
            if (xArtifactModifiedEvent.artifactModType == ArtifactModType.Changed) {
               if (xArtifactModifiedEvent.artifact != null) {
                  data.cacheChangedArtifacts.add(xArtifactModifiedEvent.artifact);
                  if (data.branchId == -1) {
                     data.branchId = xArtifactModifiedEvent.artifact.getBranch().getId();
                  }
               } else {
                  data.unloadedChangedArtifacts.add(xArtifactModifiedEvent.unloadedArtifact);
                  if (data.branchId == -1) {
                     data.branchId = xArtifactModifiedEvent.unloadedArtifact.getBranchId();
                  }
               }
            }
         }
         if (xModifiedEvent instanceof RelationModifiedEvent) {
            RelationModifiedEvent xRelationModifiedEvent = (RelationModifiedEvent) xModifiedEvent;
            UnloadedRelation unloadedRelation = xRelationModifiedEvent.unloadedRelation;
            LoadedRelation loadedRelation = null;
            // If link is loaded, get information from link
            if (xRelationModifiedEvent.link != null) {
               RelationLink link = xRelationModifiedEvent.link;
               // Get artifact A/B if loaded in artifact cache
               Artifact artA = ArtifactCache.getActive(link.getAArtifactId(), link.getABranch());
               Artifact artB = ArtifactCache.getActive(link.getBArtifactId(), link.getBBranch());
               try {
                  loadedRelation =
                     new LoadedRelation(artA, artB, xRelationModifiedEvent.link.getRelationType(),
                        xRelationModifiedEvent.branch, unloadedRelation);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            // Else, get information from unloadedRelation (if != null)
            else if (unloadedRelation != null) {
               Artifact artA = ArtifactCache.getActive(unloadedRelation.getArtifactAId(), unloadedRelation.getId());
               Artifact artB = ArtifactCache.getActive(unloadedRelation.getArtifactBId(), unloadedRelation.getId());
               if (artA != null || artB != null) {
                  try {
                     loadedRelation =
                        new LoadedRelation(artA, artB, RelationTypeManager.getType(unloadedRelation.getTypeId()),
                           artA != null ? artA.getBranch() : artB.getBranch(), unloadedRelation);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            }
            if (xRelationModifiedEvent.relationEventType == RelationEventType.Added) {
               if (loadedRelation != null) {
                  data.cacheAddedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     data.cacheRelationAddedArtifacts.add(loadedRelation.getArtifactA());
                     if (data.branchId == -1) {
                        data.branchId = loadedRelation.getArtifactA().getBranch().getId();
                     }
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     data.cacheRelationAddedArtifacts.add(loadedRelation.getArtifactB());
                     if (data.branchId == -1) {
                        data.branchId = loadedRelation.getArtifactB().getBranch().getId();
                     }
                  }
               }
               if (unloadedRelation != null) {
                  data.unloadedAddedRelations.add(unloadedRelation);
               }
            }
            if (xRelationModifiedEvent.relationEventType == RelationEventType.Deleted) {
               if (loadedRelation != null) {
                  data.cacheDeletedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     data.cacheRelationDeletedArtifacts.add(loadedRelation.getArtifactA());
                     if (data.branchId == -1) {
                        data.branchId = loadedRelation.getArtifactA().getBranch().getId();
                        loadedRelation.getBranch();
                     }
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     data.cacheRelationDeletedArtifacts.add(loadedRelation.getArtifactB());
                     if (data.branchId == -1) {
                        data.branchId = loadedRelation.getArtifactB().getBranch().getId();
                     }
                  }
               }
               if (unloadedRelation != null) {
                  data.unloadedDeletedRelations.add(unloadedRelation);
                  if (data.branchId == -1) {
                     data.branchId = unloadedRelation.getId();
                  }
               }
            }
            if (xRelationModifiedEvent.relationEventType == RelationEventType.ModifiedRationale) {
               if (loadedRelation != null) {
                  data.cacheChangedRelations.add(loadedRelation);
                  if (loadedRelation.getArtifactA() != null) {
                     data.cacheRelationChangedArtifacts.add(loadedRelation.getArtifactA());
                     if (data.branchId == -1) {
                        data.branchId = loadedRelation.getArtifactA().getBranch().getId();
                     }
                  }
                  if (loadedRelation.getArtifactB() != null) {
                     data.cacheRelationChangedArtifacts.add(loadedRelation.getArtifactB());
                     if (data.branchId == -1) {
                        data.branchId = loadedRelation.getArtifactB().getBranch().getId();
                     }
                  }
               }
               if (unloadedRelation != null) {
                  data.unloadedChangedRelations.add(unloadedRelation);
                  if (data.branchId == -1) {
                     data.branchId = unloadedRelation.getId();
                  }
               }
            }
         }
      }
      // Clean out known duplicates
      data.cacheChangedArtifacts.removeAll(data.cacheDeletedArtifacts);
      data.cacheAddedArtifacts.removeAll(data.cacheDeletedArtifacts);
   }
}
