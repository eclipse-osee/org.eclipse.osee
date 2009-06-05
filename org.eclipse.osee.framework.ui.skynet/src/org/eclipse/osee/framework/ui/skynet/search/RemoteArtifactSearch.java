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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactMatch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactXmlQueryResultParser.MatchLocation;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.search.page.AttributeLineElement;
import org.eclipse.osee.framework.ui.skynet.search.page.AttributeMatch;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * @author Roberto E. Escobar
 */
final class RemoteArtifactSearch extends AbstractArtifactSearchQuery {
   private final String queryString;
   private final String[] attributeTypeNames;
   private final boolean includeDeleted;
   private final boolean matchWordOrder;
   private final Branch branch;
   private final boolean findAllMatchLocations;
   private final boolean isCaseSensitive;

   RemoteArtifactSearch(String queryString, Branch branch, boolean includeDeleted, boolean matchWordOrder, boolean findAllMatchLocations, boolean isCaseSensitive, String... attributeTypeNames) {
      this.branch = branch;
      this.includeDeleted = includeDeleted;
      this.attributeTypeNames = attributeTypeNames;
      this.queryString = queryString;
      this.matchWordOrder = matchWordOrder;
      this.findAllMatchLocations = findAllMatchLocations;
      this.isCaseSensitive = isCaseSensitive;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getCriteriaLabel()
    */
   @Override
   public String getCriteriaLabel() {
      List<String> optionsList = new ArrayList<String>();
      if (includeDeleted) {
         optionsList.add("Include Deleted");
      }

      if (matchWordOrder) {
         optionsList.add("Match Word Order");
         if (findAllMatchLocations) {
            optionsList.add("All Matches");
         } else {
            optionsList.add("1st Match Only");
         }
      }

      if (isCaseSensitive) {
         optionsList.add("Case Sensitive");
      }

      if (attributeTypeNames != null && attributeTypeNames.length > 0) {
         optionsList.add(String.format("Attribute Type Filter:%s", Arrays.deepToString(attributeTypeNames)));
      }

      String options = String.format(" - Options:[%s]", StringFormat.listToValueSeparatedString(optionsList, ", "));
      return String.format("%s%s", queryString, optionsList.size() > 0 ? options : "");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
    */
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
         List<ArtifactMatch> matches =
               ArtifactQuery.getArtifactMatchesFromAttributeWithKeywords(branch, queryString, matchWordOrder,
                     includeDeleted, findAllMatchLocations, isCaseSensitive, attributeTypeNames);

         endOfloadTime = System.currentTimeMillis();

         startCollectTime = System.currentTimeMillis();
         ResultCollector resultCollector = new ResultCollector(aResult);

         for (ArtifactMatch artifactMatch : matches) {
            resultCollector.beginReporting();
            if (artifactMatch.hasMatchData()) {
               try {
                  Artifact artifact = artifactMatch.getArtifact();
                  HashCollection<Attribute<?>, MatchLocation> matchData = artifactMatch.getMatchData();
                  for (Attribute<?> attribute : matchData.keySet()) {
                     for (MatchLocation matchLocation : matchData.getValues(attribute)) {
                        resultCollector.acceptMatchData(artifact, attribute, matchLocation);
                        lineMatches++;
                     }
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, String.format(
                        "Error processing attribute line matches for [%s]", artifactMatch.getArtifact()), ex);
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
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, String.format(
               "Quick Search: [%s] artifacts with [%s] location matches loaded in [%s secs] collected in [%s]",
               artifactCount, lineMatches, (endOfloadTime - startTime) / 1000.0, Lib.getElapseString(startCollectTime)));
      }
      setIsDoneRunning(true);
      return new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, "OK", null);
   }

   private final class ResultCollector {

      private final AbstractTextSearchResult fResult;
      private ArrayList<Match> fCachedMatches;

      private ResultCollector(AbstractTextSearchResult result) {
         fResult = result;
      }

      public boolean acceptArtifactMatch(ArtifactMatch artifactMatch) {
         fResult.addMatch(new Match(artifactMatch.getArtifact(), 1, 2));
         flushMatches();
         return true;
      }

      public boolean acceptMatchData(Artifact artifact, Attribute<?> attribute, MatchLocation matchLocation) {
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

      private AttributeLineElement getLineElement(int offset, int matchEnd, Artifact artifact, Attribute<?> attribute) {
         int lineNumber = 1;
         int lineStart = 0;
         if (!fCachedMatches.isEmpty()) {
            AttributeMatch last = (AttributeMatch) fCachedMatches.get(fCachedMatches.size() - 1);
            AttributeLineElement lineElement = last.getLineElement();
            if (lineElement.contains(offset) && lineElement.getAttribute().equals(attribute)) {
               return lineElement;
            }
            lineStart = lineElement.getOffset() + lineElement.getLength();
            lineNumber = lineElement.getLine() + 1;
         }
         if (offset < lineStart) {
            return null; // offset before the last line
         }

         int i = lineStart;
         String content = getContentFromAttribute(attribute);
         int contentLength = content.length();
         int charCount = 0;
         while (i < contentLength) {
            char ch = content.charAt(i++);
            charCount++;
            if (charCount >= 40 && Character.isWhitespace(ch) && matchEnd < i) {
               ch = '\n';
               charCount = 0;
            }
            if (ch == '\n' || ch == '\r') {
               if (ch == '\r' && i < contentLength && content.charAt(i) == '\n') {
                  i++;
               }
               if (offset < i) {
                  String lineContent = getContents(content, lineStart, i); // include line delimiter
                  return new AttributeLineElement(artifact, attribute, lineNumber, lineStart, lineContent);
               }
               lineNumber++;
               lineStart = i;
            }
         }
         if (offset < i) {
            String lineContent = getContents(attribute, lineStart, i);
            return new AttributeLineElement(artifact, attribute, lineNumber, lineStart, lineContent);
         }
         return null; // offset outside of range
      }

      public String getContents(Attribute<?> attribute, int start, int end) {
         String contents = getContentFromAttribute(attribute);
         if (Strings.isValid(contents)) {
            contents = getContents(contents, start, end);
         }
         return contents;
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
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
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
         fCachedMatches = new ArrayList<Match>();
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
