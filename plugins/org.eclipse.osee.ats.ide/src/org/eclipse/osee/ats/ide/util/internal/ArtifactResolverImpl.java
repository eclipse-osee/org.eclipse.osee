/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.search.AtsArtifactQuery;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ArtifactResolverImpl implements IArtifactResolver {

   @Override
   public ArtifactId get(IAtsObject atsObject) {
      if (atsObject instanceof Artifact) {
         return AtsClientService.get().getQueryServiceClient().getArtifact(atsObject);
      }
      Artifact artifact = AtsArtifactQuery.getArtifactFromId(atsObject.getId());
      return artifact;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <A extends ArtifactId> A get(IAtsWorkItem workItem, Class<?> clazz) {
      ArtifactId artifact = get(workItem);
      if (clazz.isInstance(artifact)) {
         return (A) artifact;
      }
      return null;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <A extends ArtifactId> List<A> get(Collection<? extends IAtsWorkItem> workItems, Class<?> clazz) {
      Assert.isNotNull(workItems, "Work Items can not be null");
      List<A> arts = new ArrayList<>();
      for (IAtsWorkItem workItem : workItems) {
         Artifact artifact = get(workItem, clazz);
         if (artifact != null) {
            arts.add((A) artifact);
         }
      }
      return arts;
   }

   @Override
   public ArtifactTypeToken getArtifactType(IAtsWorkItem workItem) {
      Assert.isNotNull(workItem, "Work Item can not be null");
      return AtsClientService.get().getQueryServiceClient().getArtifact(workItem).getArtifactType();
   }
}