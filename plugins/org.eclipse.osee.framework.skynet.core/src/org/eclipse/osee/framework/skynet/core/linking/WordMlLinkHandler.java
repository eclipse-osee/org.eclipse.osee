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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * This class converts between OSEE hyperlink markers into wordML style links. <br/>
 * <br/>
 * <b>Example:</b>
 *
 * <pre>
 * LinkType linkType = LinkType.OSEE_SERVER_LINK;
 *
 * Artifact source = ... // Artifact that contains original
 * String original = ... //Doc containing osee link markers
 *
 * // Substitue OSEE link markers with wordML style hyperlinks requesting content to the OSEE application server
 * String linkedDoc = WordMlLinkHandler.link(linkType, source, original);
 *
 * // Substitue wordML style hyperlinks with OSEE link markers
 * String original = WordMlLinkHandler.unLink(linkType, source, linkedDoc);
 * </pre>
 *
 * <b>Link types handled</b> <br/>
 * <br/>
 * <ol>
 * <li><b>OSEE link:</b> This is a branch neutral marker placed in the wordML document.
 *
 * <pre>
 *    OSEE_LINK([artifact_guid])
 * </pre>
 *
 * <li><b>Legacy style links:</b>
 *
 * <pre>
 * &lt;w:hlink w:dest=&quot;http://[server_address]:[server_port]/Define?guid=&quot;[artifact_guid]&quot;&gt;
 *    &lt;w:r&gt;
 *       &lt;w:rPr&gt;
 *          &lt;w:rStyle w:val=&quot;Hyperlink&quot;/&gt;
 *       &lt;/w:rPr&gt;
 *       &lt;w:t&gt;[artifact_name]&lt;/w:t&gt;
 *    &lt;/w:r&gt;
 * &lt;/w:hlink&gt;
 * </pre>
 *
 * </li>
 * </ol>
 *
 * @author Roberto E. Escobar
 */
public class WordMlLinkHandler {

   private static final Matcher OSEE_LINK_PATTERN = Pattern.compile("OSEE_LINK\\((.*?)\\)", Pattern.DOTALL).matcher("");
   private static final Matcher WORDML_LINK =
      Pattern.compile("<w:hlink\\s+w:dest=\"(.*?)\"[^>]*?(/>|>.*?</w:hlink\\s*>)", Pattern.DOTALL).matcher("");
   private static final Matcher HYPERLINK_PATTERN = Pattern.compile(
      "<w:r[^>]*><w:instrText>\\s*HYPERLINK\\s+\"(.+?)\"\\s*</w:instrText></w:r>([^<]</w:t>.+?</w:fldChar></w:r>)?",
      Pattern.DOTALL).matcher("");

   private static final OseeLinkBuilder linkBuilder = new OseeLinkBuilder();

   private static LinkType checkLinkType(LinkType value) {
      return value != null ? value : LinkType.OSEE_SERVER_LINK;
   }

   /**
    * Remove WordML hyperlinks and replace with OSEE_LINK marker. It is assumed that an unlink call will be made after a
    * link call. Therefore we expect the input to have links that are recognized by this handler as identified by the
    * sourceLinkType.
    *
    * @param source artifact that produced the string content
    * @param content input
    * @return processed input
    */
   public static String unlink(LinkType sourceLinkType, Artifact source, String content)  {
      LinkType linkType = checkLinkType(sourceLinkType);
      String modified = content;
      HashCollection<String, MatchRange> matchMap = parseOseeWordMLLinks(content, new HashCollection<>());
      if (!matchMap.isEmpty()) {
         modified = modifiedContent(linkType, source, content, matchMap, true, null);
      }
      return modified;
   }

   /**
    * Replace OSEE_LINK marker or Legacy hyper-links with WordML hyperlinks.
    *
    * @param destLinkType type of link to produce
    * @param source artifact that produced the string content
    * @param content input
    * @return processed input
    */
   public static String link(LinkType destLinkType, Artifact source, String content, Set<String> unknownGuids)  {
      return link(destLinkType, source, content, unknownGuids, PresentationType.DEFAULT_OPEN);
   }

