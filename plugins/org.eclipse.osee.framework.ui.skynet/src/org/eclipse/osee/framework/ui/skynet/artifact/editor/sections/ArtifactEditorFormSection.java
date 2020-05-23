/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Roberto E. Escobar
 */
public abstract class ArtifactEditorFormSection extends SectionPart {

   private final AbstractArtifactEditor editor;

   public ArtifactEditorFormSection(AbstractArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style);
      this.editor = editor;
   }

   public AbstractArtifactEditor getEditor() {
      return editor;
   }

   public ArtifactEditorInput getEditorInput() {
      return (ArtifactEditorInput) editor.getEditorInput();
   }
}