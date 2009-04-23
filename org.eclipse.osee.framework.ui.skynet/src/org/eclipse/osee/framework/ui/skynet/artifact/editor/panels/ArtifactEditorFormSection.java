/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.artifact.editor.panels;

import org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations.NewArtifactEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditorFormSection extends SectionPart {

   private final NewArtifactEditor editor;

   /**
    * @param parent
    * @param toolkit
    * @param style
    */
   public ArtifactEditorFormSection(NewArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style);
      this.editor = editor;
   }

   public NewArtifactEditor getEditor() {
      return editor;
   }

   public BaseArtifactEditorInput getEditorInput() {
      return editor.getEditorInput();
   }
}
