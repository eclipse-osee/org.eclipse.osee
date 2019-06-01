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
package org.eclipse.osee.define.rest.importing.parsers;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.net.URI;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class XmlDataExtractor extends AbstractArtifactExtractor {

   @Override
   protected XResultData extractFromSource(OrcsApi orcsApi, XResultData results, URI source, RoughArtifactCollector collector) throws Exception {
      ArtifactTypeToken primaryArtifactType = null;
      ArtifactTypes artTypes = orcsApi.getOrcsTypes().getArtifactTypes();

      for (ArtifactTypeToken type : artTypes.getAll()) {
         if (type.getName().equals(Lib.removeExtension(new File(source).getName()))) {
            primaryArtifactType = type;
         }
      }
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new XmlDataSaxHandler(orcsApi, results, collector, primaryArtifactType));
      xmlReader.parse(new InputSource(new InputStreamReader(source.toURL().openStream(), "UTF-8")));
      return results;
   }

   @Override
   public FileFilter getFileFilter() {
      return new FileFilter() {
         @Override
         public boolean accept(File file) {
            return file.isDirectory() || file.isFile() && file.getName().endsWith(".xml");
         }
      };
   }

   @Override
   public String getName() {
      return "Excel XML Data";
   }

   @Override
   public String getDescription() {
      return "Extract Data from xml of the form <row><cell></cell>*</row>* like that created by Excel data export.";
   }

   @Override
   public boolean usesTypeList() {
      return true;
   }
}