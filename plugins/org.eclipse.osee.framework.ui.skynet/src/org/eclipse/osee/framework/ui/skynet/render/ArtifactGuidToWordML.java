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
package org.eclipse.osee.framework.ui.skynet.render;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactGuidToWordML {

   private final OseeLinkBuilder linkBuilder;

   public ArtifactGuidToWordML(OseeLinkBuilder linkBuilder) {
      this.linkBuilder = linkBuilder;
   }

   public List<String> resolveAsOseeLinks(Branch branch, List<String> artifactGuids) throws OseeCoreException {
      List<String> mlLinks = new ArrayList<String>();
      for (String guid : artifactGuids) {
         Artifact artifact = ArtifactQuery.checkArtifactFromId(guid, branch);
         if (artifact != null) {
            mlLinks.add(linkBuilder.getWordMlLink(LinkType.OSEE_SERVER_LINK, artifact));
         } else {
            // Artifact does not exist - don't add a link
         }
      }
      return mlLinks;
   }
}
