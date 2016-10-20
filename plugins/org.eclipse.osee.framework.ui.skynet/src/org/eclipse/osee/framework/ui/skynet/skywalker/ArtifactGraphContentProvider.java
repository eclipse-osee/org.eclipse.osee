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

package org.eclipse.osee.framework.ui.skynet.skywalker;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

/**
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public class ArtifactGraphContentProvider implements IGraphEntityContentProvider {

   private final SkyWalkerOptions options;

   public ArtifactGraphContentProvider(SkyWalkerOptions options) {
      super();
      this.options = options;
   }

   @SuppressWarnings("deprecation")
   @Override
   public Object[] getConnectedTo(Object entity) {
      List<Artifact> otherItems = new LinkedList<>();

      // Don't want to create any links to artifacts that are NOT in displayArtifacts
      try {
         Artifact artifact = (Artifact) entity;
         for (Object obj : options.getSelectedRelTypes()) {
            if (obj instanceof RelationType) {
               RelationType relationType = (RelationType) obj;
               if (options.isValidRelationType(relationType)) {
                  for (Artifact art : artifact.getRelatedArtifacts(relationType)) {
                     if (options.isValidArtifactType(art.getArtifactType()) && displayArtifacts.contains(art)) {
                        otherItems.add(art);
                     }
                  }
               }
            }
            if (obj instanceof RelationTypeSide) {
               RelationTypeSide side = (RelationTypeSide) obj;
               if (options.isValidRelationLinkDescriptorSide(side)) {
                  for (Artifact art : RelationManager.getRelatedArtifacts(artifact, side)) {
                     if (options.isValidArtifactType(art.getArtifactType()) && displayArtifacts.contains(art)) {
                        otherItems.add(art);
                     }
                  }
               }
            }
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return otherItems.toArray();
   }
   private final Set<Artifact> displayArtifacts = new HashSet<>();

   @Override
   public Object[] getElements(Object inputElement) {
      // Only perform this method for top level artifact
      if (inputElement.equals(options.getArtifact())) {
         displayArtifacts.clear();
         displayArtifacts.add((Artifact) inputElement);
         getDescendants(displayArtifacts, (Artifact) inputElement, options.getLevels());
         return displayArtifacts.toArray();
      }
      return null;
   }

   @SuppressWarnings("deprecation")
   private void getDescendants(Collection<Artifact> displayArtifacts, Artifact artifact, int level) {
      if (level == 0) {
         return;
      } else {
         try {
            for (Object obj : options.getSelectedRelTypes()) {
               if (obj instanceof RelationType) {
                  RelationType relationType = (RelationType) obj;
                  if (options.isValidRelationType(relationType)) {
                     for (Artifact art : artifact.getRelatedArtifacts(relationType)) {
                        if (options.isValidArtifactType(art.getArtifactType())) {
                           displayArtifacts.add(art);
                           getDescendants(displayArtifacts, art, level - 1);
                        }
                     }
                  }
               }
               if (obj instanceof RelationTypeSide) {
                  RelationTypeSide side = (RelationTypeSide) obj;
                  if (options.isValidRelationLinkDescriptorSide(side)) {
                     for (Artifact art : RelationManager.getRelatedArtifacts(artifact, side)) {
                        if (options.isValidArtifactType(art.getArtifactType())) {
                           displayArtifacts.add(art);
                           getDescendants(displayArtifacts, art, level - 1);
                        }
                     }
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public double getWeight(Object entity1, Object entity2) {
      return 0;
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

}
