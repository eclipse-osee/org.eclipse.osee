/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.ui.skynet.commandHandlers.change;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.change.WordChangeUtil;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;

/**
 * @author Branden W. Phillips
 */
public class ViewBranchContentChangeReportHandler extends CommandHandler {

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      List<Change> localChanges = Handlers.getArtifactChangesFromStructuredSelection(selection);
      WordChangeUtil.generateWordTemplateChangeReport(localChanges, PresentationType.DIFF_NO_ATTRIBUTES, true, false);
      return null;
   }

   @Override
   public boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      List<Change> localChanges = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);
      return !localChanges.isEmpty();
   }

}