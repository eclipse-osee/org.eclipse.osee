/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class ArtifactResolverImpl implements IArtifactResolver {

   private final AtsApi atsApi;

   public ArtifactResolverImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public ArtifactId get(IAtsObject atsObject) {
      if (atsObject.getStoreObject() instanceof ArtifactReadable) {
         return atsObject.getStoreObject();
      }
      return atsApi.getQueryService().getArtifact(atsObject);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <A extends ArtifactId> A get(IAtsWorkItem workItem, Class<?> clazz) {
      Assert.isNotNull(workItem, "Work Item can not be null");
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
         ArtifactReadable artifact = get(workItem, clazz);
         if (artifact != null) {
            arts.add((A) artifact);
         }
      }
      return arts;
   }

   @Override
   public ArtifactTypeToken getArtifactType(IAtsWorkItem workItem) {
      Assert.isNotNull(workItem, "Work Item can not be null");
      return ((ArtifactReadable) workItem.getStoreObject()).getArtifactType();
   }
}