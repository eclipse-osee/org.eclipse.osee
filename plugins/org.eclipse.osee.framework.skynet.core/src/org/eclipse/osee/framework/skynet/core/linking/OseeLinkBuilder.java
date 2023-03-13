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

package org.eclipse.osee.framework.skynet.core.linking;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.util.xml.XmlEncoderDecoder;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactUrlClient;

/**
 * @author Roberto E. Escobar
 */
public class OseeLinkBuilder {

   public OseeLinkBuilder() {
      super();
   }

   private String getLinkId(LinkType destLinkType, Artifact artifact, PresentationType presentationType) {
      String toReturn;
      if (destLinkType == LinkType.OSEE_SERVER_LINK) {
         toReturn = XmlEncoderDecoder.textToXml(
            new ArtifactUrlClient().getOpenInOseeLink(artifact, presentationType).toString()).toString();
      } else {
         toReturn = artifact.getIdString();
      }
      return toReturn;
   }

   private String getLinkText(LinkType linkType, Artifact artifact) {
      StringBuilder builder = new StringBuilder();
      if (linkType.isParagraphRequired()) {
         builder.append(artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "Undefined"));
      }
      if (linkType.isArtifactNameRequired()) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append(artifact.getName());
      }
      if (artifact.isDeleted()) {
         builder.append(" (DELETED)");
      }
      return XmlEncoderDecoder.textToXml(builder).toString();
   }

   public String getWordMlLink(LinkType destLinkType, Artifact artifact) {
      return getWordMlLink(destLinkType, artifact, PresentationType.DEFAULT_OPEN);
   }

   public String getWordMlLink(LinkType destLinkType, Artifact artifact, PresentationType presentationType) {

      String linkId = getLinkId(destLinkType, artifact, presentationType);
      String linkText = getLinkText(destLinkType, artifact);

      return WordCoreUtil.getLink(destLinkType, linkId, linkText).toString();
   }

}