/**
 * 
 */
package org.eclipse.osee.ats.editor;

import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;

/**
 * @author Jeff C. Phillips
 */
public class AtsWorldRenderer extends DefaultArtifactRenderer {
   private static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.atsworldeditor.command";
   public static final String RENDERER_EXTENSION = "org.eclipse.osee.ats.editor.AtsWorldRenderer";

   /**
    * @param rendererId
    */
   public AtsWorldRenderer(String rendererId) {
      super(rendererId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getApplicabilityRating(org.eclipse.osee.framework.ui.skynet.render.PresentationType, org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (artifact instanceof IATSArtifact) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getCommandId()
    */
   @Override
   public String getCommandId() {
      return COMMAND_ID;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getId()
    */
   @Override
   public String getId() {
      return RENDERER_EXTENSION;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getName()
    */
   @Override
   public String getName() {
      return "ATS World Editor";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#newInstance()
    */
   @Override
   public AtsWorldRenderer newInstance() throws OseeCoreException {
      return new AtsWorldRenderer(getId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#open(java.util.List)
    */
   @Override
   public void open(List<Artifact> artifacts) throws OseeCoreException {
      if (OseeAts.getAtsLib() != null) {
         OseeAts.getAtsLib().openInAtsWorldEditor("ATS", artifacts);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#preview(java.util.List)
    */
   @Override
   public void preview(List<Artifact> artifacts) throws OseeCoreException {
      open(artifacts);
   }

}
