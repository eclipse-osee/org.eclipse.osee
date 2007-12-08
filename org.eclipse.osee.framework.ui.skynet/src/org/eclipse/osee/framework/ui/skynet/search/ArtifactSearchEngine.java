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
package org.eclipse.osee.framework.ui.skynet.search;

import java.text.MessageFormat;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterModelList;
import org.eclipse.osee.framework.ui.skynet.search.ui.IArtifactSearchResultCollector;
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
            Integer[] args = new Integer[] {new Integer(1), new Integer(amountOfWork)};
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