   public static String link(LinkType destLinkType, Artifact source, String content, Set<String> unknownGuids, PresentationType presentationType)  {
      LinkType linkType = checkLinkType(destLinkType);
      String modified = content;

      HashCollection<String, MatchRange> matchMap = getLinks(content, new HashCollection<>());
      if (!matchMap.isEmpty()) {
         modified = modifiedContent(linkType, source, content, matchMap, false, unknownGuids, presentationType);
         unknownGuids.addAll(matchMap.keySet());
      }
      if (linkType != LinkType.OSEE_SERVER_LINK) {
         // Add a bookmark to the start of the content so internal links can link later
         modified = linkBuilder.getWordMlBookmark(source) + modified;
      }
      return modified;
   }

   public static HashCollection<String, MatchRange> getLinks(String content, HashCollection<String, MatchRange> errorMap) {
      // Detect legacy links
      HashCollection<String, MatchRange> matchMap = parseOseeWordMLLinks(content, errorMap);

      // Detect new style link marker
      OSEE_LINK_PATTERN.reset(content);
      while (OSEE_LINK_PATTERN.find()) {
         String guid = OSEE_LINK_PATTERN.group(1);
         if (Strings.isValid(guid)) {
            matchMap.put(guid, new MatchRange(OSEE_LINK_PATTERN.start(), OSEE_LINK_PATTERN.end()));
         } else {
            errorMap.put(guid, new MatchRange(WORDML_LINK.start(), WORDML_LINK.end()));
         }
      }
      OSEE_LINK_PATTERN.reset();
      return matchMap;
   }

   /**
    * Find WordML links locations in content grouped by GUID
    *
    * @return locations where WordMlLinks were found grouped by GUID
    */
   private static HashCollection<String, MatchRange> parseOseeWordMLLinks(String content, HashCollection<String, MatchRange> errorMap) {
      HashCollection<String, MatchRange> matchMap = new HashCollection<>();
      OseeLinkParser linkParser = new OseeLinkParser();
      WORDML_LINK.reset(content);
      while (WORDML_LINK.find()) {
         String link = WORDML_LINK.group(1);
         if (Strings.isValid(link)) {
            linkParser.parse(link);
            String guid = linkParser.getGuid();
            if (Strings.isValid(guid)) {
               matchMap.put(guid, new MatchRange(WORDML_LINK.start(), WORDML_LINK.end()));
            } else {
               errorMap.put(linkParser.getErrLink(), new MatchRange(WORDML_LINK.start(), WORDML_LINK.end()));
            }
         }
      }
      WORDML_LINK.reset();

      HYPERLINK_PATTERN.reset(content);
      while (HYPERLINK_PATTERN.find()) {
         String link = HYPERLINK_PATTERN.group(1);
         if (Strings.isValid(link)) {
            linkParser.parse(link);
            String guid = linkParser.getGuid();
            if (Strings.isValid(guid)) {
               matchMap.put(guid, new MatchRange(HYPERLINK_PATTERN.start(), HYPERLINK_PATTERN.end()));
            } else {
               errorMap.put(linkParser.getErrLink(),
                  new MatchRange(HYPERLINK_PATTERN.start(), HYPERLINK_PATTERN.end()));
            }
         }
      }
      HYPERLINK_PATTERN.reset();

      return matchMap;
   }

   private static List<Artifact> findArtifacts(TransactionToken transactionId, BranchId branch, boolean isHistorical, List<String> guidsFromLinks)  {
      List<Artifact> artifactsFromSearch;
      if (isHistorical) {
         artifactsFromSearch =
            ArtifactQuery.getHistoricalArtifactListFromIds(guidsFromLinks, transactionId, INCLUDE_DELETED);
      } else {
         artifactsFromSearch = ArtifactQuery.getArtifactListFromIds(guidsFromLinks, branch, INCLUDE_DELETED);
      }
      return artifactsFromSearch;
   }

