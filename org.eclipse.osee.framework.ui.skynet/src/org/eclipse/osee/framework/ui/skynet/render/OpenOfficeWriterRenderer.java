/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.InputStream;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class OpenOfficeWriterRenderer extends FileRenderer implements ITemplateRenderer {

   /**
    * @param rendererId
    */
   public OpenOfficeWriterRenderer(String rendererId) {
      super(rendererId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
    */
   @Override
   public OpenOfficeWriterRenderer newInstance() throws OseeCoreException {
      return new OpenOfficeWriterRenderer(getId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getAssociatedExtension(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public String getAssociatedExtension(Artifact artifact) {
      return "odt";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, java.util.List, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer#getAssociatedProgram(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      throw new UnsupportedOperationException();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#getApplicabilityRating(org.eclipse.osee.framework.ui.skynet.render.PresentationType, org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      return NO_MATCH;
   }

   private String getTemplate(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      return TemplateManager.getTemplate(this, artifact, presentationType.name(), getStringOption(TEMPLATE_OPTION)).getSoleAttributeValue(
            NativeArtifact.CONTENT_NAME);
   }
}