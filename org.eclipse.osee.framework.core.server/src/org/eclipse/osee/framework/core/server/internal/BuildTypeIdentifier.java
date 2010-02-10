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
package org.eclipse.osee.framework.core.server.internal;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.PatternSyntaxException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class BuildTypeIdentifier {

   private static final String DEFAULT_IDENTIFIER = "N/A";
   private static final String START_TAG = "build";
   private static final String ID_TAG = "type";
   private static final String ENTRY_TAG = "matches";

   private final BuildTypeDataProvider provider;

   public BuildTypeIdentifier(BuildTypeDataProvider provider) {
      this.provider = provider;
   }

   public String getBuildDesignation(String clientVersion) {
      BuildInfo buildType = null;
      try {
         buildType = match(clientVersion, getBuildTypeEntries());
      } catch (OseeCoreException ex) {
         // Do Nothing -
      }
      return buildType != null ? buildType.getName() : DEFAULT_IDENTIFIER;
   }

   public synchronized Collection<BuildInfo> getBuildTypeEntries() throws OseeCoreException {
      Collection<BuildInfo> dataEntries = new ArrayList<BuildInfo>();
      String rawData = provider.getData();
      BuildTypeParser parser = new BuildTypeParser();
      parser.load(rawData, dataEntries);
      return dataEntries;
   }

   private static BuildInfo match(String version, Collection<BuildInfo> dataEntries) {
      BuildInfo toReturn = null;
      if (Strings.isValid(version)) {
         for (BuildInfo entry : dataEntries) {
            for (String regEx : entry.getVersions()) {
               if (Strings.isValid(regEx)) {
                  try {
                     if (isCompatibleVersion(regEx, version)) {
                        toReturn = entry;
                     }
                  } catch (PatternSyntaxException ex) {
                     // Do Nothing -
                  }
               }
            }
         }
      }
      return toReturn;
   }

   private static boolean isCompatibleVersion(String serverVersion, String clientVersion) throws PatternSyntaxException {
      boolean result = false;
      if (serverVersion.equals(clientVersion)) {
         result = true;
      } else {
         result = clientVersion.matches(serverVersion);
         if (!result) {
            result = serverVersion.matches(clientVersion);
         }
      }
      return result;
   }

   private static final class BuildTypeParser {
      private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
      private BuildInfo currentType;
      private String localName;
      private String uri;
      private boolean isVersionPattern;

      public BuildTypeParser() {
         reset();
      }

      public void reset() {
         localName = null;
         uri = null;
         currentType = null;
         isVersionPattern = false;
      }

      private void load(String rawData, Collection<BuildInfo> dataEntries) throws OseeCoreException {
         if (Strings.isValid(rawData)) {
            Reader reader = new StringReader(rawData);
            try {
               XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(reader);
               while (streamReader.hasNext()) {
                  process(streamReader, dataEntries);
                  streamReader.next();
               }
            } catch (XMLStreamException ex) {
               throw new OseeWrappedException(ex);
            } finally {
               Lib.close(reader);
            }
         }
      }

      private void process(XMLStreamReader reader, Collection<BuildInfo> dataEntries) {
         int eventType = reader.getEventType();
         switch (eventType) {
            case XMLStreamConstants.START_ELEMENT:
               localName = reader.getLocalName();
               uri = reader.getNamespaceURI();
               if (START_TAG.equals(localName)) {
                  String typeName = reader.getAttributeValue(uri, ID_TAG);
                  if (Strings.isValid(typeName)) {
                     currentType = new BuildInfo(typeName);
                  }
               } else if (ENTRY_TAG.equals(localName) && currentType != null) {
                  isVersionPattern = true;
               }
               break;
            case XMLStreamConstants.CDATA:
            case XMLStreamConstants.CHARACTERS:
               if (isVersionPattern) {
                  String data = reader.getText();
                  if (Strings.isValid(data)) {
                     currentType.addPattern(data);
                  }
               }
               break;
            case XMLStreamConstants.END_ELEMENT:
               localName = reader.getLocalName();
               uri = reader.getNamespaceURI();
               if (START_TAG.equals(localName) && currentType != null) {
                  dataEntries.add(currentType);
                  reset();
               } else if (ENTRY_TAG.equals(localName) && currentType != null) {
                  isVersionPattern = false;
               }
               break;
         }
      }
   }
}
