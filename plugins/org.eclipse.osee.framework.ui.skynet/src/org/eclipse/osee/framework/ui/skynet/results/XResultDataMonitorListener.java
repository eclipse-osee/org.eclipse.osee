/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.ui.skynet.results;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.result.IResultDataListener;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Added to XResultData if desire results to be output to monitor
 * 
 * @author Donald G. Dunne
 */
public class XResultDataMonitorListener implements IResultDataListener {

   private final IProgressMonitor monitor;

   public XResultDataMonitorListener(final IProgressMonitor monitor) {
      this.monitor = monitor;
   }

   @Override
   public void log(XResultData.Type type, final String str) {
      if (monitor != null) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               monitor.subTask(str);
            }
         });
      }

   }

}
