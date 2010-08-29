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
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.GoalArtifact;
import org.eclipse.osee.ats.config.AtsBulkLoad;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class NewGoal extends Action {

   public NewGoal() {
      super("Create New Goal");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GOAL_NEW));
      setToolTipText("Create New Goal");
   }

   @Override
   public void run() {
      super.run();
      AtsBulkLoad.loadConfig(true);
      try {
         EntryDialog ed = new EntryDialog("New Goal", "Enter Title");
         if (ed.open() == 0) {
            String title = ed.getEntry();
            GoalArtifact goalArt = GoalArtifact.createGoal(title);
            goalArt.persist();
            SMAEditor.editArtifact(goalArt);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}