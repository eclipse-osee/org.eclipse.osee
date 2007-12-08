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

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Jeff C. Phillips
 * @author Paul K. Waldfogel
 */
public abstract class PreviewArtifactHandler extends AbstractSelectionHandler {
   private static final RendererManager rendererManager = RendererManager.getInstance();
   List<Artifact> mySelectedArtifactList = super.getArtifactList();

   public PreviewArtifactHandler() {
      super(new String[] {});
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!mySelectedArtifactList.isEmpty()) {
         rendererManager.previewInJob(mySelectedArtifactList, getPreviewType());
      }
      return null;
   }

   // /*
   // * (NON-JAVADOC)
   // *
   // * @SEE
   // ORG.ECLIPSE.OSEE.FRAMEWORK.UI.SKYNET.COMMANDHANDLERS.ABSTRACTARTIFACTSELECTIONHANDLER#PERMISSIONLEVEL()
   // */
   // @OVERRIDE
   // PROTECTED PERMISSIONENUM PERMISSIONLEVEL() {
   // RETURN PERMISSIONENUM.READ;
   // }

   protected abstract String getPreviewType();

   @Override
   public boolean isEnabled() {
      return true;
   }

}
