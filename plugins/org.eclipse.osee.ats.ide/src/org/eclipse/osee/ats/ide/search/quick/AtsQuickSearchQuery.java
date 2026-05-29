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

package org.eclipse.osee.ats.ide.search.quick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
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
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery;
import org.eclipse.osee.framework.ui.skynet.search.page.AttributeLineElement;
import org.eclipse.osee.framework.ui.skynet.search.page.AttributeMatch;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * ATS Quick Search Query that integrates with Eclipse Search View. Uses
 * ArtifactQuery.getArtifactMatchesFromAttributeKeywords to get match locations and filters to ATS Workflows only.
 *
 * @author Donald G. Dunne
 */
public class AtsQuickSearchQuery extends AbstractArtifactSearchQuery {

   private final AtsQuickSearchData data;
   private AtsSearchResult atsResult;

   public AtsQuickSearchQuery(AtsQuickSearchData data) {
      this.data = data;
   }

   @Override
   public ISearchResult getSearchResult() {
      if (atsResult == null) {
         atsResult = new AtsSearchResult(this);
         aResult = atsResult;
      }
      return atsResult;
   }

   @Override
   public String getCriteriaLabel() {
      StringBuilder sb = new StringBuilder();
      sb.append(data.getSearchStr());
      sb.append(" - ATS Workflows");
      if (data.isIncludeCompleteCancelled()) {
         sb.append(" (Include Completed/Cancelled)");
      }
      if (data.isIncludeDeleted()) {
         sb.append(" (Include Deleted)");
      }
      if (data.isCaseSensitive()) {
         sb.append(" (Case Sensitive)");
      }
      return sb.toString();
   }

   @Override
   public String getLabel() {
      return "ATS Quick Search";
   }

   @Override
   public IStatus run(IProgressMonitor pm) {
      getSearchResult(); // ensure aResult is initialized
      setIsDoneRunning(false);
      aResult.removeAll();
      long startTime = System.currentTimeMillis();
      int artifactCount = 0;
      long startCollectTime = startTime;
      long endOfloadTime = startTime;
      int lineMatches = 0;
      try {
         SearchOptions options = new SearchOptions();
         options.setDeletedIncluded(
            data.isIncludeDeleted() ? DeletionFlag.INCLUDE_DELETED : DeletionFlag.EXCLUDE_DELETED);
         options.setCaseSensive(data.isCaseSensitive());
         options.setIsSearchAll(true);

         SearchRequest searchRequest =
            new SearchRequest(AtsApiService.get().getAtsBranch(), data.getSearchStr(), options);

         Iterable<ArtifactMatch> matches = ArtifactQuery.getArtifactMatchesFromAttributeKeywords(searchRequest);

         endOfloadTime = System.currentTimeMillis();
         startCollectTime = System.currentTimeMillis();

         ResultCollector resultCollector = new ResultCollector(aResult);

         for (ArtifactMatch artifactMatch : matches) {
            Artifact artifact = artifactMatch.getArtifact();

            // Only include ATS Workflow artifacts
            if (!isValidAtsWorkflow(artifact)) {
               continue;
            }

            resultCollector.beginReporting();
            if (artifactMatch.hasMatchData()) {
               try {
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
                     artifact);
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
            "ATS Quick Search: [%s] artifacts with [%s] location matches loaded in [%s secs] collected in [%s]",
            artifactCount, lineMatches, (endOfloadTime - startTime) / 1000.0, Lib.getElapseString(startCollectTime));
      }
      setIsDoneRunning(true);
      return new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, "OK", null);
   }

   private boolean isValidAtsWorkflow(Artifact art) {
      if (!(art instanceof AbstractWorkflowArtifact)) {
         return false;
      }
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) art;
      if (!data.isIncludeCompleteCancelled() && awa.isCompletedOrCancelled()) {
         return false;
      }
      return true;
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

         try {
            AttributeLineElement lineElement = getLineElement(matchOffset, matchEnd, artifact, attribute);

            if (lineElement != null) {
               AttributeMatch match = new AttributeMatch(artifact, matchOffset, matchLength, lineElement);
               fCachedMatches.add(match);
            }
         } catch (Exception ex) {
            OseeLog.log(getClass(), Level.SEVERE, ex);
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
            if (artifact.hasAttribute(attrId)) {
               content = getContentFromAttribute(artifact.getAttributeById(attrId, false));
               attrContent.put(attrId, content);
            }
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
            return null;
         }

         int newLineStart = offset;
         int contentLength = 0;
         if (content != null) {
            contentLength = content.length();
         }
         int charCount = 0;
         int i = offset - 1;
         boolean markLineStart = true;
         while (i >= lineStart) {
            if (content != null) {
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
         }
         i++;
         if (markLineStart) {
            newLineStart = i;
         }

         i = matchEnd;
         charCount = 0;
         while (i < contentLength) {
            if (content != null) {
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
         }
         if (offset < i) {
            String lineContent = "";
            if (content != null) {
               lineContent = getContents(content, newLineStart, i);
            }
            return new AttributeLineElement(artifact, attrId, lineNumber, newLineStart, lineContent);
         }
         return null;
      }

      private String getContentFromAttribute(Attribute<?> attribute) {
         try {
            Object value = attribute.getValue();
            if (!attribute.getAttributeType().getTaggerType().equals(TaggerTypeToken.PlainTextTagger)) {
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
