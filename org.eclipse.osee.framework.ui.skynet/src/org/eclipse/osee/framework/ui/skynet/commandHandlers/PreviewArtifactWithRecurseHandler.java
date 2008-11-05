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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;

/**
 * @author Jeff C. Phillips
 */
public class PreviewArtifactWithRecurseHandler extends PreviewArtifactHandler {
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.commandHandlers.PreviewArtifactHandler#getPreviewType()
    */

   @Override
   protected VariableMap getPreviewOptions() throws OseeArgumentException {
      return new VariableMap(ITemplateRenderer.PREVIEW_WITH_RECURSE_OPTION_PAIR);
   }
}
