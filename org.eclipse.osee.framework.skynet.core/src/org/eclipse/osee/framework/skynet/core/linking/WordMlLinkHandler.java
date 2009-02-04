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

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactURL;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * This class converts between OSEE hyperlink markers into wordML style links. <br/><br/> <b>Example:</b>
 * 
 * <pre>
 * LinkType linkType = LinkType.OSEE_SERVER_LINK;
 * 
 * String original = ... //Doc containing osee link markers
 * 
 * // Substitue OSEE link markers with wordML style hyperlinks requesting content to the OSEE application server
 * String linkedDoc = WordMlLinkHandler.link(linkType, original);
 * 
 * // Substitue wordML style hyperlinks with OSEE link markers
 * String original = WordMlLinkHandler.unLink(linkType, linkedDoc);
 * </pre>
 * 
 * <b>Link types handled</b> <br/><br/>
 * <ol>
 * <li><b>OSEE link:</b> This is a branch neutral marker placed in the wordML document.
 * 
 * <pre>
 *    OSEE_LINK([artifact_guid])
 * </pre>
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

   private static final Matcher LEGACY_LINK_MATCHER =
         Pattern.compile("(.+?)Define\\?guid=(.*)", Pattern.DOTALL).matcher("");

   private static final Matcher OSEE_LINK_PATTERN = Pattern.compile("OSEE_LINK\\((.*?)\\)", Pattern.DOTALL).matcher("");
   private static final Matcher WORDML_LINK =
         Pattern.compile(
               "<w:hlink\\s+w:dest=\"(.*?)\"\\s*>\\s*<w:r\\s*>\\s*<w:rPr\\s*>\\s*<w:rStyle\\s+w:val=\"Hyperlink\"\\s*/>\\s*</w:rPr\\s*>\\s*<w:t\\s*>(.*?)</w:t\\s*>\\s*</w:r\\s*>\\s*</w:hlink\\s*>",
               Pattern.DOTALL).matcher("");
   private static final String OSEE_LINK_FORMAT = "OSEE_LINK(%s)";
   private static final String WORDML_LINK_FORMAT =
         "<w:hlink w:dest=\"%s\"><w:r><w:rPr><w:rStyle w:val=\"Hyperlink\"/></w:rPr><w:t>%s</w:t></w:r></w:hlink>";

   private static final Matcher WORDML_GUID_MATCHER = Pattern.compile("guid=(.*?)&").matcher("");
   private static final Matcher WORDML_BRANCHID_MATCHER = Pattern.compile("branchId=(.*?)&?").matcher("");

   private WordMlLinkHandler() {
   }

   /**
    * It is assumed that an unlink call will be made after a link call. Therefore we expect the input to have links that
    * are recognized by this handler as identified by the sourceLinkType
    * 
    * @param sourceLinkType
    * @param original input
    * @return processed input
    */
   public static String unlink(LinkType sourceLinkType, String original) throws OseeCoreException {
      Map<Integer, HashCollection<String, Match>> matchMap = new HashMap<Integer, HashCollection<String, Match>>();
      String modified = original;

      WORDML_LINK.reset(original);
      while (WORDML_LINK.find()) {
         String link = WORDML_LINK.group(1);
         if (Strings.isValid(link)) {
            ObjectPair<String, Integer> idPair = getGuidFromWordMlLink(link);
            if (idPair != null) {
               HashCollection<String, Match> guidMatch = matchMap.get(idPair.object2);
               if (guidMatch == null) {
                  guidMatch = new HashCollection<String, Match>();
                  matchMap.put(idPair.object2, guidMatch);
               }
               guidMatch.put(idPair.object1, new Match(WORDML_LINK.start(), WORDML_LINK.end()));
            }
         }
      }

      if (!matchMap.isEmpty()) {
         ChangeSet changeSet = new ChangeSet(original);
         for (Integer branchId : matchMap.keySet()) {
            HashCollection<String, Match> guidMatches = matchMap.get(branchId);
            Branch branch = BranchManager.getBranch(branchId);

            List<Artifact> artifacts =
                  ArtifactQuery.getArtifactsFromIds(new ArrayList<String>(guidMatches.keySet()), branch, true);
            if (guidMatches.keySet().size() != artifacts.size()) {
               Set<String> artGuids = new HashSet<String>();
               for (Artifact artifact : artifacts) {
                  artGuids.add(artifact.getGuid());
               }
               List<String> itemsNotFound = Collections.setComplement(guidMatches.keySet(), artGuids);
               throw new OseeStateException(String.format("Artifact(s) with guid [%s] not found on branch [%s].",
                     itemsNotFound, branch));
            } else {
               for (Artifact artifact : artifacts) {
                  for (Match match : guidMatches.getValues(artifact.getGuid())) {
                     changeSet.replace(match.start, match.end, String.format(OSEE_LINK_FORMAT, artifact.getGuid()));
                  }
               }
            }
         }
         modified = changeSet.applyChangesToSelf().toString();
      }
      System.out.println(modified);
      return modified;
   }

   private static ObjectPair<String, Integer> getGuidFromWordMlLink(String link) throws OseeWrappedException {
      ObjectPair<String, Integer> toReturn = null;
      String guidToReturn = null;
      WORDML_GUID_MATCHER.reset(link);
      if (WORDML_GUID_MATCHER.find()) {
         String guid = WORDML_GUID_MATCHER.group(1);
         if (Strings.isValid(guid)) {
            if (!GUID.isValid(guid)) {
               try {
                  guid = URLDecoder.decode(guid, "UTF-8");
                  if (GUID.isValid(guid)) {
                     guidToReturn = guid;
                  }
               } catch (Exception ex) {
                  throw new OseeWrappedException(ex);
               }
            } else {
               guidToReturn = guid;
            }
         }
      }

      if (guidToReturn != null) {
         WORDML_BRANCHID_MATCHER.reset(link);
         if (WORDML_BRANCHID_MATCHER.find()) {
            String branchIdStr = WORDML_BRANCHID_MATCHER.group(1);
            if (Strings.isValid(branchIdStr)) {
               int branchId = -1;
               try {
                  branchId = Integer.parseInt(branchIdStr);
               } catch (Exception ex) {
                  throw new OseeWrappedException(ex);
               }
               if (branchId > -1) {
                  toReturn = new ObjectPair<String, Integer>(guidToReturn, branchId);
               }
            }
         }
      }
      return toReturn;
   }

   public static String link(LinkType destLinkType, Branch branch, String original) throws OseeCoreException {
      String modified = original;
      HashCollection<String, Match> matchMap = new HashCollection<String, Match>();

      // Detect legacy links
      WORDML_LINK.reset(original);
      while (WORDML_LINK.find()) {
         String link = WORDML_LINK.group(1);
         if (Strings.isValid(link)) {
            LEGACY_LINK_MATCHER.reset(link);
            if (LEGACY_LINK_MATCHER.find()) {
               String guid = LEGACY_LINK_MATCHER.group(2);
               if (Strings.isValid(guid)) {
                  if (!GUID.isValid(guid)) {
                     try {
                        guid = URLDecoder.decode(guid, "UTF-8");
                        if (GUID.isValid(guid)) {
                           matchMap.put(guid, new Match(WORDML_LINK.start(), WORDML_LINK.end()));
                        }
                     } catch (Exception ex) {
                        throw new OseeWrappedException(ex);
                     }
                  } else {
                     matchMap.put(guid, new Match(WORDML_LINK.start(), WORDML_LINK.end()));
                  }
               }
            }
         }
      }

      // Detect new style link marker
      OSEE_LINK_PATTERN.reset(original);
      while (OSEE_LINK_PATTERN.find()) {
         String guid = OSEE_LINK_PATTERN.group(1);
         if (Strings.isValid(guid)) {
            matchMap.put(guid, new Match(OSEE_LINK_PATTERN.start(), OSEE_LINK_PATTERN.end()));
         }
      }

      if (!matchMap.isEmpty()) {
         ChangeSet changeSet = new ChangeSet(original);
         List<Artifact> artifacts =
               ArtifactQuery.getArtifactsFromIds(new ArrayList<String>(matchMap.keySet()), branch, true);
         for (Artifact artifact : artifacts) {
            for (Match match : matchMap.getValues(artifact.getGuid())) {
               changeSet.replace(match.start, match.end, getWordMlLink(destLinkType, artifact, branch));
            }
         }
         if (matchMap.keySet().size() != artifacts.size()) {
            Set<String> artGuids = new HashSet<String>();
            for (Artifact artifact : artifacts) {
               artGuids.add(artifact.getGuid());
            }
            List<String> unknownGuids = Collections.setComplement(matchMap.keySet(), artGuids);
            for (String guid : unknownGuids) {
               for (Match match : matchMap.getValues(guid)) {
                  String guidBranch = String.format("guid=%s&branchId=%s", guid, branch.getBranchId());
                  String link =
                        String.format(WORDML_LINK_FORMAT, guidBranch, String.format("Link Unknown: [%s]", guidBranch));
                  changeSet.replace(match.start, match.end, link);
               }
            }
         }
         modified = changeSet.applyChangesToSelf().toString();
      }
      return modified;
   }

   private static String getWordMlLink(LinkType destLinkType, Artifact artifact, Branch branch) throws OseeCoreException {
      String toReturn = "";
      URL url = null;
      switch (destLinkType) {
         case OSEE_SERVER_LINK:
            url = ArtifactURL.getOpenInOseeLink(artifact);
            toReturn =
                  String.format(WORDML_LINK_FORMAT, toWordMlHyperLink(url),
                        artifact.getDescriptiveName() + (artifact.isDeleted() ? " (DELETED)" : ""));
            break;
         default:
            throw new OseeArgumentException(String.format("Unsupported link type [%s]", destLinkType));
      }
      return toReturn;
   }

   private static String toWordMlHyperLink(URL url) {
      return url.toString().replaceAll("&", "&amp;");
   }

   private static class Match {
      int start;
      int end;

      public Match(int start, int end) {
         super();
         this.end = end;
         this.start = start;
      }
   }
}
