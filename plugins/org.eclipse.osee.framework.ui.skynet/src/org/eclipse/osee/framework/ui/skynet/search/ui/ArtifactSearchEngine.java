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

package org.eclipse.osee.framework.ui.skynet.search.ui;

import java.text.MessageFormat;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModelList;
import org.eclipse.search.ui.NewSearchUI;

public class ArtifactSearchEngine {

   public IStatus search(IArtifactSearchResultCollector collector, FilterModelList list) {
      Assert.isNotNull(collector);
      Assert.isNotNull(list);

      IProgressMonitor monitor = collector.getProgressMonitor();

      String message = "Message 1";
      MultiStatus status = new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, message, null);

      int amountOfWork = 100;
      try {
         monitor.beginTask("", amountOfWork); //$NON-NLS-1$
         if (amountOfWork > 0) {
            Integer[] args = new Integer[] {Integer.valueOf(1), Integer.valueOf(amountOfWork)};
            monitor.setTaskName(MessageFormat.format("Scanning:", (Object[]) args));
         }
         collector.aboutToStart();
      } catch (CoreException ex) {
         status.add(ex.getStatus());
      } finally {
         monitor.done();
         try {
            collector.done();
         } catch (CoreException ex) {
            status.add(ex.getStatus());
         }
      }
      return status;
   }
}
