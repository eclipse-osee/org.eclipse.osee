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
package org.eclipse.osee.framework.skynet.core.linking;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactURL;

/**
 * @author Roberto E. Escobar
 */
public class OseeLinkBuilder {
   private static final String WORDML_INTERNAL_DOC_LINK_FORMAT =
         "<w:r><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:instrText> HYPERLINK \\l \"OSEE.%s\" </w:instrText></w:r><w:r><w:fldChar w:fldCharType=\"separate\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>%s</w:t></w:r><w:r><w:fldChar w:fldCharType=\"end\"/></w:r>";
   private static final String WORDML_LINK_FORMAT =
         "<w:hlink w:dest=\"%s\"><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>%s</w:t></w:r></w:hlink>";
   private static final String WORDML_BOOKMARK_FORMAT =
         "<aml:annotation aml:id=\"\" w:type=\"Word.Bookmark.Start\" w:name=\"OSEE.%s\"/><aml:annotation aml:id=\"\" w:type=\"Word.Bookmark.End\"/>";
   private static final String OSEE_LINK_MARKER = "OSEE_LINK(%s)";

   public OseeLinkBuilder() {
      super();
   }

   public String getWordMlLink(LinkType destLinkType, Artifact artifact) throws OseeCoreException {
      String linkFormat = getLinkFormat(destLinkType);
      String linkId = getLinkId(destLinkType, artifact);
      String linkText = getLinkText(destLinkType, artifact);
      return String.format(linkFormat, linkId, linkText);
   }

   public String getUnknownArtifactLink(String guid, Branch branch) {
      String internalLink = String.format("http://none/unknown?guid=%s&amp;branchId=%s", guid, branch.getId());
      return String.format(WORDML_LINK_FORMAT, internalLink, String.format(
            "Invalid Link: artifact with guid:[%s] on branchId:[%s] does not exist", guid, branch.getId()));
   }

   public String getWordMlBookmark(Artifact source) {
      return String.format(WORDML_BOOKMARK_FORMAT, source.getGuid());
   }

   public String getOseeLinkMarker(String guid) {
      return String.format(OSEE_LINK_MARKER, guid);
   }

   private String escapeXml(String source) {
      return Xml.escape(source).toString();
   }

   private String getLinkFormat(LinkType destLinkType) {
      String toReturn;
      if (destLinkType == LinkType.OSEE_SERVER_LINK) {
         toReturn = WORDML_LINK_FORMAT;
      } else {
         toReturn = WORDML_INTERNAL_DOC_LINK_FORMAT;
      }
      return toReturn;
   }

   private boolean isArtifactNameRequired(LinkType linkType) {
      return linkType == LinkType.OSEE_SERVER_LINK || linkType == LinkType.INTERNAL_DOC_REFERENCE_USE_NAME || linkType == LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME;
   }

   private boolean isParagraphRequired(LinkType linkType) {
      return linkType == LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER || linkType == LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME;
   }

   private String getLinkText(LinkType linkType, Artifact artifact) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      if (isParagraphRequired(linkType)) {
         builder.append(artifact.getSoleAttributeValue(CoreAttributeTypes.PARAGRAPH_NUMBER, "Undefined"));
      }
      if (isArtifactNameRequired(linkType)) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append(artifact.getName());
      }
      if (artifact.isDeleted()) {
         builder.append(" (DELETED)");
      }
      return escapeXml(builder.toString());
   }

   private String getLinkId(LinkType destLinkType, Artifact artifact) throws OseeCoreException {
      String toReturn;
      if (destLinkType == LinkType.OSEE_SERVER_LINK) {
         toReturn = escapeXml(ArtifactURL.getOpenInOseeLink(artifact).toString());
      } else {
         toReturn = artifact.getGuid();
      }
      return toReturn;
   }

}
