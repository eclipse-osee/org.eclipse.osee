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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchViewPresentationPreferences;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

/**
 * @author Jeff C. Phillips
 */
public class ShowArchivedBranchHandler extends AbstractHandler implements IElementUpdater {
   public static String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.branch.BranchView.showArchivedBranches";
   boolean itemChk;
   private ICommandService service;

   public ShowArchivedBranchHandler(){
      this.service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
   }
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      ((BranchView) HandlerUtil.getActivePartChecked(event)).changeArchivedBranchPresentation(!itemChk);
      return null;
   }

   @Override
   public void updateElement(UIElement element, Map parameters) {
      itemChk = Platform.getPreferencesService().getRootNode().node(InstanceScope.SCOPE).node(BranchView.VIEW_ID).getBoolean(BranchViewPresentationPreferences.SHOW_ARCHIVED_BRANCHES, false);
      element.setChecked(itemChk);
   }

   @Override
   public boolean isEnabled() {
      boolean isValid = false;
      service.refreshElements(COMMAND_ID, null);
      
      try {
         isValid = AccessControlManager.isOseeAdmin();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      
      return isValid;
   }
}
