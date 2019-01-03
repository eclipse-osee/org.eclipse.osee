/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.search;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author John Misinco
 */
public class AtsArtifactQuery {

   public static Artifact getArtifactFromId(String guidOrAtsId) {
      List<Artifact> artifacts = new LinkedList<>();

      if (GUID.isValid(guidOrAtsId)) {
         artifacts.add(ArtifactQuery.getArtifactFromId(guidOrAtsId, AtsClientService.get().getAtsBranch()));
      } else {
         artifacts.addAll(ArtifactQuery.getArtifactListFromAttributeValues(AtsAttributeTypes.AtsId,
            Collections.singleton(guidOrAtsId), AtsClientService.get().getAtsBranch(), 1));
      }

      if (artifacts.isEmpty()) {
         throw new ArtifactDoesNotExist("AtsArtifactQuery: No artifact found with id %s on ATS branch", guidOrAtsId);
      }
      if (artifacts.size() > 1) {
         throw new MultipleArtifactsExist("%d artifacts found with id %s", artifacts.size(), guidOrAtsId);
      }
      return artifacts.iterator().next();
   }

   public static List<Artifact> getArtifactListFromIds(Collection<String> guidsOrAtsIds) {
      List<Artifact> toReturn = new LinkedList<>();
      List<String> guids = new LinkedList<>();
      List<String> atsIds = new LinkedList<>();
      for (String guidOrAtsId : guidsOrAtsIds) {
         if (GUID.isValid(guidOrAtsId)) {
            guids.add(guidOrAtsId);
         } else {
            atsIds.add(guidOrAtsId.toUpperCase());
         }
      }

      if (!guids.isEmpty()) {
         List<Artifact> fromIds = ArtifactQuery.getArtifactListFromIds(guids, AtsClientService.get().getAtsBranch());
         toReturn.addAll(fromIds);
      }

      if (!atsIds.isEmpty()) {
         List<Artifact> fromIds = ArtifactQuery.getArtifactListFromAttributeValues(AtsAttributeTypes.AtsId, atsIds,
            AtsClientService.get().getAtsBranch(), atsIds.size());
         toReturn.addAll(fromIds);
      }

      return toReturn;
   }

   public static Artifact getArtifactFromId(long id) {
      return ArtifactQuery.getArtifactFromId(ArtifactId.valueOf(id), AtsClientService.get().getAtsBranch());
   }
}