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

package org.eclipse.osee.framework.ui.skynet.render;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.define.api.publishing.LinkType;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactGuidToWordML {

   private final OseeLinkBuilder linkBuilder;

   public ArtifactGuidToWordML(OseeLinkBuilder linkBuilder) {
      this.linkBuilder = linkBuilder;
   }

   public List<String> resolveAsOseeLinks(BranchId branch, List<String> artifactGuids) {
      List<String> mlLinks = new ArrayList<>();
      for (String guid : artifactGuids) {
         Artifact artifact = ArtifactQuery.checkArtifactFromId(guid, branch, DeletionFlag.EXCLUDE_DELETED);
         if (artifact != null) {
            mlLinks.add(linkBuilder.getWordMlLink(LinkType.OSEE_SERVER_LINK, artifact));
         } else {
            // Artifact does not exist - don't add a link
         }
      }
      return mlLinks;
   }
}
