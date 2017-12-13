/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.api;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 * @author David W. Miller Reduced from the client side class to only support unlinking
 */
public class OseeLinkBuilder {
   private static final String WORDML_INTERNAL_DOC_LINK_FORMAT =
      "<w:r><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:instrText> HYPERLINK \\l \"OSEE.%s\" </w:instrText></w:r><w:r><w:fldChar w:fldCharType=\"separate\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>%s</w:t></w:r><w:r><w:fldChar w:fldCharType=\"end\"/></w:r>";
   private static final String WORDML_LINK_FORMAT =
      "<w:hlink w:dest=\"%s\"><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>%s</w:t></w:r></w:hlink>";
   private static final String WORDML_BOOKMARK_FORMAT =
      "<aml:annotation aml:id=\"%s\" w:type=\"Word.Bookmark.Start\" w:name=\"OSEE.%s\"/><aml:annotation aml:id=\"%s\" w:type=\"Word.Bookmark.End\"/>";
   private static final String OSEE_LINK_MARKER = "OSEE_LINK(%s)";

   // @formatter:off
   public static String START_BIN_DATA =
      "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a" +
      "HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy" +
      "MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUAEcDASIA" +
      "AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA" +
      "AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3" +
      "ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm" +
      "p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA" +
      "AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx" +
      "BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK" +
      "U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3" +
      "uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDuI/HV" +
      "3L4l1XTJvEnhmwtrKK1aC5uYSRd+bEHZkzcKNoPTBbhhz3Ol478fWvhjR9TFhcW0us2awt9nmRmQ" +
      "eY4AViMDcU3sF3bsKWxgGix0bxLpfi3Xtahs9JnTVltSYnv5IzC0UW1hkQncMk4PHAHHOBleLPh/" +
      "rWrz+Kv7Nu9PWHXls2b7TvDRtAcbPlBGCAG3c9Nu3ncPfhHBSxEPaWUEovdav3OZOyv/ADPXe2lj" +
      "J81nY0ta8fjw7N4nluxDdw6V9mWC2tYpRKryoSBM5GxVJxhlzgcHLEA9pBMtxBHMgcJIodRIhRgC" +
      "M8qwBB9iARXAa98P9Q1l/G4W7tok11bI2pO4lWgAyHGOASAMjOAc44wen/4S/wAPwfub/XtGtryP" +
      "5Z4P7QjPlyDhlycHg5HIB9hXHiKNGdOH1dXlpe3+GHS383N+vQpN31ObtPGOsap4k1fSrW40azvr" +
      "G+8iHS7+OVJbqEYPmLKG/iUOwxG2BtzkHJ2bfxxp0/iHW9KeG5hGkrF5k0kD4kdyRtUbckk7Ao6y" +
      "FjtBAycbxb4O1jxRJIj2+jRTx3Mb2OswySxXdrErBsbAp3sMvj94qkkHCkVY1Hwjrp1Xxbd6RqcN" +
      "pJrltbiG4ywkt5Yl2bcAH5WXPzghlJ4U4zXTKGCqRV2otpddneN3dJ3VuZ6rmVnvoT7yNh/G3h+L" +
      "TdQv5r14YdOZEu1mtpY5YS+Nm6NlD4bcMHGDz6GoLTxvY33i+Hw9BZ6gsslm90ZZ7OWELhgoG10B" +
      "wfm+Y4XIABJOBx0vww1N9D8WWNpBpOnJqy2KWlvDcSSRwiAjducxgktjOcEkk59a7WXw/dj4jW/i" +
      "SGSF7ZtMbT54nYq6fvPMV1wCGyeCDtx1yelZ1KGAgpcsm3Z21W/LFrZd3Jb20GnNnR0UUV45oFFF" +
      "FABRRRQAUUUUAFFFFABRRRQB/9k=";

