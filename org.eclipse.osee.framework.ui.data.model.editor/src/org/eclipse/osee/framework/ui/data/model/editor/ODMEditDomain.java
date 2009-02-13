/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.data.model.editor;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.Tool;
import org.eclipse.ui.IEditorPart;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditDomain extends DefaultEditDomain {
   private ODMEditorSelectionTool selectionTool;

   public ODMEditDomain(IEditorPart editorPart) {
      super(editorPart);
   }

   public Tool getDefaultTool() {
      if (selectionTool == null) {
         selectionTool = new ODMEditorSelectionTool();
      }
      return selectionTool;
   }

}
