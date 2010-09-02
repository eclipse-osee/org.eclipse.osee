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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeEventFilter implements IEventFilter {

   private final Collection<IArtifactType> artifactTypes;

   /**
    * Provide artifact types of events to be passed through. All others will be ignored.
    */
   public ArtifactTypeEventFilter(IArtifactType... artifactTypes) {
      this.artifactTypes = Collections.getAggregate(artifactTypes);
   }

   @Override
   public boolean isMatch(IBasicGuidArtifact guidArt) {
      try {
         ArtifactType artType = ArtifactTypeManager.getTypeByGuid(guidArt.getArtTypeGuid());
         for (IArtifactType artifactType : artifactTypes) {
            if (artType.inheritsFrom(artifactType)) {
               return true;
            }
         }
         return this.artifactTypes.contains(guidArt.getArtTypeGuid());

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public boolean isMatch(IBasicGuidRelation relArt) {
      return isMatch(relArt.getArtA()) || isMatch(relArt.getArtB());
   }

   @Override
   public boolean isMatch(String branchGuid) {
      return true;
   }

}
