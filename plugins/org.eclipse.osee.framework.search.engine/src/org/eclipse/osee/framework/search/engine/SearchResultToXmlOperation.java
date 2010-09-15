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
package org.eclipse.osee.framework.search.engine;

import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.internal.Activator;

public class SearchResultToXmlOperation extends AbstractOperation {

   private final SearchResult searchResult;
   private final Writer writer;

   public SearchResultToXmlOperation(SearchResult searchResult, Writer writer) {
      super("Write Search Results", Activator.PLUGIN_ID);
      this.searchResult = searchResult;
      this.writer = writer;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkNotNull(searchResult, "searchResult");
      Conditions.checkNotNull(writer, "writer");

      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter xmlWriter = null;
      try {
         xmlWriter = factory.createXMLStreamWriter(writer);
         xmlWriter.writeStartDocument();

         writeRawSearch(xmlWriter, searchResult.getRawSearch());
         writeSearchWords(xmlWriter, searchResult.getSearchTags());
         writeErrorMessage(xmlWriter, searchResult.getErrorMessage());
         writeSearchData(xmlWriter, searchResult);
         xmlWriter.writeEndElement();
         xmlWriter.writeEndDocument();
      } finally {
         if (xmlWriter != null) {
            xmlWriter.close();
         }
      }
   }

   private void writeSearchWords(XMLStreamWriter xmlWriter, Map<String, Long> searchTags) throws XMLStreamException {
      xmlWriter.writeStartElement("searchTags");
      for (Entry<String, Long> word : searchTags.entrySet()) {
         xmlWriter.writeStartElement("word");
         xmlWriter.writeCharacters(word.getKey());
         xmlWriter.writeEndElement();
         xmlWriter.writeStartElement("tag");
         xmlWriter.writeCharacters(String.valueOf(word.getValue()));
         xmlWriter.writeEndElement();
      }
      xmlWriter.writeEndElement();
   }

   private void writeRawSearch(XMLStreamWriter xmlWriter, String rawSearch) throws XMLStreamException {
      xmlWriter.writeStartElement("rawSearch");
      if (Strings.isValid(rawSearch)) {
         xmlWriter.writeCData(rawSearch);
      }
      xmlWriter.writeEndElement();
   }

   private void writeErrorMessage(XMLStreamWriter xmlWriter, String errorMessage) throws XMLStreamException {
      xmlWriter.writeStartElement("error");
      if (Strings.isValid(errorMessage)) {
         xmlWriter.writeCData(errorMessage);
      }
      xmlWriter.writeEndElement();
   }

   private void writeSearchData(XMLStreamWriter xmlWriter, SearchResult results) throws Exception {
      for (Integer branchId : results.getBranchIds()) {
         xmlWriter.writeStartElement("match");
         xmlWriter.writeAttribute("branchId", String.valueOf(branchId));
         for (ArtifactMatch artifactMatch : results.getArtifacts(branchId)) {
            writeArtifactMatch(xmlWriter, artifactMatch);
         }
         xmlWriter.writeEndElement();
      }
   }

   private void writeArtifactMatch(XMLStreamWriter xmlWriter, ArtifactMatch artifactMatch) throws XMLStreamException {
      xmlWriter.writeStartElement("art");
      xmlWriter.writeAttribute("artId", String.valueOf(artifactMatch.getArtId()));
      for (Long gammaId : artifactMatch.getAttributes()) {
         Collection<MatchLocation> locations = artifactMatch.getMatchLocations(gammaId);
         writeAttributes(xmlWriter, gammaId, locations);
      }
      xmlWriter.writeEndElement();
   }

   private void writeAttributes(XMLStreamWriter xmlWriter, Long gammaId, Collection<MatchLocation> locations) throws XMLStreamException {
      xmlWriter.writeStartElement("attr");
      xmlWriter.writeAttribute("gammaId", String.valueOf(gammaId));
      if (locations != null) {
         for (MatchLocation location : locations) {
            writeLocationData(xmlWriter, location);
         }
      }
      xmlWriter.writeEndElement();
   }

   private void writeLocationData(XMLStreamWriter xmlWriter, MatchLocation location) throws XMLStreamException {
      xmlWriter.writeStartElement("location");
      xmlWriter.writeAttribute("start", String.valueOf(location.getStartPosition()));
      xmlWriter.writeAttribute("end", String.valueOf(location.getEndPosition()));
      xmlWriter.writeEndElement();
   }
}
