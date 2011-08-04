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

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenVersionArtifactAction extends Action {

   private static final String ACTION_TEXT = "Open Targeted for Version";
   private final AbstractWorkflowArtifact sma;

   public OpenVersionArtifactAction(AbstractWorkflowArtifact sma) {
      super(ACTION_TEXT, ImageManager.getImageDescriptor(FrameworkImage.VERSION));
      setToolTipText(ACTION_TEXT);
      this.sma = sma;
   }

   @Override
   public void run() {
      try {
         if (sma.getTargetedVersion() != null) {
            RendererManager.open(sma.getTargetedVersion(), PresentationType.DEFAULT_OPEN);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }
}
