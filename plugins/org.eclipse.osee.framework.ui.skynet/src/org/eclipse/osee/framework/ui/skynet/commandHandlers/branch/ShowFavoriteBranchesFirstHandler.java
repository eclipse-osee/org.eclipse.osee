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
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
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
public class ShowFavoriteBranchesFirstHandler extends AbstractHandler implements IElementUpdater {
   public static String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.branch.BranchView.ShowFavoriteBranchesFirst";
   boolean itemChk;
   private ICommandService service;

   public ShowFavoriteBranchesFirstHandler(){
      this.service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
   }
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      ((BranchView) HandlerUtil.getActivePartChecked(event)).changeFavoritesFirstPresentation(!itemChk);
      return null;
   }

   @Override
   public void updateElement(UIElement element, Map parameters) {
      itemChk = Platform.getPreferencesService().getRootNode().node(InstanceScope.SCOPE).node(BranchView.VIEW_ID).getBoolean(BranchViewPresentationPreferences.FAVORITE_KEY, false);
      element.setChecked(itemChk);
   }

   @Override
   public boolean isEnabled() {
      service.refreshElements(COMMAND_ID, null);
      return true;
   }
}
