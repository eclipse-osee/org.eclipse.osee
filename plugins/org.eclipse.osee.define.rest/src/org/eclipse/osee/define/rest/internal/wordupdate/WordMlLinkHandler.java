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
package org.eclipse.osee.define.rest.internal.wordupdate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.define.api.OseeLinkBuilder;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * This class converts between OSEE hyperlink markers into wordML style links. <br/>
 * <br/>
 * <b>Example:</b>
 *
 * <pre>
 * LinkType linkType = LinkType.OSEE_SERVER_LINK;
 *
 * ArtifactReadable source = ... // ArtifactReadable that contains original
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

   private static final Pattern OSEE_LINK_PATTERN = Pattern.compile("OSEE_LINK\\((.*?)\\)", Pattern.DOTALL);
   private static final Pattern WORDML_LINK =
      Pattern.compile("<w:hlink\\s+w:dest=\"(.*?)\"[^>]*?(/>|>.*?</w:hlink\\s*>)", Pattern.DOTALL);
   private static final Pattern HYPERLINK_PATTERN = Pattern.compile(
      "<w:r[^>]*><w:instrText>\\s*HYPERLINK\\s+\"(.+?)\"\\s*</w:instrText></w:r>(.*?</w:t>.+?</w:fldChar></w:r>)?",
      Pattern.DOTALL);
   private static final Pattern IS_GUID = Pattern.compile("\\D");

   public static final String WORDML_KEY = "wordml";
   public static final String UNKNOWNGUIDS_KEY = "unknownguids";

   private static final OseeLinkBuilder linkBuilder = new OseeLinkBuilder();

   private static LinkType checkLinkType(LinkType value) {
      return value != null ? value : LinkType.OSEE_SERVER_LINK;
   }

   /**
    * Remove WordML hyperlinks and replace with OSEE_LINK marker. It is assumed that an unlink call will be made after a
    * link call. Therefore we expect the input to have links that are recognized by this handler as identified by the
    * sourceLinkType.
    *
    * @param content input
    * @return processed input
    */
   public static String unlink(QueryFactory queryFactory, LinkType sourceLinkType, ArtifactReadable source, String content) {
      LinkType linkType = checkLinkType(sourceLinkType);
      String modified = content;
      HashCollection<String, MatchRange> matchMap = parseOseeWordMLLinks(content);
      if (!matchMap.isEmpty()) {
         modified = modifiedContent(queryFactory, linkType, source, content, matchMap, true, TransactionId.SENTINEL,
            null, null, null);
      }
      return modified;
   }

   /**
    * Replace OSEE_LINK marker or Legacy hyper-links with WordML hyperlinks.
    *
    * @param content input
    * @return processed input
    */
   public static String link(QueryFactory queryFactory, LinkType destLinkType, ArtifactReadable source, String content, TransactionId txId, Set<String> unknownGuids, PresentationType presentationType, String permanentUrl) {
      LinkType linkType = checkLinkType(destLinkType);
      String modified = content;

      // Detect legacy links
      HashCollection<String, MatchRange> matchMap = parseOseeWordMLLinks(content);

      // Detect new style link marker
      Matcher oseeMatcher = OSEE_LINK_PATTERN.matcher(content);
      while (oseeMatcher.find()) {
         String guid = oseeMatcher.group(1);
         if (Strings.isValid(guid)) {
            matchMap.put(guid, new MatchRange(oseeMatcher.start(), oseeMatcher.end()));
         }
      }

      if (!matchMap.isEmpty()) {
         modified = modifiedContent(queryFactory, linkType, source, content, matchMap, false, txId, unknownGuids,
            presentationType, permanentUrl);
      }

      if (linkType != LinkType.OSEE_SERVER_LINK) {
         // Add a bookmark to the start of the content so internal links can link later
         modified = linkBuilder.getWordMlBookmark(source) + modified;
      }

      return modified;
   }

   /**
    * Find WordML links locations in content grouped by GUID
    *
    * @return locations where WordMlLinks were found grouped by GUID
    */
   public static HashCollection<String, MatchRange> parseOseeWordMLLinks(String content) {
      HashCollection<String, MatchRange> matchMap = new HashCollection<>();

      OseeLinkParser linkParser = new OseeLinkParser();
      Matcher wordMlMatcher = WORDML_LINK.matcher(content);
      while (wordMlMatcher.find()) {
         String link = wordMlMatcher.group(1);
         if (Strings.isValid(link)) {
            linkParser.parse(link);
            String guid = linkParser.getGuid();
            if (Strings.isValid(guid)) {
               matchMap.put(guid, new MatchRange(wordMlMatcher.start(), wordMlMatcher.end()));
            }
         }
      }

      Matcher hyperlinkMatcher = HYPERLINK_PATTERN.matcher(content);
      while (hyperlinkMatcher.find()) {
         String link = hyperlinkMatcher.group(1);
         if (Strings.isValid(link)) {
            linkParser.parse(link);
            String guid = linkParser.getGuid();
            if (Strings.isValid(guid)) {
               matchMap.put(guid, new MatchRange(hyperlinkMatcher.start(), hyperlinkMatcher.end()));
            }
         }
      }

      return matchMap;
   }

   private static List<ArtifactReadable> findArtifacts(QueryFactory queryFactory, BranchId branch, List<String> guidsFromLinks, TransactionId txId) {
      QueryBuilder query;
      Matcher matcher = IS_GUID.matcher(guidsFromLinks.get(0));
      if (matcher.find()) {
         query = queryFactory.fromBranch(branch).andGuids(
            guidsFromLinks).includeDeletedArtifacts().includeDeletedAttributes();
      } else {
         List<ArtifactId> artIdsFromLinks = new LinkedList<>();
         for (String link : guidsFromLinks) {
            artIdsFromLinks.add(ArtifactId.valueOf(link));
         }
         query = queryFactory.fromBranch(branch).andIds(
            artIdsFromLinks).includeDeletedArtifacts().includeDeletedAttributes();
      }

      if (txId.isValid()) {
         query.fromTransaction(txId);
      }
      return query.getResults().getList();
   }

   private static List<String> getGuidsNotFound(List<String> guidsFromLinks, List<ArtifactReadable> artifactsFound) {
      Set<String> artGuids = new HashSet<>();
      for (ArtifactReadable artifact : artifactsFound) {
         artGuids.add(artifact.getGuid());
      }
      return Collections.setComplement(guidsFromLinks, artGuids);
   }

   private static String modifiedContent(QueryFactory queryFactory, LinkType destLinkType, ArtifactReadable source, String original, HashCollection<String, MatchRange> matchMap, boolean isUnlinking, TransactionId txId, Set<String> unknown, PresentationType presentationType, String permanentUrl) {
      BranchId branch = source.getBranch();
      ChangeSet changeSet = new ChangeSet(original);
      List<ArtifactReadable> artifactsFromSearch = null;
      List<String> guidsFromLinks = new ArrayList<>(matchMap.keySet());

      artifactsFromSearch = findArtifacts(queryFactory, branch, guidsFromLinks, txId);
      boolean isMergeBranch = queryFactory.branchQuery().andId(branch).andIsOfType(BranchType.MERGE).exists();
      if (guidsFromLinks.size() != artifactsFromSearch.size() && isMergeBranch) {
         Branch branchReadable = queryFactory.branchQuery().andId(branch).getResults().getExactlyOne();
         List<String> unknownGuids = getGuidsNotFound(guidsFromLinks, artifactsFromSearch);

         List<ArtifactReadable> union = new ArrayList<>();
         union.addAll(findArtifacts(queryFactory, branchReadable.getParentBranch(), unknownGuids, txId));
         union.addAll(artifactsFromSearch);
         artifactsFromSearch = union;
      }

      if (guidsFromLinks.size() != artifactsFromSearch.size()) {
         List<String> unknownGuids = getGuidsNotFound(guidsFromLinks, artifactsFromSearch);
         if (isUnlinking) {
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
      Matcher matcher = IS_GUID.matcher(matchMap.keySet().iterator().next());
      if (matcher.find()) {
         for (ArtifactReadable artifact : artifactsFromSearch) {
            for (MatchRange match : matchMap.getValues(artifact.getGuid())) {
               String replaceWith = null;
               if (isUnlinking) {
                  replaceWith = linkBuilder.getOseeLinkMarker(artifact.getGuid());
               } else {
                  replaceWith = linkBuilder.getWordMlLink(destLinkType, artifact, txId, presentationType, permanentUrl);
               }
               changeSet.replace(match.start(), match.end(), replaceWith);
            }
         }
      } else {
         for (ArtifactReadable artifact : artifactsFromSearch) {
            for (MatchRange match : matchMap.getValues(artifact.getIdString())) {
               String replaceWith = null;
               if (isUnlinking) {
                  replaceWith = linkBuilder.getOseeLinkMarker(artifact.getIdString());
               } else {
                  replaceWith = linkBuilder.getWordMlLink(destLinkType, artifact, txId, presentationType, permanentUrl);
               }
               changeSet.replace(match.start(), match.end(), replaceWith);
            }
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
