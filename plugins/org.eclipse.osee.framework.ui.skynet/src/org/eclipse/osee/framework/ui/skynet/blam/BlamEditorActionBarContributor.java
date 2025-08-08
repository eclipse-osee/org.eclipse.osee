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

package org.eclipse.osee.framework.ui.skynet.blam;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Roberto E. Escobar
 */
public class BlamEditorActionBarContributor implements IActionContributor {

   private final BlamEditor editor;
   private Action executeBlamAction, executeBlamInDebugAction;

   public BlamEditorActionBarContributor(BlamEditor editor) {
      this.editor = editor;
   }

   @Override
   public void contributeToToolBar(IToolBarManager manager) {
      if (editor.getEditorInput().getBlamOperation().showTopPlayButton()) {
         manager.add(getExecuteBlamAction());
      }
      if (editor.getEditorInput().getBlamOperation().isDebugRunAvailable() && ServiceUtil.accessControlService().isOseeAdmin()) {
         manager.add(getExecuteBlamInDebugAction());
      }
   }

   public final Action getExecuteBlamAction() {
      if (executeBlamAction == null) {
         executeBlamAction = new ExecuteBlamAction();
      }
      return executeBlamAction;
   }

   public final Action getExecuteBlamInDebugAction() {
      if (executeBlamInDebugAction == null) {
         executeBlamInDebugAction = new ExecuteBlamInDebugAction();
      }
      return executeBlamInDebugAction;
   }

   private final class ExecuteBlamAction extends Action {
      public ExecuteBlamAction() {
         super(editor.getButtonText(), IAction.AS_PUSH_BUTTON);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.RUN_EXC));
         setToolTipText(editor.getButtonText());
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

   private final class ExecuteBlamInDebugAction extends Action {
      public ExecuteBlamInDebugAction() {
         super(editor.getButtonText() + " - Debug Mode", IAction.AS_PUSH_BUTTON);
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DEBUG));
         setToolTipText("Executes Blam in Debug Mode (if applicable)");
      }

      @Override
      public void run() {
         try {
            editor.executeBlam(true);
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

}
