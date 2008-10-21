/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.InputStream;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.word.WordConverter;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public abstract class WordRenderer extends FileRenderer {
   // We need MS Word, so look for the program that is for .doc files
   private static final Program wordApp = Program.findProgram("doc");

   /**
    * @param rendererId
    */
   public WordRenderer(String rendererId) {
      super(rendererId);
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
}