   public static final String END_BIN_DATA =
      "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a" +
      "HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy" +
      "MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUAEcDASIA" +
      "AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA" +
      "AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3" +
      "ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm" +
      "p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA" +
      "AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx" +
      "BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK" +
      "U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3" +
      "uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD1jUPE" +
      "2oR+NF8NabpdtcTf2f8Ab2mubxoVC+YY9uFjck5wf886s2t6fYIE1XUNPsrpIFnmie6UCNSQu7Lb" +
      "SV3naGIGTjoeK5HxB4emufiUmsXXhn+29KGji1CYt32zecWztldei55H9761V17wjfal4hvL210V" +
      "I7U+EpbCzjYxAwXLFgsYAbCkIxXI+XBIzivYWGwk1BOSj7t3qt+2r0+5fMzvJXO11/XrTQNMubma" +
      "SFrmO2muILV5gj3HlIXYLnk8DkgHGc1Pouo/2xoOnan5Xk/bLaO48vdu2b1DYzgZxnrivMdQ8E68" +
      "1ra7dJhvDJ4QXSHiedB5FyhVwWzweR8pXPzKMlR8w9H8MWc+neE9GsbqPy7m2sYIZUyDtdY1BGRw" +
      "eQelY4rD4elh04TUpX79PS44tt6mUnifVb3xVrWiaZpFlL/ZXkeZNc37Rb/NTeMKsT9MEdfSp/GH" +
      "i+08J6Je3mIbq8tokm+w/aAkjRtKse/oSFy3XGMjFc3J4cdPH/iXU9T8Hf21Z332X7HJttZNuyLa" +
      "/EsilcnHbnb9KyvG/gnXtSuvGH2HSYb4av8AYJrSUzovktCNjgbuQ+CfQbWb5s/KeylhcFOvTUpJ" +
      "RtBvXdvk5k3zabyb0VrNIlykkzstT8c2OhTa62rrDBaaZ5IieO6SWW5eRC2zyh8yNxxu6jLcAEjp" +
      "oJ4rmCOeCVJYZVDxyRsGV1IyCCOCCO9eZeJfBeuas/xCW2tkA1ZbBrFnlUCYwgF165U5GBuwMkc4" +
      "5r02CRpYI5HheF3UM0UhBZCR907SRkdOCR7muHF0sPClCVJ+87X1/uQe3q5J+a6bFRbvqSUUUV55" +
      "YUUUUAFZV54Y8P6jdPdX2h6Zc3MmN809pG7tgYGSRk8AD8KKKuFScHeDt6Ba5owQRW0EcEESRQxK" +
      "EjjjUKqKBgAAcAAdqkooqG76sAooooAKKKKAP//Z";

   private static final String PIC_TAG_DATA =
      "<w:r><w:pict>" +
      "<v:shapetype id=\"_x0000_t75\" coordsize=\"21600,21600\" o:spt=\"75\" o:preferrelative=\"t\""+
      " path=\"m@4@5l@4@11@9@11@9@5xe\" filled=\"f\" stroked=\"f\">" +
      "<v:stroke joinstyle=\"miter\"/>" +
      "<v:formulas><v:f eqn=\"if lineDrawn pixelLineWidth 0\"/><v:f eqn=\"sum @0 1 0\"/>" +
      "<v:f eqn=\"sum 0 0 @1\"/><v:f eqn=\"prod @2 1 2\"/><v:f eqn=\"prod @3 21600 pixelWidth\"/>" +
      "<v:f eqn=\"prod @3 21600 pixelHeight\"/><v:f eqn=\"sum @0 0 1\"/><v:f eqn=\"prod @6 1 2\"/>" +
      "<v:f eqn=\"prod @7 21600 pixelWidth\"/><v:f eqn=\"sum @8 21600 0\"/>" +
      "<v:f eqn=\"prod @7 21600 pixelHeight\"/><v:f eqn=\"sum @10 21600 0\"/></v:formulas>" +
      "<v:path o:extrusionok=\"f\" gradientshapeok=\"t\" o:connecttype=\"rect\"/>"+
      "<o:lock v:ext=\"edit\" aspectratio=\"t\"/></v:shapetype>" +
      "<w:binData w:name=\"wordml://%s\">%s</w:binData>" +
      "<v:shape id=\"_x0000_i1025\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\">" +
      "<v:imagedata src=\"wordml://%s\" o:title=\"%s\"/></v:shape></w:pict></w:r>";
   // @formatter:on

   public String getUnknownArtifactLink(String guid, BranchId branch) {
      String message =
         String.format("Invalid Link: artifact with guid:[%s] on branchUuid:[%s] does not exist", guid, branch);
      String internalLink = String.format("http://none/unknown?guid=%s&amp;branchUuid=%s", guid, branch);
      return String.format(WORDML_LINK_FORMAT, internalLink, message);
   }

