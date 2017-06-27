/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeProvider;
import org.eclipse.osee.framework.skynet.core.artifact.IArtifactTypeProvider;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeEventFilter implements IEventFilter {

   private final Collection<ArtifactTypeId> artifactTypes;
   private final IArtifactTypeProvider typeProvider;

   /**
    * Provide artifact types of events to be passed through. All others will be ignored.
    */
   public ArtifactTypeEventFilter(ArtifactTypeId... artifactTypes) {
      this(new ArtifactTypeProvider(), artifactTypes);
   }

   public ArtifactTypeEventFilter(IArtifactTypeProvider typeProvider, ArtifactTypeId... artifactTypes) {
      this.typeProvider = typeProvider;
      this.artifactTypes = Arrays.asList(artifactTypes);
   }

   /**
    * Return true if any artifact matches any of the desired artifact types
    */
   @Override
   public boolean isMatchArtifacts(List<? extends DefaultBasicGuidArtifact> guidArts) {
      try {
         for (DefaultBasicGuidArtifact guidArt : guidArts) {
            ArtifactTypeId artType = guidArt.getArtifactType();
            for (ArtifactTypeId artifactType : artifactTypes) {
               if (typeProvider.inheritsFrom(artType, artifactType)) {
                  return true;
               }
               for (ArtifactTypeId matchArtType : artifactTypes) {
                  if (matchArtType.equals(artType)) {
                     return true;
                  }
               }
            }
         }

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   /**
    * Return true if any artifact on either side of relation matches any of the desired artifact types
    */
   @Override
   public boolean isMatchRelationArtifacts(List<? extends IBasicGuidRelation> relations) {
      for (IBasicGuidRelation relation : relations) {
         if (isMatchArtifacts(Arrays.asList(relation.getArtA(), relation.getArtB()))) {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean isMatch(BranchId branch) {
      return true;
   }

}
