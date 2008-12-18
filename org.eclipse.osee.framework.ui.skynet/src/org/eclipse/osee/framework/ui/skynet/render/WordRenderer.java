/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.InputStream;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.word.WordConverter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public abstract class WordRenderer extends FileRenderer {
   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getImage(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */

   // We need MS Word, so look for the program that is for .doc files
   private static final Program wordApp = Program.findProgram("doc");

   /**
    * @param rendererId
    */
   public WordRenderer(String rendererId) {
      super(rendererId);
   }

   @Override
   public Image getImage(Artifact artifact) throws OseeCoreException {
      return SkynetActivator.getInstance().getImageForProgram("doc");
   }

   public static ImageDescriptor getImageDescriptor() {
      return SkynetActivator.getInstance().getImageDescriptorForProgram("doc");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#minimumRanking()
    */
   @Override
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#getName()
    */
   @Override
   public String getName() {
      return "MS Word Edit";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getAssociatedExtension(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public String getAssociatedExtension(Artifact artifact) throws OseeCoreException {
      return "xml";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#getAssociatedProgram(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      if (wordApp == null) {
         throw new OseeStateException("No program associated with the extension .doc");
      }
      return wordApp;
   }

   @Override
   public String generateHtml(Artifact artifact) throws OseeCoreException {
      InputStream xml = getRenderInputStream(artifact, PresentationType.PREVIEW);
      return WordConverter.toHtml(xml);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#supportsCompare()
    */
   @Override
   public boolean supportsCompare() {
      return true;
   }
}