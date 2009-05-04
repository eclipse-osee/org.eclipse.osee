/*
 * Created on May 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor;

import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkflowMetricsHeader extends Composite implements IFrameworkTransactionEventListener {

   private static final String NAME = "Workflow Metrics: ";
   private final SMAManager smaMgr;
   private Label percentLabel, estHoursLabel, hoursSpentLabel;

   public SMAWorkflowMetricsHeader(Composite parent, XFormToolkit toolkit, SMAManager smaMgr) throws OseeCoreException {
      super(parent, SWT.NONE);
      this.smaMgr = smaMgr;
      try {

         toolkit.adapt(this);
         setLayout(ALayout.getZeroMarginLayout(4, false));
         setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         toolkit.createLabel(this, NAME, SWT.NONE);

         percentLabel = toolkit.createLabel(this, "", SWT.NONE);
         percentLabel.setToolTipText("Calculation: sum of percent for all states (including all tasks and reviews) / # statusable states");
         percentLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         estHoursLabel = toolkit.createLabel(this, "", SWT.NONE);
         estHoursLabel.setToolTipText("Calculation: sum estimated hours for workflow and all tasks and reviews");
         estHoursLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         hoursSpentLabel = toolkit.createLabel(this, "", SWT.NONE);
         hoursSpentLabel.setToolTipText("Calculation: sum of all hours spent for all tasks, reviews and in each state");
         hoursSpentLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         refresh();

         OseeEventManager.addListener(this);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   private void refresh() throws OseeCoreException {
      if (percentLabel.isDisposed()) {
         OseeEventManager.removeListener(this);
         return;
      }
      try {
         if (percentLabel != null && !percentLabel.isDisposed()) percentLabel.setText("Total Percent: " + smaMgr.getSma().getPercentCompleteSMATotal());
         if (estHoursLabel != null && !estHoursLabel.isDisposed()) estHoursLabel.setText("Total Estimated Hours: " + AtsLib.doubleToStrString(smaMgr.getSma().getEstimatedHoursTotal()));
         if (hoursSpentLabel != null && !hoursSpentLabel.isDisposed()) hoursSpentLabel.setText("Total Hours Spent: " + AtsLib.doubleToStrString(smaMgr.getSma().getHoursSpentSMATotal()));
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      percentLabel.update();
      layout();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (smaMgr.isInTransition()) return;
      if (transData.branchId != AtsPlugin.getAtsBranch().getBranchId()) return;
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               refresh();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.widgets.Widget#dispose()
    */
   @Override
   public void dispose() {
      super.dispose();
      OseeEventManager.removeListener(this);
   }

}
