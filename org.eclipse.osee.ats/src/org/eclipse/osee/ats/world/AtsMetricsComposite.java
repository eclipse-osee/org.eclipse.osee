/*
 * Created on Nov 2, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xbargraph.XBarGraphLine;
import org.eclipse.osee.framework.ui.skynet.widgets.xbargraph.XBarGraphLineSegment;
import org.eclipse.osee.framework.ui.skynet.widgets.xbargraph.XBarGraphTable;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class AtsMetricsComposite extends ScrolledComposite {

   private Composite toolBarComposite;
   private Composite metricsComposite;
   private final IAtsMetricsProvider iAtsMetricsProvider;
   private final Color BACKGROUND_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
   private final Color FOREGROUND_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
   private final Composite mainComp;
   private boolean refreshedOnce = true;

   /**
    * @param iAtsMetricsProvider
    * @param parent
    * @param style
    */
   public AtsMetricsComposite(IAtsMetricsProvider iAtsMetricsProvider, Composite parent, int style) {
      super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
      this.iAtsMetricsProvider = iAtsMetricsProvider;

      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      mainComp = new Composite(this, SWT.NONE);
      mainComp.setLayout(new GridLayout());
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      adapt(mainComp);

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(mainComp)) {
         return;
      }

      adapt(this);
      creatToolBar(mainComp);

      setContent(mainComp);
      setExpandHorizontal(true);
      setExpandVertical(true);
      layout();
   }

   private void creatToolBar(Composite composite) {
      toolBarComposite = new Composite(composite, SWT.NONE);
      toolBarComposite.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, false, 1, 1));
      toolBarComposite.setLayout(new GridLayout(2, false));
      adapt(toolBarComposite);

      Button refresh = new Button(toolBarComposite, SWT.PUSH);
      refresh.setText("Display/Refresh Metrics");
      refresh.setToolTipText("Recalculate and Update Metrics");
      refresh.addSelectionListener(new SelectionAdapter() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               handleUpdateMetrics();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });
      adapt(refresh);

      if (!refreshedOnce) {
         Label label = new Label(toolBarComposite, SWT.NONE);
         label.setText("        Last Updated: " + XDate.getDateNow(XDate.MMDDYYHHMM));
         adapt(label);
      }

   }

   public void handleUpdateMetrics() throws OseeCoreException {
      refreshedOnce = false;
      if (metricsComposite != null) {
         metricsComposite.dispose();
      }
      metricsComposite = new Composite(mainComp, SWT.NONE);
      metricsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
      metricsComposite.setLayout(ALayout.getZeroMarginLayout(1, true));
      adapt(metricsComposite);

      addSpace();
      SMAMetrics sMet =
            new SMAMetrics(iAtsMetricsProvider.getMetricsArtifacts(), iAtsMetricsProvider.getMetricsVersionArtifact());
      createOverviewChart(sMet, metricsComposite);
      addSpace();
      createCompletedByAssigneesChart(sMet, metricsComposite);
      addSpace();
      createHoursRemainingByAssigneesChart(sMet, metricsComposite);

      mainComp.layout();
      computeScrollSize();
   }

   public void computeScrollSize() {
      this.computeScrollSize(mainComp);
   }

   private void computeScrollSize(Composite viewableArea) {
      this.setMinSize(viewableArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
   }

   private void addSpace() {
      Label label = new Label(metricsComposite, SWT.NONE);
      label.setText(" ");
      adapt(label);
   }

   public void createOverviewChart(SMAMetrics sMet, Composite parent) throws OseeCoreException {
      List<XBarGraphLine> lines = new ArrayList<XBarGraphLine>();

      List<XBarGraphLineSegment> segments = new ArrayList<XBarGraphLineSegment>();
      int totalObjects = sMet.getNumActions() + sMet.getNumTeamWfs() + sMet.getNumTasks() + sMet.getNumReviews();
      if (sMet.getNumActions() > 0) {
         double percent = new Double(sMet.getNumActions()) / totalObjects * 100;
         segments.add(new XBarGraphLineSegment("Actions", SWT.COLOR_DARK_YELLOW, Math.round(percent)));
      }
      if (sMet.getNumTeamWfs() > 0) {
         double percent = new Double(sMet.getNumTeamWfs()) / totalObjects * 100;
         segments.add(new XBarGraphLineSegment("Workflows", SWT.COLOR_GREEN, Math.round(percent)));
      }
      if (sMet.getNumTasks() > 0) {
         double percent = new Double(sMet.getNumTasks()) / totalObjects * 100;
         segments.add(new XBarGraphLineSegment("Tasks", SWT.COLOR_CYAN, Math.round(percent)));
      }
      if (sMet.getNumReviews() > 0) {
         double percent = new Double(sMet.getNumReviews()) / totalObjects * 100;
         segments.add(new XBarGraphLineSegment("Reviews", SWT.COLOR_MAGENTA, Math.round(percent)));
      }
      lines.add(new XBarGraphLine(sMet.toStringObjectBreakout(), segments));

      lines.add(XBarGraphLine.getPercentLine(
            "By Team Percents (" + sMet.getCummulativeTeamPercentComplete() + "/" + sMet.getNumTeamWfs() + ")",
            (int) sMet.getPercentCompleteByTeamPercents()));
      lines.add(XBarGraphLine.getPercentLine(
            "By Team Workflow (" + sMet.getCompletedTeamWorkflows().size() + "/" + sMet.getNumTeamWfs() + ")",
            (int) sMet.getPercentCompleteByTeamWorkflow()));
      lines.add(XBarGraphLine.getPercentLine(
            "By Task Percents (" + sMet.getCummulativeTaskPercentComplete() + "/" + sMet.getNumTasks() + ")",
            (int) sMet.getPercentCompleteByTaskPercents()));
      lines.add(XBarGraphLine.getPercentLine(
            "By Task (" + sMet.getCompletedTaskWorkflows().size() + "/" + sMet.getNumTasks() + ")",
            (int) sMet.getPercentCompleteByTaskWorkflow()));

      lines.add(XBarGraphLine.getTextLine("Estimated Hours: ", String.format("%5.2f", sMet.getEstHours())));
      lines.add(XBarGraphLine.getTextLine("Remaining Hours: ", String.format("%5.2f", sMet.getHrsRemain())));
      lines.add(XBarGraphLine.getTextLine("Hours Spent: ", String.format("%5.2f", sMet.getHrsSpent())));
      lines.add(XBarGraphLine.getTextLine("Man Days Needed: ", String.format("%5.2f", sMet.getManDaysNeeded())));

      try {
         if (iAtsMetricsProvider.getMetricsVersionArtifact() != null) {
            lines.add(new XBarGraphLine("Targeted Version", 0,
                  iAtsMetricsProvider.getMetricsVersionArtifact().toString()));
            lines.add(new XBarGraphLine("Estimated Release Date", 0,
                  iAtsMetricsProvider.getMetricsVersionArtifact().getSoleAttributeValueAsString(
                        ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(), "Not Set")));
            double hoursTillRelease = sMet.getHoursTillRel();
            double hoursRemaining = sMet.getHrsRemain();
            int percent = 0;
            if (hoursTillRelease != 0) {
               percent = (int) (hoursRemaining / hoursTillRelease);
            }
            if (sMet.getEstRelDate() == null) {
               lines.add(new XBarGraphLine("Release Effort Remaining", 0, "Estimated Release Date Not Set"));
            } else if (percent == 0 || hoursRemaining > hoursTillRelease) {
               lines.add(new XBarGraphLine("Release Effort Remaining", XBarGraphLine.DEFAULT_RED_FOREGROUND,
                     XBarGraphLine.DEFAULT_RED_BACKGROUND, 100, String.format(
                           "%5.2f hours exceeds remaining release hours %5.2f", hoursRemaining, hoursTillRelease)));
            } else {
               lines.add(new XBarGraphLine("Release Effort Remaining", XBarGraphLine.DEFAULT_GREEN_FOREGROUND,
                     XBarGraphLine.DEFAULT_GREEN_BACKGROUND, SWT.COLOR_WHITE, SWT.COLOR_WHITE, percent, String.format(
                           "%5.2f remaining work hours", hoursRemaining), String.format(
                           "%5.2f release remaining hours", hoursRemaining)));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      XBarGraphTable table = new XBarGraphTable("Overview", "", "", lines);
      table.setHeaderVisible(false);
      table.setFillHorizontally(true);
      table.createWidgets(parent, 1);
      adapt(table);
   }

   public void createCompletedByAssigneesChart(SMAMetrics sMet, Composite parent) {
      List<XBarGraphLine> lines = new ArrayList<XBarGraphLine>();
      for (User user : sMet.getAssigneesAssignedOrCompleted()) {
         int completed =
               sMet.getUserToCompletedSmas().containsKey(user) ? sMet.getUserToCompletedSmas().getValues(user).size() : 0;
         int inWork =
               sMet.getUserToAssignedSmas().containsKey(user) ? sMet.getUserToAssignedSmas().getValues(user).size() : 0;
         int total = completed + inWork;
         int percentComplete = 0;
         if (completed == total) {
            percentComplete = 100;
         } else if (completed != 0 && total != 0) {
            double percent = new Double(completed) / total * 100.0;
            percentComplete = (int) percent;
         }
         lines.add(XBarGraphLine.getPercentLine(user.getName() + " (" + completed + "/" + total + ")", percentComplete));
      }
      XBarGraphTable table =
            new XBarGraphTable("Completed by Assignee per Assigned Workflow", "User", "Percent Complete", lines);
      table.setFillHorizontally(true);
      table.createWidgets(parent, 1);
      adapt(table);
   }

   public void createHoursRemainingByAssigneesChart(SMAMetrics sMet, Composite parent) throws OseeCoreException {
      List<XBarGraphLine> lines = new ArrayList<XBarGraphLine>();
      Double versionHoursRemain = null;
      if (iAtsMetricsProvider.getMetricsVersionArtifact() != null) {
         versionHoursRemain = sMet.getHoursTillRel();
      }
      for (User user : sMet.getAssigneesAssignedOrCompleted()) {
         try {
            double userHoursRemain = 0;
            for (TeamWorkFlowArtifact team : sMet.getTeamArts()) {
               Collection<User> users = team.getSmaMgr().getStateMgr().getAssignees();
               if (users.contains(user)) {
                  double hours = team.getRemainHoursTotal();
                  if (hours > 0) {
                     userHoursRemain += hours / users.size();
                  }
               }
            }
            if (sMet.getEstRelDate() == null) {
               lines.add(new XBarGraphLine(user.getName(), 0, "Estimated Release Date Not Set"));
            } else if (versionHoursRemain == null) {
               lines.add(new XBarGraphLine(user.getName(), (int) userHoursRemain, String.format("%5.2f",
                     userHoursRemain)));
            } else {
               lines.add(new XBarGraphLine(user.getName(), XBarGraphLine.DEFAULT_RED_FOREGROUND,
                     XBarGraphLine.DEFAULT_RED_BACKGROUND, (int) userHoursRemain, String.format(
                           "%5.2f - Exceeds release remaining hours %5.2f.", userHoursRemain, versionHoursRemain)));
            }
         } catch (OseeCoreException ex) {
            lines.add(new XBarGraphLine(user.getName(), 0, "Exception: " + ex.getLocalizedMessage()));
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      XBarGraphTable table = new XBarGraphTable("Hours Remaining by Assignee", "User", "Hours Remaining", lines);
      table.setFillHorizontally(true);
      table.createWidgets(parent, 1);
      adapt(table);
   }

   public void disposeComposite() {
   }

   public void adapt(XWidget xWidget) {
      adapt(xWidget.getControl());
      adapt(xWidget.getLabelWidget());
   }

   public void adapt(Control control) {
      if (control == null) return;
      control.setBackground(BACKGROUND_COLOR);
      control.setForeground(FOREGROUND_COLOR);
   }

   public void adapt(Composite composite) {
      composite.setBackground(BACKGROUND_COLOR);
   }

}
