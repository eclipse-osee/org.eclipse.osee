/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.data.model.editor;

import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

/**
 * A Graphiti Diagram editor that saves its xml representation in an artifact. This editor is registered using the
 * extension point org.eclipse.ui.editors.
 *
 * @see http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.graphiti.doc%2Fjavadoc%2Forg%2Feclipse%2Fgraphiti%
 * 2Fui%2Feditor%2FDiagramEditor.html
 * @author Ryan D. Brooks
 */
public class GraphitiDiagramArtifactEditor extends DiagramEditor {
   public static final String EDITOR_ID =
      "org.eclipse.osee.framework.ui.data.model.editor.graphiti.GraphitiDiagramArtifactEditor";

   /**
    * Overriding createDiagramBehavior() provides extensibility to specify an artifact-based PersistencyBehavior
    */
   @Override
   protected DiagramBehavior createDiagramBehavior() {
      return new GraphitiArtifactDiagramBehavior(this);
   }

}
