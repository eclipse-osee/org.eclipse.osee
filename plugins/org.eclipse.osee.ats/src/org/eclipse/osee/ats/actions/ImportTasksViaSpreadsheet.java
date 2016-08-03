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

import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.Import.ImportTasksFromSpreadsheet;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ImportTasksViaSpreadsheet extends AbstractAtsAction {

   private final TeamWorkFlowArtifact taskableArt;
   private final ImportListener listener;

   public ImportTasksViaSpreadsheet(TeamWorkFlowArtifact taskableArt, ImportListener listener) {
      super();
      this.taskableArt = taskableArt;
      this.listener = listener;
      setText("Import Tasks via spreadsheet");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.IMPORT));
   }

   @Override
   public void runWithException() {
      ImportTasksFromSpreadsheet blamOperation = new ImportTasksFromSpreadsheet();
      blamOperation.setTaskableStateMachineArtifact(taskableArt);
      BlamEditor.edit(blamOperation);
      if (listener != null) {
         listener.importCompleted();
      }
   }
}
