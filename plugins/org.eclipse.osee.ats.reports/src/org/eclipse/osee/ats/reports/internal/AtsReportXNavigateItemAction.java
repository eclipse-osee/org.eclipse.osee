/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.reports.internal;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.ats.reports.AtsReport;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Roberto E. Escobar
 */
public class AtsReportXNavigateItemAction<IN, OUT> extends XNavigateItemAction {

   private final AtsReport<IN, OUT> atsReport;

   public AtsReportXNavigateItemAction(final XNavigateItem parent, AtsReport<IN, OUT> atsReport) {
      super(parent, atsReport.getName(), atsReport.getKeyedImage());
      this.atsReport = atsReport;
   }

   @Override
   public void run(final TableLoadOption... tableLoadOptions) throws Exception {
      IN input = atsReport.getInputParameters();
      if (input != null) {
         final OUT output = atsReport.createOutputParameters();
         IOperation operation = atsReport.createReportOperation(input, output, tableLoadOptions);
         Operations.executeAsJob(operation, true, Job.LONG, new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               super.done(event);
               try {
                  atsReport.displayResults(output);
               } catch (OseeCoreException ex) {
                  OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP, ex, "Error displaying [%s]",
                     atsReport.getName());
               }
            }
         });
      }
   }
}
