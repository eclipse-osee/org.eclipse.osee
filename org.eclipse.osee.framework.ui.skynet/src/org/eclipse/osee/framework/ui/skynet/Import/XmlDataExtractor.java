/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
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
   public void discoverArtifactAndRelationData(File artifactsFile, Branch branch, ArtifactType primaryArtifactType) throws Exception {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(new XmlDataSaxHandler(branch, primaryArtifactType));
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