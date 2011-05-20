/*
 * Created on May 20, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.util.IResultDataListener;
import org.eclipse.osee.framework.core.util.XResultData;
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
