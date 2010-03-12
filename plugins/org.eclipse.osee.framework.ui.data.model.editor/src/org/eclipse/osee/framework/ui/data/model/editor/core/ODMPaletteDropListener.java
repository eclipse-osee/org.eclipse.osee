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
package org.eclipse.osee.framework.ui.data.model.editor.core;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;

/**
 * @author Roberto E. Escobar
 */
public class ODMPaletteDropListener extends TemplateTransferDropTargetListener {

   public ODMPaletteDropListener(EditPartViewer viewer) {
      super(viewer);
   }

   protected CreationFactory getFactory(Object template) {
      return (CreationFactory) template;
   }
}
