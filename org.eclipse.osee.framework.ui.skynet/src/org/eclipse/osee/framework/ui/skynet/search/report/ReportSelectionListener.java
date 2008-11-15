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
package org.eclipse.osee.framework.ui.skynet.search.report;

import java.util.logging.Level;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Ryan D. Brooks
 */
public class ReportSelectionListener implements SelectionListener {
   private TableViewer tableViewer;

   public ReportSelectionListener(TableViewer tableViewer) {
      super();
      this.tableViewer = tableViewer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
    */
   public void widgetSelected(SelectionEvent ev) {
      ReportJob job = (ReportJob) ((MenuItem) ev.getSource()).getData();
      job.setSelection((IStructuredSelection) tableViewer.getSelection());
      try {
         Jobs.startJob(job);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
    */
   public void widgetDefaultSelected(SelectionEvent e) {
   }
}