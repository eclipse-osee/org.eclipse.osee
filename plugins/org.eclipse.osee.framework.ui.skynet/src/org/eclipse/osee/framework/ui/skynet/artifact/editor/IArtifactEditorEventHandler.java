/*
 * Created on Jun 30, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public interface IArtifactEditorEventHandler {

   public AbstractEventArtifactEditor getEditor();

   public boolean isDisposed();

   public void refreshDirtyArtifact();

   public void closeEditor();

   public void refreshRelations();

   public Artifact getArtifactFromEditorInput();

   public void setMainImage(Image titleImage);

}
