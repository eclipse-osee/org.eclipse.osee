/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.skywalker.arttype;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeContentProvider implements IGraphEntityContentProvider {

   private final Set<ArtifactTypeToken> parentTypes = new HashSet<>();
   private ArtifactTypeToken selectedArtType = null;
   private boolean singleLevel = false;

   public ArtifactTypeContentProvider() {
      super();
   }

   @Override
   public Object[] getConnectedTo(Object entity) {
      try {
         if (entity instanceof ArtifactTypeToken) {
            ArtifactTypeToken artifactType = (ArtifactTypeToken) entity;
            if (parentTypes.contains(artifactType)) {
               if (!singleLevel || selectedArtType.equals(artifactType)) {
                  Set<ArtifactTypeToken> artifactTypes = new HashSet<>();
                  for (ArtifactTypeToken childType : artifactType.getDirectDescendantTypes()) {
                     if (parentTypes.contains(childType)) {
                        artifactTypes.add(childType);
                     }
                  }
                  return artifactTypes.toArray();
               }
            } else if (parentTypes.contains(entity)) {
               if (!singleLevel || selectedArtType.equals(artifactType)) {
                  Set<ArtifactTypeToken> artifactTypes = new HashSet<>();
                  for (ArtifactTypeToken childType : artifactType.getSuperTypes()) {
                     if (parentTypes.contains(childType)) {
                        artifactTypes.add(childType);
                     }
                  }
                  return artifactTypes.toArray();
               }
            } else if (selectedArtType.equals(entity) && selectedArtType.notEqual(CoreArtifactTypes.Artifact)) {
               Set<ArtifactTypeToken> artifactTypes = new HashSet<>();
               // parents
               if (!singleLevel || selectedArtType.equals(artifactType)) {
                  for (ArtifactTypeToken childType : artifactType.getSuperTypes()) {
                     if (parentTypes.contains(childType)) {
                        artifactTypes.add(childType);
                     }
                  }
               }
               // children
               if (!singleLevel || selectedArtType.equals(artifactType)) {
                  artifactTypes.addAll(artifactType.getDirectDescendantTypes());
               }
               return artifactTypes.toArray();
            } else {
               if (!singleLevel || selectedArtType.equals(artifactType)) {
                  return artifactType.getDirectDescendantTypes().toArray();
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      try {
         if (inputElement instanceof ArtifactTypeToken) {
            ArtifactTypeToken artifactType = (ArtifactTypeToken) inputElement;
            if (selectedArtType.equals(artifactType)) {
               Set<ArtifactTypeToken> artifactTypes = new HashSet<>();
               getParents(artifactType, artifactTypes);
               if (!parentTypes.contains(artifactType)) {
                  artifactTypes.add(artifactType);
                  getDecendents(artifactType, artifactTypes);
               }
               return artifactTypes.toArray();
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   public void getParents(ArtifactTypeToken artifactType, Set<ArtifactTypeToken> parents) {
      for (ArtifactTypeToken artType : artifactType.getSuperTypes()) {
         if (!singleLevel || selectedArtType.equals(
            artifactType) || selectedArtType.getDirectDescendantTypes().contains(artifactType)) {
            parents.add(artType);
            parentTypes.add(artType);
            getParents(artType, parents);
         }
      }
   }

   public void getDecendents(ArtifactTypeToken artifactType, Set<ArtifactTypeToken> decendents) {
      // If not singleLevel, show all levels; else only show children if selected this artifact type
      if (!singleLevel || selectedArtType.equals(artifactType)) {
         for (ArtifactTypeToken artType : artifactType.getDirectDescendantTypes()) {
            if (!parentTypes.contains(artType)) {
               decendents.add(artType);
               getDecendents(artType, decendents);
            }
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

   public Set<ArtifactTypeToken> getParentTypes() {
      return parentTypes;
   }

   public ArtifactTypeToken getSelectedArtType() {
      return selectedArtType;
   }

   public void setSelectedArtType(ArtifactTypeToken selectedArtType) {
      this.selectedArtType = selectedArtType;
   }

   public boolean isSingleLevel() {
      return singleLevel;
   }

   public void setSingleLevel(boolean singleLevel) {
      this.singleLevel = singleLevel;
   }

}
