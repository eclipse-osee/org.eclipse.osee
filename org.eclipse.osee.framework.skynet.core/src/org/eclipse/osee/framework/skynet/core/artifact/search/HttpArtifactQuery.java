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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStoreWriter;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.ISearchConfirmer;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactXmlQueryResultParser.MatchLocation;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactXmlQueryResultParser.XmlArtifactSearchResult;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
final class HttpArtifactQuery {
   private final String queryString;
   private final boolean matchWordOrder;
   private final String[] attributeTypes;
   private final boolean includeDeleted;
   private final IOseeBranch branch;
   private final boolean isCaseSensitive;

   protected HttpArtifactQuery(IOseeBranch branch, String queryString, boolean matchWordOrder, boolean includeDeleted, boolean isCaseSensitive, String... attributeTypes) {
      this.branch = branch;
      this.matchWordOrder = matchWordOrder;
      this.includeDeleted = includeDeleted;
      this.attributeTypes = attributeTypes != null ? attributeTypes : new String[0];
      this.queryString = queryString;
      this.isCaseSensitive = isCaseSensitive;
   }

   private String getSearchUrl(String sessionId) throws OseeDataStoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("sessionId", sessionId);
      return HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.SEARCH_CONTEXT, parameters);
   }

   private CharBackedInputStream getSearchParameters(String sessionId, boolean withMatches, boolean findAllMatchLocations) throws IOException, OseeCoreException {
      CharBackedInputStream backedInputStream = new CharBackedInputStream();

      PropertyStore propertyStore = new PropertyStore(sessionId);
      propertyStore.put("branchId", BranchManager.getBranchId(branch));
      propertyStore.put("query", queryString);
      propertyStore.put("include deleted", includeDeleted);
      propertyStore.put("match word order", matchWordOrder);
      propertyStore.put("attributeType", attributeTypes);
      propertyStore.put("case sensitive", isCaseSensitive);

      if (matchWordOrder) {
         propertyStore.put("as xml", withMatches);
         if (withMatches) {
            propertyStore.put("find all locations", findAllMatchLocations);
         }
      }
      PropertyStoreWriter writer = new PropertyStoreWriter();
      writer.save(propertyStore, backedInputStream.getWriter());

      return backedInputStream;
   }

   public List<Artifact> getArtifacts(ArtifactLoad loadLevel, ISearchConfirmer confirmer, boolean reload, boolean historical, boolean allowDeleted) throws OseeCoreException {
      List<Artifact> toReturn = null;
      Pair<String, ByteArrayOutputStream> data = executeSearch(false, false);
      if (data != null) {
         try {
            Pair<Integer, Integer> queryIdAndSize = handleAsDbJoin(data.getSecond());
            if (queryIdAndSize != null && queryIdAndSize.getSecond() > 0) {
               try {
                  toReturn =
                        ArtifactLoader.loadArtifactsFromQueryId(queryIdAndSize.getFirst(), loadLevel, confirmer,
                              queryIdAndSize.getSecond(), reload, historical, allowDeleted);
               } finally {
                  JoinUtility.deleteQuery(JoinUtility.JoinItem.ARTIFACT, queryIdAndSize.getFirst().intValue());
               }
            }
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
      }
      if (toReturn == null) {
         toReturn = java.util.Collections.emptyList();
      }
      return toReturn;
   }

   public List<ArtifactMatch> getArtifactsWithMatches(ArtifactLoad loadLevel, ISearchConfirmer confirmer, boolean reload, boolean historical, boolean allowDeleted, boolean findAllMatchLocations) throws OseeCoreException {
      List<ArtifactMatch> toReturn = new ArrayList<ArtifactMatch>();
      Pair<String, ByteArrayOutputStream> data = executeSearch(true, findAllMatchLocations);
      if (data != null) {

         try {
            if (data.getFirst().endsWith("xml")) {
               List<XmlArtifactSearchResult> results = handleAsXmlResults(data.getSecond());
               for (XmlArtifactSearchResult result : results) {
                  try {
                     result.getJoinQuery().store();
                     List<Artifact> artifacts =
                           ArtifactLoader.loadArtifactsFromQueryId(result.getJoinQuery().getQueryId(), loadLevel,
                                 confirmer, result.getJoinQuery().size(), reload, historical, allowDeleted);
                     for (Artifact artifact : artifacts) {
                        ArtifactMatch artMatch = new ArtifactMatch(artifact);
                        HashCollection<Long, MatchLocation> attributeMatches =
                              result.getAttributeMatches(artifact.getArtId());
                        if (attributeMatches != null) {
                           artMatch.addMatches(attributeMatches);
                        }
                        toReturn.add(artMatch);
                     }
                  } finally {
                     result.getJoinQuery().delete();
                  }
               }
            } else if (data.getFirst().endsWith("plain")) {
               Pair<Integer, Integer> queryIdAndSize = handleAsDbJoin(data.getSecond());
               if (queryIdAndSize != null && queryIdAndSize.getSecond() > 0) {
                  try {
                     List<Artifact> artifactList =
                           ArtifactLoader.loadArtifactsFromQueryId(queryIdAndSize.getFirst(), loadLevel, confirmer,
                                 queryIdAndSize.getSecond(), reload, historical, allowDeleted);
                     for (Artifact artifact : artifactList) {
                        toReturn.add(new ArtifactMatch(artifact));
                     }
                  } finally {
                     JoinUtility.deleteQuery(JoinUtility.JoinItem.ARTIFACT, queryIdAndSize.getFirst().intValue());
                  }
               }
            }
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
      }
      return toReturn;
   }

   private Pair<String, ByteArrayOutputStream> executeSearch(boolean withMatches, boolean findAllMatchLocations) throws OseeCoreException {
      Pair<String, ByteArrayOutputStream> toReturn = null;
      String sessionId = ClientSessionManager.getSessionId();
      CharBackedInputStream inputStream = null;
      try {
         inputStream = getSearchParameters(sessionId, withMatches, findAllMatchLocations);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult httpRequestResult =
               HttpProcessor.post(new URL(getSearchUrl(sessionId)), inputStream, "application/xml", "UTF-8",
                     outputStream);
         if (httpRequestResult.getCode() == HttpURLConnection.HTTP_ACCEPTED) {
            toReturn = new Pair<String, ByteArrayOutputStream>(httpRequestResult.getContentType(), outputStream);
         } else if (httpRequestResult.getCode() != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new OseeCoreException(String.format("Search error due to bad request: url[%s] status code: [%s]",
                  inputStream.toString(), httpRequestResult.getCode()));
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (Exception ex) {
               throw new OseeWrappedException(ex);
            }
         }
      }
      return toReturn;
   }

   private List<XmlArtifactSearchResult> handleAsXmlResults(ByteArrayOutputStream outputStream) throws SAXException, IOException {
      ArtifactXmlQueryResultParser parser = new ArtifactXmlQueryResultParser();
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(parser);
      xmlReader.parse(new InputSource(new ByteArrayInputStream(outputStream.toByteArray())));
      return parser.getResults();
   }

   private Pair<Integer, Integer> handleAsDbJoin(ByteArrayOutputStream outputStream) throws UnsupportedEncodingException {
      Pair<Integer, Integer> toReturn = null;
      String queryIdString = outputStream.toString("UTF-8");
      if (Strings.isValid(queryIdString)) {
         String[] entries = queryIdString.split(",\\s*");
         if (entries.length >= 2) {
            toReturn = new Pair<Integer, Integer>(new Integer(entries[0]), new Integer(entries[1]));
         }
      }
      return toReturn;
   }
}
