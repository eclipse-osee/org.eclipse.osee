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
package org.eclipse.osee.framework.skynet.core.event2.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeEventFilter implements IEventFilter {

   private final Collection<String> artTypeGuids;

   /**
    * Provide artifact types of events to be passed through. All others will be ignored.
    */
   public ArtifactTypeEventFilter(String artTypeGuid) {
      this.artTypeGuids = Arrays.asList(artTypeGuid);
   }

   /**
    * Provide artifact types of events to be passed through. All others will be ignored.
    */
   public ArtifactTypeEventFilter(Collection<String> artTypeGuids) {
      this.artTypeGuids = artTypeGuids;
   }

   /**
    * Provide artifact types of events to be passed through. All others will be ignored.
    */
   public ArtifactTypeEventFilter(IArtifactType... artifactTypes) {
      this.artTypeGuids = new HashSet<String>();
      for (IArtifactType artifactType : artifactTypes) {
         this.artTypeGuids.add(artifactType.getGuid());
      }
   }

   @Override
   public boolean isMatch(IBasicGuidArtifact guidArt) {
      return this.artTypeGuids.contains(guidArt.getArtTypeGuid());
   }

   @Override
   public boolean isMatch(IBasicGuidRelation relArt) {
      return this.artTypeGuids.contains(relArt.getArtA().getArtTypeGuid()) ||
      //
      this.artTypeGuids.contains(relArt.getArtB().getArtTypeGuid());

   }

}
