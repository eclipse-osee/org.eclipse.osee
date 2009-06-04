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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class XmlDataExtractor extends AbstractArtifactExtractor {
   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#discoverArtifactAndRelationData(java.io.File)
    */
   @Override
   public void discoverArtifactAndRelationData(File artifactsFile, Branch branch) throws Exception {
      ArtifactType primaryArtifactType = ArtifactTypeManager.getType(Lib.removeExtension(artifactsFile));
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new XmlDataSaxHandler(this, branch, primaryArtifactType));
      xmlReader.parse(new InputSource(new InputStreamReader(new FileInputStream(artifactsFile), "UTF-8")));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getFileFilter()
    */
   @Override
   public FileFilter getFileFilter() {
      return new FileFilter() {
         public boolean accept(File file) {
            return file.isDirectory() || (file.isFile() && file.getName().endsWith(".xml"));
         }
      };
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getName()
    */
   @Override
   public String getName() {
      return "Excel XML Data";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getDescription()
    */
   @Override
   public String getDescription() {
      return "Extract Data from xml of the form <row><cell></cell>*</row>* like that created by Excel data export";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#usesTypeList()
    */
   @Override
   public boolean usesTypeList() {
      return true;
   }
}