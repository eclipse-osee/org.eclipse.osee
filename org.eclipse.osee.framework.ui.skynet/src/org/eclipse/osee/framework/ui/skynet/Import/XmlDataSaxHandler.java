/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.Import;

import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class XmlDataSaxHandler extends AbstractSaxHandler {
   private int level = 0;
   private RoughArtifact roughArtifact;
   private final Branch branch;
   private final ArtifactType artifactType;

   /**
    * @param branch
    */
   public XmlDataSaxHandler(Branch branch, ArtifactType artifactType) {
      super();
      this.branch = branch;
      this.artifactType = artifactType;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#endElementFound(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void endElementFound(String uri, String localName, String name) throws SAXException {
      level--;
      if (level == 3) {
         roughArtifact.addAttribute(localName, getContents());
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#startElementFound(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      level++;

      if (level == 2) {
         roughArtifact = new RoughArtifact(branch);
         roughArtifact.setPrimaryArtifactType(artifactType);
         roughArtifact.setForcePrimaryType(true);
      }

   }
}