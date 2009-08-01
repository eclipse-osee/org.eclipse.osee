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
package org.eclipse.osee.define.traceability.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.define.traceability.jobs.FindTraceUnitJob;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class FindTraceUnitActionDelegate implements IWorkbenchWindowActionDelegate {

   @Override
   public void dispose() {
   }

   @Override
   public void init(IWorkbenchWindow window) {
   }

   @Override
   public void run(IAction action) {
      final String jobName = "Resource To Trace Unit Artifact";
      final List<IResource> resources = getSelectedItems();
      Jobs.startJob(new FindTraceUnitJob(jobName, resources.toArray(new IResource[resources.size()])), true);
   }

   @Override
   public void selectionChanged(IAction action, ISelection selection) {
   }

   private List<IResource> getSelectedItems() {
      ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
      if (selection instanceof StructuredSelection) {
         List<IResource> selectedItems =
               Handlers.processSelectionObjects(IResource.class, (StructuredSelection) selection);
         List<IResource> toReturn = new ArrayList<IResource>();
         for (IResource resource : selectedItems) {
            if (resource instanceof IFile) {
               toReturn.add(resource);
            }
         }
         return toReturn;
      }
      return Collections.emptyList();
   }
}
