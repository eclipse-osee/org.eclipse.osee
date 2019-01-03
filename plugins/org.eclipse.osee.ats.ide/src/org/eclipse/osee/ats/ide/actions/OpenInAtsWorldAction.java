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
package org.eclipse.osee.ats.ide.actions;

import java.util.Arrays;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorldAction extends AbstractAtsAction {

   private final AbstractWorkflowArtifact sma;

   public OpenInAtsWorldAction(AbstractWorkflowArtifact sma) {
      super();
      this.sma = sma;
      setText("Open in ATS World Editor");
   }

   @Override
   public void runWithException() {
      ActionArtifact actionArt = sma.getParentActionArtifact();
      if (actionArt != null) {
         WorldEditor.open(
            new WorldEditorSimpleProvider("Action " + actionArt.getAtsId(), Arrays.asList(actionArt), null, sma));
      } else {
         WorldEditor.open(new WorldEditorSimpleProvider(sma.getArtifactTypeName() + " " + sma.getAtsId(),
            Arrays.asList(sma), null, sma));
         throw new OseeStateException("No Parent Action; Opening Team Workflow");
      }
      return;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.GLOBE);
   }

}
