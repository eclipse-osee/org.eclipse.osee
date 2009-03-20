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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.skywalker.RelTypeContentProvider.RelationLinkDescriptorSide;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

/**
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public class ArtifactGraphContentProvider implements IGraphEntityContentProvider {
   // private static final Collection<Artifact>EMPTY_LIST = new ArrayList<Artifact>(0);
   private final SkyWalkerOptions options;

   /**
    * @param levels
    */
   public ArtifactGraphContentProvider(SkyWalkerOptions options) {
      super();
      this.options = options;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.mylar.zest.core.viewers.IGraphEntityContentProvider#getConnectedTo(java.lang.Object)
    */
   public Object[] getConnectedTo(Object entity) {
      List<Artifact> otherItems = new LinkedList<Artifact>();

      // Don't want to create any links to artifacts that are NOT in displayArtifacts
      try {
         Artifact artifact = (Artifact) entity;
         //         List<RelationType> validTypes =
         //               RelationTypeManager.getValidTypes(artifact.getArtifactType(), artifact.getBranch());
         //         for (RelationType relationType : validTypes) {
         //            if (options.isValidRelationType(relationType)) {
         //               for (Artifact art : artifact.getRelatedArtifacts(relationType)) {
         //                  if (options.isValidArtifactType(art.getArtifactType()) && displayArtifacts.contains(art)) {
         //                     otherItems.add(art);
         //                  }
         //               }
         //            }
         //         }
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
            if (obj instanceof RelationLinkDescriptorSide) {
               RelationLinkDescriptorSide side = (RelationLinkDescriptorSide) obj;
               if (options.isValidRelationLinkDescriptorSide(side)) {
                  for (Artifact art : artifact.getRelatedArtifacts(side)) {
                     if (options.isValidArtifactType(art.getArtifactType()) && displayArtifacts.contains(art)) {
                        otherItems.add(art);
                     }
                  }
               }
            }
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return otherItems.toArray();
   }
   private final Set<Artifact> displayArtifacts = new HashSet<Artifact>();

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.mylar.zest.core.viewers.IGraphEntityContentProvider#getElements(java.lang.Object)
    */
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

   private void getDescendants(Collection<Artifact> displayArtifacts, Artifact artifact, int level) {
      // System.out.println("getDecendants level: " + level + " artifact => " + artifact);
      if (level == 0) {
         return;
      } else {
         try {
            //            List<RelationType> validTypes =
            //                  RelationTypeManager.getValidTypes(artifact.getArtifactType(), artifact.getBranch());
            //            for (RelationType relationType : validTypes) {
            //               if (options.isValidRelationType(relationType)) {
            //                  for (Artifact art : artifact.getRelatedArtifacts(relationType)) {
            //                     if (options.isValidArtifactType(art.getArtifactType())) {
            //                        displayArtifacts.add(art);
            //                        getDescendants(displayArtifacts, art, level - 1);
            //                     }
            //                  }
            //               }
            //            }
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
               if (obj instanceof RelationLinkDescriptorSide) {
                  RelationLinkDescriptorSide side = (RelationLinkDescriptorSide) obj;
                  if (options.isValidRelationLinkDescriptorSide(side)) {
                     for (Artifact art : artifact.getRelatedArtifacts(side)) {
                        if (options.isValidArtifactType(art.getArtifactType())) {
                           displayArtifacts.add(art);
                           getDescendants(displayArtifacts, art, level - 1);
                        }
                     }
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.mylar.zest.core.viewers.IGraphEntityContentProvider#getWeight(java.lang.Object,
    *      java.lang.Object)
    */
   public double getWeight(Object entity1, Object entity2) {
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IContentProvider#dispose()
    */
   public void dispose() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
    *      java.lang.Object, java.lang.Object)
    */
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

}
