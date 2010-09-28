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

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.artifact.AbstractTaskableArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.operation.ImportTasksFromSpreadsheet;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class ImportTasksViaSpreadsheet extends Action {

   private final AbstractTaskableArtifact taskableArt;
   private final Listener listener;

   public ImportTasksViaSpreadsheet(AbstractTaskableArtifact taskableArt, Listener listener) {
      super();
      this.taskableArt = taskableArt;
      this.listener = listener;
      setText("Import Tasks via spreadsheet");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.IMPORT));
   }

   @Override
   public void run() {
      try {
         ImportTasksFromSpreadsheet blamOperation = new ImportTasksFromSpreadsheet();
         blamOperation.setTaskableStateMachineArtifact(taskableArt);
         BlamEditor.edit(blamOperation);
         if (listener != null) {
            listener.notifyAll();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
