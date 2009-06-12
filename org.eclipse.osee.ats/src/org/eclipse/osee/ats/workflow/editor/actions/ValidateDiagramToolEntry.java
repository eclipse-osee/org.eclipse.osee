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
package org.eclipse.osee.ats.workflow.editor.actions;

import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ValidateDiagramToolEntry extends ToolEntry {

   /**
    * @param label
    * @param shortDesc
    * @param iconSmall
    * @param iconLarge
    */
   public ValidateDiagramToolEntry() {
      super("Validate Diagram", "Validate", ImageManager.getImageDescriptor(AtsImage.CHECK_BLUE),
            ImageManager.getImageDescriptor(AtsImage.CHECK_BLUE), ValidateDiagramTool.class);
   }

}
