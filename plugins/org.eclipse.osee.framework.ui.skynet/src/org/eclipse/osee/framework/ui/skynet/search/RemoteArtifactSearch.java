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
package org.eclipse.osee.framework.ui.skynet.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactMatch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.SearchOptions;
import org.eclipse.osee.framework.skynet.core.artifact.search.SearchRequest;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.search.page.AttributeLineElement;
import org.eclipse.osee.framework.ui.skynet.search.page.AttributeMatch;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * @author Roberto E. Escobar
 */
public final class RemoteArtifactSearch extends AbstractArtifactSearchQuery {
   private final SearchRequest searchRequest;

   public RemoteArtifactSearch(SearchRequest searchRequest) {
      this.searchRequest = searchRequest;
   }

   @Override
   public String getCriteriaLabel() {
      SearchOptions options = searchRequest.getOptions();
      List<String> optionsList = new ArrayList<>();
      if (options.getDeletionFlag().areDeletedAllowed()) {
         optionsList.add("Include Deleted");
      }

      if (options.isCaseSensitive()) {
         optionsList.add("Case Sensitive");
      }

      if (!options.isSearchAll()) {
         if (options.getAttributeTypeFilter().size() <= 5) {
            optionsList.add(String.format("Attribute Type Filter:%s", options.getAttributeTypeFilter()));
         } else {
            optionsList.add(String.format("Attribute Type Filter: %d types", options.getAttributeTypeFilter().size()));
         }
      }

      String optionsLabel = String.format(" - Options:[%s]",
         org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ", optionsList));
      return String.format("%s%s", searchRequest.getRawSearch(), optionsList.size() > 0 ? optionsLabel : "");
   }

   @Override
   public IStatus run(IProgressMonitor pm) {
      setIsDoneRunning(false);
      aResult.removeAll();
      long startTime = System.currentTimeMillis();
      int artifactCount = 0;
      long startCollectTime = startTime;
      long endOfloadTime = startTime;
      int lineMatches = 0;
      try {
         Iterable<ArtifactMatch> matches = ArtifactQuery.getArtifactMatchesFromAttributeKeywords(searchRequest);

         endOfloadTime = System.currentTimeMillis();

         startCollectTime = System.currentTimeMillis();
         ResultCollector resultCollector = new ResultCollector(aResult);

         for (ArtifactMatch artifactMatch : matches) {
            resultCollector.beginReporting();
            if (artifactMatch.hasMatchData()) {
               try {
                  Artifact artifact = artifactMatch.getArtifact();
                  HashCollection<AttributeId, MatchLocation> matchData = artifactMatch.getMatchData();
                  if (!matchData.isEmpty()) {
                     for (AttributeId attribute : matchData.keySet()) {
                        for (MatchLocation matchLocation : matchData.getValues(attribute)) {
                           resultCollector.acceptMatchData(artifact, attribute, matchLocation);
                           lineMatches++;
                        }
                     }
                  } else {
                     resultCollector.acceptArtifactMatch(artifactMatch);
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error processing attribute line matches for [%s]",
                     artifactMatch.getArtifact());
                  resultCollector.acceptArtifactMatch(artifactMatch);
               }
            } else {
               resultCollector.acceptArtifactMatch(artifactMatch);
            }
            resultCollector.endReporting();
            artifactCount++;
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      } finally {
         OseeLog.logf(Activator.class, Level.INFO,
            "Quick Search: [%s] artifacts with [%s] location matches loaded in [%s secs] collected in [%s]",
            artifactCount, lineMatches, (endOfloadTime - startTime) / 1000.0, Lib.getElapseString(startCollectTime));
      }
      setIsDoneRunning(true);
      return new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, "OK", null);
   }

   private final class ResultCollector {

      private final AbstractTextSearchResult fResult;
      private ArrayList<Match> fCachedMatches;
      private final Map<AttributeId, String> attrContent = new HashMap<>();
      private static final int HALF_LINE_CHAR_COUNT = 40;

      private ResultCollector(AbstractTextSearchResult result) {
         fResult = result;
      }

      public boolean acceptArtifactMatch(ArtifactMatch artifactMatch) {
         fResult.addMatch(new Match(artifactMatch.getArtifact(), 1, 2));
         flushMatches();
         return true;
      }

      public boolean acceptMatchData(Artifact artifact, AttributeId attribute, MatchLocation matchLocation) {
         int matchOffset = matchLocation.getStartPosition() - 1;
         if (matchOffset < 0) {
            matchOffset = 0;
         }
         int matchEnd = matchLocation.getEndPosition();
         int matchLength = matchEnd - matchOffset;

         AttributeLineElement lineElement = getLineElement(matchOffset, matchEnd, artifact, attribute);
         if (lineElement != null) {
            AttributeMatch match = new AttributeMatch(artifact, matchOffset, matchLength, lineElement);
            fCachedMatches.add(match);
         }
         return true;
      }

      private AttributeLineElement getLineElement(int offset, int matchEnd, Artifact artifact, AttributeId attrId) {
         int lineNumber = 1;
         int lineStart = 0;
         String content = null;
         if (attrContent.containsKey(attrId)) {
            content = attrContent.get(attrId);
         } else {
            content = getContentFromAttribute(artifact.getAttributeById(attrId.getId(), false));
            attrContent.put(attrId, content);
         }
         if (!fCachedMatches.isEmpty()) {
            AttributeMatch last = (AttributeMatch) fCachedMatches.get(fCachedMatches.size() - 1);
            AttributeLineElement lineElement = last.getLineElement();
            if (lineElement.contains(offset) && lineElement.getAttribute() == attrId) {
               return lineElement;
            }
            if (lineElement.getAttribute() == attrId) {
               lineStart = lineElement.getOffset() + lineElement.getLength();
               lineNumber = lineElement.getLine();
            }
         }
         if (offset < lineStart) {
            return null; // offset before the last line
         }

         int newLineStart = offset;
         int contentLength = content.length();
         int charCount = 0;
         int i = offset - 1;
         boolean markLineStart = true;
         while (i >= lineStart) {
            char ch = content.charAt(i--);
            if (ch == '\r') {
               continue;
            }
            charCount++;
            if (ch == '\n') {
               lineNumber++;
            }
            if (charCount >= HALF_LINE_CHAR_COUNT && Character.isWhitespace(ch) || ch == '\n') {
               if (markLineStart) {
                  newLineStart = i + 1;
                  markLineStart = false;
                  if (ch == '\n') {
                     charCount = 0;
                  }
               }
            }
         }
         i++;
         if (markLineStart) {
            newLineStart = i;
         }

         i = matchEnd;
         charCount = 0;
         while (i < contentLength) {
            char ch = content.charAt(i++);
            charCount++;
            if (charCount >= HALF_LINE_CHAR_COUNT && Character.isWhitespace(ch)) {
               ch = '\n';
               charCount = 0;
            }
            if (ch == '\n' || ch == '\r') {
               break;
            }
         }
         if (offset < i) {
            String lineContent = getContents(content, newLineStart, i);
            return new AttributeLineElement(artifact, attrId, lineNumber, newLineStart, lineContent);
         }
         return null; // offset outside of range
      }

      private String getContentFromAttribute(Attribute<?> attribute) {
         try {
            Object value = attribute.getValue();
            if (!attribute.getAttributeType().getTaggerId().contains("Default")) {
               return Lib.inputStreamToString(new XmlTextInputStream((String) value));
            } else if (value instanceof String) {
               return (String) value;
            } else {
               return attribute.getDisplayableString();
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         return "";
      }

      private String getContents(String content, int start, int end) {
         StringBuffer buf = new StringBuffer();
         for (int i = start; i < end; i++) {
            char ch = content.charAt(i);
            if (Character.isWhitespace(ch) || Character.isISOControl(ch)) {
               buf.append(' ');
            } else {
               buf.append(ch);
            }
         }
         return buf.toString();
      }

      public void beginReporting() {
         fCachedMatches = new ArrayList<>();
      }

      public void endReporting() {
         flushMatches();
         fCachedMatches = null;
      }

      private void flushMatches() {
         if (fCachedMatches != null && !fCachedMatches.isEmpty()) {
            fResult.addMatches(fCachedMatches.toArray(new Match[fCachedMatches.size()]));
            fCachedMatches.clear();
         }
      }
   }
}
