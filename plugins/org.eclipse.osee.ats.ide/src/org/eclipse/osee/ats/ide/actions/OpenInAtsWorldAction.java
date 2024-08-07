/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import java.util.Arrays;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
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
      IAtsAction action = sma.getParentAction();
      if (action != null) {
         WorldEditor.open(new WorldEditorSimpleProvider("Action " + action.getAtsId(),
            Arrays.asList(action.getStoreObject()), null, sma));
      } else {
         WorldEditor.open(new WorldEditorSimpleProvider(sma.getArtifactTypeName() + " " + sma.getAtsId(),
            Arrays.asList(sma), null, sma));
         // Only error if workItem is supposed to have an Action; eg: Goals do not
         if (sma.hasAction()) {
            throw new OseeStateException("No Parent Action; Opening Workflow");
         }
      }
      return;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.GLOBE);
   }

}
