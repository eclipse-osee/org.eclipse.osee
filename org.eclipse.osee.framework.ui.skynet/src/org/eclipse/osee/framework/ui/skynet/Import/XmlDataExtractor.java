/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.io.FileFilter;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Ryan D. Brooks
 */
public class XmlDataExtractor extends AbstractArtifactExtractor {
   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#discoverArtifactAndRelationData(java.io.File)
    */
   @Override
   public void discoverArtifactAndRelationData(File artifactsFile, IArtifactImportResolver artifactResolver, Branch branch, ArtifactType primaryArtifactType) throws Exception {
      Element documentElement = Jaxp.readXmlDocument(artifactsFile).getDocumentElement();
      NodeList rows = documentElement.getChildNodes();

      for (int i = 0; i < rows.getLength(); i++) {
         NodeList cells = rows.item(i).getChildNodes();
         for (int j = 0; j < cells.getLength(); j++) {
            cells.item(j).getTextContent();
         }
      }
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