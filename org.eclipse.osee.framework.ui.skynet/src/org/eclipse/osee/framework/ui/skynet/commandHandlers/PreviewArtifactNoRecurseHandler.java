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

/**
 * @author Jeff C. Phillips
 */
public class PreviewArtifactNoRecurseHandler extends PreviewArtifactHandler {
   private static final String PREVIEW_ARTIFACT = "PREVIEW_ARTIFACT";

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.commandHandlers.PreviewArtifactHandler#getPreviewType()
    */
   @Override
   protected String getPreviewType() {
      return PREVIEW_ARTIFACT;
   }
}
