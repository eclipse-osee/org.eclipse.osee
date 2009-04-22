/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.artifact.editor.panels;

import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditorFormSection extends SectionPart {

   private final ArtifactEditor editor;

   /**
    * @param parent
    * @param toolkit
    * @param style
    */
   public ArtifactEditorFormSection(ArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style);
      this.editor = editor;
   }

   public ArtifactEditor getEditor() {
      return editor;
   }

   public ArtifactEditorInput getEditorInput() {
      return editor.getEditorInput();
   }
}
