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
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchOptionsEnum;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

/**
 * @author Jeff C. Phillips
 */
public class ShowArchivedBranchHandler extends CommandHandler implements IElementUpdater {
   public static final String COMMAND_ID =
      "org.eclipse.osee.framework.ui.skynet.branch.BranchView.showArchivedBranches";

   private final ICommandService service;
   private boolean itemChk;

   public ShowArchivedBranchHandler() {
      this.service = PlatformUI.getWorkbench().getService(ICommandService.class);
   }

   @Override
   protected Object executeWithException(ExecutionEvent event, IStructuredSelection selection)  {
      try {
         IWorkbenchPart part = HandlerUtil.getActivePartChecked(event);
         if (part instanceof BranchView) {
            BranchView branchView = (BranchView) part;
            branchView.changePresentation(BranchOptionsEnum.SHOW_ARCHIVED_BRANCHES, !itemChk);
         }
      } catch (ExecutionException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return null;
   }

   @SuppressWarnings("rawtypes")
   @Override
   public void updateElement(UIElement element, Map parameters) {
      itemChk =
         Platform.getPreferencesService().getRootNode().node(InstanceScope.SCOPE).node(BranchView.VIEW_ID).getBoolean(
            BranchOptionsEnum.SHOW_ARCHIVED_BRANCHES.origKeyName, false);
      element.setChecked(itemChk);
   }

   @Override
   protected boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      boolean isValid = false;
      if (!DbUtil.isDbInit() && DbConnectionUtility.isApplicationServerAlive()) {
         service.refreshElements(COMMAND_ID, null);
         try {
            isValid = AccessControlManager.isOseeAdmin();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return isValid;
   }

}
