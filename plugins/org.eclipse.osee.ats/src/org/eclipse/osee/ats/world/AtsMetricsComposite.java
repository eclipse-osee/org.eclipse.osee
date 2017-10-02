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
package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.WorkflowMetrics;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xbargraph.XBarGraphLine;
import org.eclipse.osee.framework.ui.skynet.widgets.xbargraph.XBarGraphTable;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
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
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class AtsMetricsComposite extends ScrolledComposite {

   private Composite metricsComposite;
   private final IAtsMetricsProvider iAtsMetricsProvider;
   private final Color BACKGROUND_COLOR = Displays.getSystemColor(SWT.COLOR_WHITE);
   private final Color FOREGROUND_COLOR = Displays.getSystemColor(SWT.COLOR_BLUE);
   private final Composite mainComp;
   private boolean refreshedOnce = true;
   private XDate estimatedReleaseXDate;

   public AtsMetricsComposite(IAtsMetricsProvider iAtsMetricsProvider, Composite parent, int style) {
      super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
      this.iAtsMetricsProvider = iAtsMetricsProvider;

      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      mainComp = new Composite(this, SWT.NONE);
      mainComp.setLayout(new GridLayout());
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      adapt(mainComp);

      if (DbConnectionExceptionComposite.dbConnectionIsOk(mainComp)) {

         adapt(this);
         creatToolBar(mainComp);

         setContent(mainComp);
         setExpandHorizontal(true);
         setExpandVertical(true);
         layout();
      }
   }

   private void creatToolBar(Composite composite) {
      Composite toolBarComposite = new Composite(composite, SWT.NONE);
      toolBarComposite.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, false, 1, 1));
      toolBarComposite.setLayout(new GridLayout(4, false));
      adapt(toolBarComposite);

      Button refresh = new Button(toolBarComposite, SWT.PUSH);
      refresh.setText("Display/Refresh Metrics");
      refresh.setToolTipText("Recalculate and Update Metrics");
      refresh.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               handleUpdateMetrics();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
      adapt(refresh);

      estimatedReleaseXDate = new XDate("Estimated Release Date - Override");
      estimatedReleaseXDate.createWidgets(toolBarComposite, 2);
      adapt(estimatedReleaseXDate.getLabelWidget());

      if (!refreshedOnce) {
         Label label = new Label(toolBarComposite, SWT.NONE);
         label.setText("        Last Updated: " + DateUtil.getMMDDYYHHMM());
         adapt(label);
      }

   }

   public void handleUpdateMetrics()  {
      refreshedOnce = false;
      if (metricsComposite != null) {
         metricsComposite.dispose();
      }
      metricsComposite = new Composite(mainComp, SWT.NONE);
      metricsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
      metricsComposite.setLayout(ALayout.getZeroMarginLayout(1, true));
      adapt(metricsComposite);

      addSpace();

      try {
         if (estimatedReleaseXDate.getDate() == null && iAtsMetricsProvider.getMetricsVersion() != null && iAtsMetricsProvider.getMetricsVersion().getEstimatedReleaseDate() != null) {
            estimatedReleaseXDate.setDate(iAtsMetricsProvider.getMetricsVersion().getEstimatedReleaseDate());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      WorkflowMetrics sMet =
         new WorkflowMetrics(iAtsMetricsProvider.getMetricsWorkItems(), iAtsMetricsProvider.getMetricsVersion(),
            iAtsMetricsProvider.getManHoursPerDayPreference(), getEstimatedReleaseDate());
      createOverviewChart(sMet, metricsComposite);
      addSpace();
      createHoursRemainingByAssigneesChart(sMet, metricsComposite);
      addSpace();
      createCompletedByAssigneesChart(sMet, metricsComposite);

      mainComp.layout();
      computeScrollSize();
   }

   private Date getEstimatedReleaseDate()  {
      if (estimatedReleaseXDate != null && estimatedReleaseXDate.getDate() != null) {
         return estimatedReleaseXDate.getDate();
      }
      if (iAtsMetricsProvider.getMetricsVersion() != null) {
         return iAtsMetricsProvider.getMetricsVersion().getEstimatedReleaseDate();
      }
      // Try to find an estimated release date from one of the workflows
      for (Artifact artifact : iAtsMetricsProvider.getMetricsWorkItems()) {
         if (artifact instanceof IAtsTeamWorkflow) {
            IAtsVersion verArt =
               AtsClientService.get().getVersionService().getTargetedVersion((IAtsTeamWorkflow) artifact);
            if (verArt != null) {
               Date estRelDate = verArt.getEstimatedReleaseDate();
               if (estRelDate != null) {
                  return estRelDate;
               }
            }
         }
      }
      return null;
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

   public void createOverviewChart(WorkflowMetrics sMet, Composite parent)  {
      List<XBarGraphLine> lines = new ArrayList<>();

      lines.add(XBarGraphLine.getTextLine("Loaded", sMet.toStringObjectBreakout()));
      lines.add(XBarGraphLine.getTextLineRedIfTrue("Workflows",
         String.format("Estimates off %d workflows with %d having 0 estimates.", sMet.getNumSMAs(),
            sMet.getNumNotEstimated()),
         sMet.getNumNotEstimated() > 0));

      lines.add(XBarGraphLine.getPercentLine(
         "By Workflow Percents (" + sMet.getCummulativeWorkflowPercentComplete() + "/" + sMet.getNumSMAs() + ")",
         (int) sMet.getPercentCompleteByWorkflowPercents()));
      lines.add(XBarGraphLine.getPercentLine(
         "By Number of Workflows (" + sMet.getCompletedWorkflows().size() + "/" + sMet.getNumSMAs() + ")",
         (int) sMet.getPercentCompleteByWorkflow()));

      lines.add(XBarGraphLine.getTextLine("Estimated Hours: ", String.format("%5.2f Hours", sMet.getEstHours())));
      lines.add(XBarGraphLine.getTextLine("Remaining Hours: ",
         String.format("%5.2f Hours = (Estimated hours %5.2f - (Estimated hours %5.2f x Percent Complete %5.2f))",
            sMet.getHrsRemainFromEstimates(), sMet.getEstHours(), sMet.getEstHours(),
            sMet.getPercentCompleteByWorkflowPercents())));
      lines.add(XBarGraphLine.getTextLine("Hours Spent: ", String.format("%5.2f Hours", sMet.getHrsSpent())));
      lines.add(XBarGraphLine.getTextLine("Hours Per Man Day Preference: ",
         String.format("%5.2f Hours per Day", sMet.getHoursPerManDay())));
      lines.add(XBarGraphLine.getTextLine("Work Days Needed: ",
         String.format("%5.2f Days = Remaining Hours %5.2f / Hours Per Day of %5.2f", sMet.getManDaysNeeded(),
            sMet.getHrsRemainFromEstimates(), sMet.getHoursPerManDay())));

      try {
         lines.add(new XBarGraphLine("Targeted Version", 0,
            iAtsMetricsProvider.getMetricsVersion() == null ? "Not Set" : iAtsMetricsProvider.getMetricsVersion().getName()));
         String estimatedReleaseDateStr =
            iAtsMetricsProvider.getMetricsVersion() == null || iAtsMetricsProvider.getMetricsVersion().getEstimatedReleaseDate() == null ? "Not Set" : iAtsMetricsProvider.getMetricsVersion().getEstimatedReleaseDate().toString();
         lines.add(new XBarGraphLine("Targeted Version - Estimated Release Date", 0,
            iAtsMetricsProvider.getMetricsVersion() == null ? "Not Set" : estimatedReleaseDateStr));
         lines.add(new XBarGraphLine("Metrics Estimated Release Date", 0,
            getEstimatedReleaseDate() == null ? "Not Set" : getEstimatedReleaseDate().toString()));
         lines.add(new XBarGraphLine("Work-Days Till Release", 0,
            getEstimatedReleaseDate() == null ? "Estimated Release Date Not Set" : sMet.getDaysTillRelStr()));
         double hoursTillRelease = sMet.getHoursTillRel();
         lines.add(new XBarGraphLine("Hours Till Release", 0,
            getEstimatedReleaseDate() == null ? "Estimated Release Date Not Set" : sMet.getHoursTillRelStr()));
         double hoursRemainingFromEstimates = sMet.getHrsRemainFromEstimates();
         int percent = 0;
         if (hoursTillRelease != 0) {
            percent = (int) (hoursRemainingFromEstimates / hoursTillRelease);
         }
         if (sMet.getEstRelDate() == null) {
            lines.add(new XBarGraphLine("Release Effort Remaining", 0, "Estimated Release Date Not Set"));
         } else if (hoursRemainingFromEstimates > hoursTillRelease) {
            lines.add(new XBarGraphLine("Release Effort Remaining", XBarGraphLine.DEFAULT_RED_FOREGROUND,
               XBarGraphLine.DEFAULT_RED_BACKGROUND, 100,
               String.format("%5.2f hours exceeds remaining release hours %5.2f;  Over by %5.2f hours.",
                  hoursRemainingFromEstimates, hoursTillRelease, hoursRemainingFromEstimates - hoursTillRelease)));
         } else {
            lines.add(new XBarGraphLine("Release Effort Remaining", XBarGraphLine.DEFAULT_GREEN_FOREGROUND,
               XBarGraphLine.DEFAULT_GREEN_BACKGROUND, SWT.COLOR_WHITE, SWT.COLOR_WHITE, percent,
               String.format("%5.2f remaining work hours", hoursRemainingFromEstimates),
               String.format("%5.2f release remaining hours", hoursRemainingFromEstimates)));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      XBarGraphTable table = new XBarGraphTable("Overview", "Item", "Value", lines);
      table.setFillHorizontally(true);
      table.createWidgets(parent, 1);
      adapt(table);
   }

   public void createCompletedByAssigneesChart(WorkflowMetrics sMet, Composite parent) {
      List<XBarGraphLine> lines = new ArrayList<>();
      for (IAtsUser user : sMet.getAssigneesAssignedOrCompleted()) {
         try {
            int numCompleted = sMet.getUserToCompletedSmas(user).size();
            double cummulativePercentComplete = numCompleted * 100;
            int numInWork = sMet.getUserToAssignedSmas(user).size();
            // Since table is loaded with arts and also shows children, don't want to count artifacts twice
            Set<Artifact> processedArts = new HashSet<>();
            if (sMet.getUserToAssignedSmas().getValues(user) != null) {
               for (Artifact awa : sMet.getUserToAssignedSmas().getValues(user)) {
                  if (!processedArts.contains(awa) && !sMet.getUserToCompletedSmas().containsValue(awa)) {
                     cummulativePercentComplete += PercentCompleteTotalUtil.getPercentCompleteTotal((IAtsWorkItem) awa,
                        AtsClientService.get().getServices());
                     processedArts.add(awa);
                  }
               }
            }
            int numTotal = numCompleted + numInWork;
            int percentCompleteByNumber = 0;
            if (numCompleted == numTotal) {
               percentCompleteByNumber = 100;
            } else if (numCompleted != 0 && numTotal != 0) {
               double percent = (double) numCompleted / (double) numTotal * 100.0;
               percentCompleteByNumber = (int) percent;
            }
            int percentCompleteByPercents = 0;
            if (cummulativePercentComplete == 0) {
               percentCompleteByPercents = 0;

            } else if (numTotal == 0) {
               percentCompleteByPercents = 100;
            } else {
               double percent = cummulativePercentComplete / numTotal;
               percentCompleteByPercents = (int) percent;
            }
            lines.add(XBarGraphLine.getPercentLineBlueGreen(
               user.getName() + " by Percents (" + cummulativePercentComplete + "/" + numTotal + ")",
               percentCompleteByPercents));
            lines.add(XBarGraphLine.getPercentLineBlueGreen(
               user.getName() + " by Number of Workflows (" + numCompleted + "/" + numTotal + ")",
               percentCompleteByNumber));
         } catch (Exception ex) {
            lines.add(XBarGraphLine.getTextLine(user.getName(), "Exception: " + ex.getLocalizedMessage()));
         }
      }
      XBarGraphTable table = new XBarGraphTable("Completed by Assignee per Assigned Workflow (Team, Task and Review)",
         "User", "Percent Complete", lines);
      table.setFillHorizontally(true);
      table.createWidgets(parent, 1);
      adapt(table);
   }

   public void createHoursRemainingByAssigneesChart(WorkflowMetrics sMet, Composite parent) {
      List<XBarGraphLine> lines = new ArrayList<>();
      Double hoursRemain = null;
      if (sMet.getEstRelDate() != null) {
         hoursRemain = sMet.getHoursTillRel();
      }
      for (IAtsUser user : sMet.getAssigneesAssignedOrCompleted()) {
         try {
            double userHoursRemain = 0;
            for (TeamWorkFlowArtifact team : sMet.getTeamArts()) {
               Collection<IAtsUser> users = new HashSet<>();
               users.addAll(team.getStateMgr().getAssignees());
               if (users.contains(user)) {
                  double hours = team.getRemainHoursTotal();
                  if (hours > 0) {
                     userHoursRemain += hours / users.size();
                  }
               }
            }
            if (sMet.getEstRelDate() == null) {
               lines.add(new XBarGraphLine(user.getName(), (int) userHoursRemain,
                  String.format("%5.2f - (Estimated release date not set)", userHoursRemain)));
            } else if (hoursRemain == null) {
               lines.add(
                  new XBarGraphLine(user.getName(), (int) userHoursRemain, String.format("%5.2f", userHoursRemain)));
            } else {
               if (userHoursRemain == 0.0) {
                  lines.add(new XBarGraphLine(user.getName(), XBarGraphLine.DEFAULT_GREEN_FOREGROUND,
                     XBarGraphLine.DEFAULT_GREEN_BACKGROUND, 100, "No Estimated Hours Remain"));
               } else if (userHoursRemain > hoursRemain) {
                  lines.add(new XBarGraphLine(user.getName(), XBarGraphLine.DEFAULT_RED_FOREGROUND,
                     XBarGraphLine.DEFAULT_RED_BACKGROUND, (int) userHoursRemain > 1 ? (int) userHoursRemain : 1,
                     String.format("%5.2f - Exceeds release remaining hours %5.2f.", userHoursRemain, hoursRemain)));
               } else {
                  lines.add(new XBarGraphLine(user.getName(), XBarGraphLine.DEFAULT_GREEN_FOREGROUND,
                     XBarGraphLine.DEFAULT_GREEN_BACKGROUND, (int) userHoursRemain,
                     String.format("%5.2f - Within remaining hours %5.2f.", userHoursRemain, hoursRemain)));
               }
            }
         } catch (OseeCoreException ex) {
            lines.add(new XBarGraphLine(user.getName(), 0, "Exception: " + ex.getLocalizedMessage()));
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      XBarGraphTable table = new XBarGraphTable(
         "Hours Remaining by Assignee (green = within remaining hours; red = exceeds remaining hours till release)",
         "User", "Hours Remaining", lines);
      table.setFillHorizontally(true);
      table.createWidgets(parent, 1);
      adapt(table);
   }

   public void adapt(XWidget xWidget) {
      adapt(xWidget.getControl());
      adapt(xWidget.getLabelWidget());
   }

   public void adapt(Control control) {
      if (control == null) {
         return;
      }
      control.setBackground(BACKGROUND_COLOR);
      control.setForeground(FOREGROUND_COLOR);
   }

   public void adapt(Composite composite) {
      composite.setBackground(BACKGROUND_COLOR);
   }

}
