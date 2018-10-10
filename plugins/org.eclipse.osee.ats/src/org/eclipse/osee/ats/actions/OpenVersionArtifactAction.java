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
package org.eclipse.osee.ats.actions;

import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenVersionArtifactAction extends AbstractAtsAction {

   private static final String ACTION_TEXT = "Open Targeted for Version";
   private final AbstractWorkflowArtifact sma;

   public OpenVersionArtifactAction(AbstractWorkflowArtifact sma) {
      super(ACTION_TEXT, ImageManager.getImageDescriptor(FrameworkImage.VERSION));
      setToolTipText(ACTION_TEXT);
      this.sma = sma;
   }

   @Override
   public void runWithException() {
      if (AtsClientService.get().getVersionService().hasTargetedVersion(sma)) {
         RendererManager.open(AtsClientService.get().getQueryServiceClient().getArtifact(
            AtsClientService.get().getVersionService().getTargetedVersion(sma)), PresentationType.DEFAULT_OPEN);
      }
   }
}