   public String getWordMlBookmark(ArtifactReadable source) {
      return getWordMlBookmark(source.getGuid());
   }

   public String getWordMlBookmark(String guid) {
      return String.format(WORDML_BOOKMARK_FORMAT, 0, guid, 0);
   }

   public String getStartEditImage(String guid) {
      return getEditImage(true, guid);
   }

   public String getEndEditImage(String guid) {
      return getEditImage(false, guid);
   }

   private String getEditImage(boolean isStart, String guid) {
      String imageId = String.format("%s_%s", guid, isStart ? "START.jpg" : "END.jpg");
      String imageData = isStart ? START_BIN_DATA : END_BIN_DATA;
      return String.format(PIC_TAG_DATA, imageId, imageData, imageId, guid);
   }

   public String getOseeLinkMarker(String guid) {
      return String.format(OSEE_LINK_MARKER, guid);
   }

   public String getWordMlLink(LinkType destLinkType, ArtifactReadable artifact, TransactionId txId, String sessionId, String permanentUrl) {
      return getWordMlLink(destLinkType, artifact, txId, sessionId, PresentationType.DEFAULT_OPEN, permanentUrl);
   }

   public String getWordMlLink(LinkType destLinkType, ArtifactReadable artifact, TransactionId txId, String sessionId, PresentationType presentationType, String permanentUrl) throws OseeCoreException {
      String linkFormat = getLinkFormat(destLinkType);
      String linkId = getLinkId(destLinkType, artifact, txId, sessionId, presentationType, permanentUrl);
      String linkText = getLinkText(destLinkType, artifact);
      return String.format(linkFormat, linkId, linkText);
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

   private String getLinkId(LinkType destLinkType, ArtifactReadable artifact, TransactionId tx, String sessionId, PresentationType presentationType, String permanentUrl) throws OseeCoreException {
      String toReturn;
      if (destLinkType == LinkType.OSEE_SERVER_LINK) {
         toReturn = escapeXml(getOseeLink(artifact, presentationType, sessionId, permanentUrl));
      } else {
         toReturn = artifact.getGuid();
      }
      return toReturn;
   }

   private String getOseeLink(ArtifactReadable artifact, PresentationType presentationType, String sessionId, String permanentUrl) {
      Map<String, String> parameters = new HashMap<>();
      parameters.put("sessionId", sessionId);
      parameters.put("context", "osee/loopback");
      parameters.put("guid", artifact.getGuid());
      parameters.put("branchUuid", artifact.getBranch().getIdString());
      parameters.put("isDeleted", String.valueOf(artifact.isDeleted()));

      if (artifact.isHistorical() && presentationType != PresentationType.DIFF && presentationType != PresentationType.F5_DIFF) {
         parameters.put("transactionId", String.valueOf(artifact.getTransaction()));
      }

      parameters.put("cmd", "open.artifact");
      URL url = null;

      try {
         String urlString =
            HttpUrlBuilder.createURL(permanentUrl, OseeServerContext.CLIENT_LOOPBACK_CONTEXT, parameters);
         url = new URL(urlString);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }

      return url.toString();
   }

   private String getLinkText(LinkType linkType, ArtifactReadable artifact) {
      StringBuilder builder = new StringBuilder();
      if (isParagraphRequired(linkType)) {
         builder.append(artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "Undefined"));
      }
      if (isArtifactNameRequired(linkType)) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append(artifact.getSoleAttributeValue(CoreAttributeTypes.Name, DeletionFlag.INCLUDE_DELETED, ""));
      }
      if (artifact.isDeleted()) {
         builder.append(" (DELETED)");
      }
      return escapeXml(builder.toString());
   }

   private boolean isParagraphRequired(LinkType linkType) {
      return linkType == LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER || linkType == LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME;
   }

   private boolean isArtifactNameRequired(LinkType linkType) {
      return linkType == LinkType.OSEE_SERVER_LINK || linkType == LinkType.INTERNAL_DOC_REFERENCE_USE_NAME || linkType == LinkType.INTERNAL_DOC_REFERENCE_USE_PARAGRAPH_NUMBER_AND_NAME;
   }

   private String escapeXml(String source) {
      return Xml.escape(source).toString();
   }
}
