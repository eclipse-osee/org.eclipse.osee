/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.skynet.core.topic.event.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicRelationTransfer;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTopicTypeEventFilter implements ITopicEventFilter {

   private final Collection<ArtifactTypeToken> artifactTypes;

   /**
    * Provide artifact types of events to be passed through. All others will be ignored.
    */
   public ArtifactTopicTypeEventFilter(ArtifactTypeToken... artifactTypes) {
      this.artifactTypes = Arrays.asList(artifactTypes);
   }

   @Override
   public boolean isMatch(BranchId branch) {
      return true;
   }

   @Override
   public boolean isMatchArtifacts(List<? extends EventTopicArtifactTransfer> transferArts) {
      try {
         for (EventTopicArtifactTransfer transferArt : transferArts) {
            if (typeMatches(transferArt.getArtifactTypeId())) {
               return true;
            }
         }

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public boolean isMatchRelationArtifacts(List<? extends EventTopicRelationTransfer> transferRelations) {
      for (EventTopicRelationTransfer relation : transferRelations) {
         if (typeMatches(relation.getArtAIdType()) || typeMatches(relation.getArtBIdType())) {
            return true;
         }
      }
      return false;
   }

   private boolean typeMatches(ArtifactTypeId artifactTypeId) {

      ArtifactTypeToken typeToken = ServiceUtil.getOrcsTokenService().getArtifactType(artifactTypeId.getId());
      for (ArtifactTypeToken artifactType : artifactTypes) {
         if (typeToken.inheritsFrom(artifactType)) {
            return true;
         }
         for (ArtifactTypeToken matchArtType : artifactTypes) {
            if (matchArtType.equals(typeToken)) {
               return true;
            }
         }
      }

      return false;
   }

}
