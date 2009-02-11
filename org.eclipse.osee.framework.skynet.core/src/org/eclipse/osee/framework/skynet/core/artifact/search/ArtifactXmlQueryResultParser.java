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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.JoinUtility;
import org.eclipse.osee.framework.core.data.JoinUtility.ArtifactJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <pre>
 *     &lt;search&gt;
 *         &lt;match branchId=&quot;15&quot;&gt;
 *           &lt;art artId=&quot;87577&quot;&gt;
 *               &lt;attr gammaId=&quot;292554&quot;&gt;
 *                  &lt;location start=&quot;27036&quot; end=&quot;27045&quot;/&gt;
 *                  &lt;location start=&quot;28570&quot; end=&quot;28579&quot;/&gt;
 *               &lt;/attr&gt;
 *           &lt;/art&gt;
 *         &lt;/match&gt;
 *     &lt;/search&gt;
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactXmlQueryResultParser extends AbstractSaxHandler {

   private List<XmlArtifactSearchResult> results;
   private XmlArtifactSearchResult currentResult;
   private long currentAttribute;
   private int currentArtifact;

   public ArtifactXmlQueryResultParser() {
      this.results = new ArrayList<XmlArtifactSearchResult>();
      this.currentResult = null;
      this.currentAttribute = -1;
      this.currentArtifact = -1;
   }

   private void handleError(Throwable ex) {
      OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
   }

   public List<XmlArtifactSearchResult> getResults() {
      return results;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#startElementFound(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      try {
         if (name.equalsIgnoreCase("search")) {
            currentResult = new XmlArtifactSearchResult();
         } else if (name.equalsIgnoreCase("match")) {
            handleMatch(attributes);
         } else if (name.equalsIgnoreCase("art")) {
            handleArtifact(attributes);
         } else if (name.equalsIgnoreCase("attr")) {
            handleAttribute(attributes);
         } else if (name.equalsIgnoreCase("location")) {
            handleMatchLocation(attributes);
         }
      } catch (Throwable ex) {
         handleError(ex);
      }
   }

   private void handleMatch(Attributes attributes) {
      String branchIdStr = attributes.getValue("branchId");
      if (Strings.isValid(branchIdStr)) {
         if (currentResult != null) {
            currentResult.setBranchId(Integer.parseInt(branchIdStr));
         }
      }
   }

   private void handleArtifact(Attributes attributes) {
      String artIdStr = attributes.getValue("artId");
      if (Strings.isValid(artIdStr)) {
         currentArtifact = Integer.parseInt(artIdStr);
         if (currentResult != null) {
            currentResult.addArtifact(currentArtifact);
         }
      }
   }

   private void handleAttribute(Attributes attributes) {
      String gammaId = attributes.getValue("gammaId");
      if (Strings.isValid(gammaId)) {
         currentAttribute = Long.parseLong(gammaId);
      }
   }

   private void handleMatchLocation(Attributes attributes) {
      String startAt = attributes.getValue("start");
      String stopAt = attributes.getValue("end");
      if (currentResult != null && currentArtifact > -1 && currentAttribute > -1 && Strings.isValid(startAt) && Strings.isValid(stopAt)) {
         int start = Integer.parseInt(startAt);
         int stop = Integer.parseInt(stopAt);
         currentResult.addAttribute(currentArtifact, currentAttribute, start, stop);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#endElementFound(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void endElementFound(String uri, String localName, String name) throws SAXException {
      try {
         if (name.equalsIgnoreCase("search")) {
         } else if (name.equalsIgnoreCase("match")) {
            if (currentResult != null) {
               results.add(currentResult);
               currentResult = null;
            }
         } else if (name.equalsIgnoreCase("art")) {
            currentArtifact = -1;
         } else if (name.equalsIgnoreCase("attr")) {
            currentAttribute = -1;
         } else if (name.equalsIgnoreCase("location")) {
         }
      } catch (Throwable ex) {
         handleError(ex);
      }
   }

   public final class XmlArtifactSearchResult {
      private ArtifactJoinQuery artifactJoinQuery;
      private int branchId;
      private Map<Integer, HashCollection<Long, MatchLocation>> attributeMatches;

      public XmlArtifactSearchResult() {
         this.artifactJoinQuery = JoinUtility.createArtifactJoinQuery();
         this.branchId = -1;
         this.attributeMatches = new HashMap<Integer, HashCollection<Long, MatchLocation>>();
      }

      private void setBranchId(int branchId) {
         this.branchId = branchId;
      }

      private void addArtifact(int artifactId) {
         artifactJoinQuery.add(artifactId, branchId);
      }

      private void addAttribute(int artifactId, long gammaId, int start, int end) {
         HashCollection<Long, MatchLocation> matches = attributeMatches.get(artifactId);
         if (matches == null) {
            matches = new HashCollection<Long, MatchLocation>();
            attributeMatches.put(artifactId, matches);
         }
         matches.put(gammaId, new MatchLocation(start, end));
      }

      public ArtifactJoinQuery getJoinQuery() {
         return artifactJoinQuery;
      }

      public int getBranchId() {
         return branchId;
      }

      public boolean hasAttriuteMatches() {
         return !attributeMatches.isEmpty();
      }

      public HashCollection<Long, MatchLocation> getAttributeMatches(int artifactId) {
         return attributeMatches.get(artifactId);
      }
   }

   public class MatchLocation {
      private int startPosition;
      private int endPosition;

      public MatchLocation(int startPosition, int endPosition) {
         this.startPosition = startPosition;
         this.endPosition = endPosition;
      }

      public int getStartPosition() {
         return startPosition;
      }

      public int getEndPosition() {
         return endPosition;
      }
   }
}