   private static List<String> getGuidsNotFound(List<String> guidsFromLinks, List<Artifact> artifactsFound) {
      Set<String> artGuids = new HashSet<>();
      for (Artifact artifact : artifactsFound) {
         artGuids.add(artifact.getGuid());
      }
      return Collections.setComplement(guidsFromLinks, artGuids);
   }

   private static String modifiedContent(LinkType destLinkType, Artifact source, String original, HashCollection<String, MatchRange> matchMap, boolean isUnliking, Set<String> unknown)  {
      return modifiedContent(destLinkType, source, original, matchMap, isUnliking, unknown,
         PresentationType.DEFAULT_OPEN);
   }

   private static String modifiedContent(LinkType destLinkType, Artifact source, String original, HashCollection<String, MatchRange> matchMap, boolean isUnliking, Set<String> unknown, PresentationType presentationType)  {
      BranchId branch = source.getBranch();
      ChangeSet changeSet = new ChangeSet(original);
      List<Artifact> artifactsFromSearch = null;
      List<String> guidsFromLinks = new ArrayList<>(matchMap.keySet());

      artifactsFromSearch = findArtifacts(source.getTransaction(), branch, source.isHistorical(), guidsFromLinks);
      if (guidsFromLinks.size() != artifactsFromSearch.size() && BranchManager.getType(branch).isMergeBranch()) {
         BranchId sourceBranch = BranchManager.getParentBranch(branch);
         List<String> unknownGuids = getGuidsNotFound(guidsFromLinks, artifactsFromSearch);

         List<Artifact> union = new ArrayList<>();
         union.addAll(findArtifacts(BranchManager.getSourceTransaction(branch), sourceBranch, source.isHistorical(),
            unknownGuids));
         union.addAll(artifactsFromSearch);
         artifactsFromSearch = union;
      }

      if (guidsFromLinks.size() != artifactsFromSearch.size()) {
         List<String> unknownGuids = getGuidsNotFound(guidsFromLinks, artifactsFromSearch);
         if (isUnliking) {
            // Ignore not found items and replace with osee marker
            for (String guid : unknownGuids) {
               Collection<MatchRange> matches = matchMap.getValues(guid);
               for (MatchRange match : matches) {
                  String replaceWith = linkBuilder.getOseeLinkMarker(guid);
                  changeSet.replace(match.start(), match.end(), replaceWith);
               }
            }
         } else {
            // Items not found
            if (!unknownGuids.isEmpty()) {
               unknown.addAll(unknownGuids);
               for (String guid : unknownGuids) {
                  for (MatchRange match : matchMap.getValues(guid)) {
                     String link = linkBuilder.getUnknownArtifactLink(guid, branch);
                     changeSet.replace(match.start(), match.end(), link);
                  }
               }
            }
         }
      }
      // Items found in branch
      for (Artifact artifact : artifactsFromSearch) {
         for (MatchRange match : matchMap.getValues(artifact.getGuid())) {
            String replaceWith = null;
            if (isUnliking) {
               replaceWith = linkBuilder.getOseeLinkMarker(artifact.getGuid());
            } else {
               replaceWith = linkBuilder.getWordMlLink(destLinkType, artifact, presentationType);
            }
            changeSet.replace(match.start(), match.end(), replaceWith);
         }
      }
      return changeSet.applyChangesToSelf().toString();
   }
   public static final class MatchRange {
      private final int start;
      private final int end;

      public MatchRange(int start, int end) {
         super();
         this.end = end;
         this.start = start;
      }

      public int start() {
         return start;
      }

      public int end() {
         return end;
      }

      @Override
      public String toString() {
         return "{" + start + ", " + end + "}";
      }
   }

}
