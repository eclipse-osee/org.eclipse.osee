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

package org.eclipse.osee.framework.ui.skynet.blam;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.ui.IEditorSite;

/**
 * @author Roberto E. Escobar
 */
public class BlamEditorActionBarContributor implements IActionContributor {

   private final BlamEditor editor;
   private Action executeBlamAction;
   private Action bugAction;

   public BlamEditorActionBarContributor(BlamEditor editor) {
      this.editor = editor;
   }

   @Override
   public void contributeToToolBar(IToolBarManager manager) {
      manager.add(getExecuteBlamAction());
      manager.add(getAtsBugAction());
   }

   //   OseeAts.addButtonToEditorToolBar(editor, this, SkynetGuiPlugin.getInstance(), form.getToolBarManager(),
   //         BlamEditor.EDITOR_ID, "BLAM Editor");
   public final Action getAtsBugAction() {
      if (bugAction == null) {
         IEditorSite site = editor.getEditorSite();
         bugAction =
               OseeAts.createBugAction(SkynetGuiPlugin.getInstance(), editor, site.getId(), site.getRegisteredName());
      }
      return bugAction;
   }

   public final Action getExecuteBlamAction() {
      if (executeBlamAction == null) {
         executeBlamAction = new ExecuteBlamAction();
      }
      return executeBlamAction;
   }

   private final class ExecuteBlamAction extends Action {
      public ExecuteBlamAction() {
         super("Run BLAM in Job", Action.AS_PUSH_BUTTON);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.RUN_EXC));
         setToolTipText("Executes the BLAM Operation");
      }

      @Override
      public void run() {
         try {
            editor.executeBlam();
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}
