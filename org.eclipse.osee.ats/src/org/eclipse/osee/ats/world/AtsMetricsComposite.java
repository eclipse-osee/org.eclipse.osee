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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xbargraph.XBarGraphLine;
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
   private XDate estimatedReleaseXDate;

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
      toolBarComposite.setLayout(new GridLayout(4, false));
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

      estimatedReleaseXDate = new XDate("Estimated Release Date - Override");
      estimatedReleaseXDate.createWidgets(toolBarComposite, 2);
      adapt(estimatedReleaseXDate.getLabelWidget());

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

      try {
         if (estimatedReleaseXDate.getDate() == null && iAtsMetricsProvider.getMetricsVersionArtifact() != null && iAtsMetricsProvider.getMetricsVersionArtifact().getEstimatedReleaseDate() != null) {
            estimatedReleaseXDate.setDate(iAtsMetricsProvider.getMetricsVersionArtifact().getEstimatedReleaseDate());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      SMAMetrics sMet =
            new SMAMetrics(iAtsMetricsProvider.getMetricsArtifacts(), iAtsMetricsProvider.getMetricsVersionArtifact(),
                  iAtsMetricsProvider.getManHoursPerDayPreference(), getEstimatedReleaseDate());
      createOverviewChart(sMet, metricsComposite);
      addSpace();
      createHoursRemainingByAssigneesChart(sMet, metricsComposite);
      addSpace();
      createCompletedByAssigneesChart(sMet, metricsComposite);

      mainComp.layout();
      computeScrollSize();
   }

   private Date getEstimatedReleaseDate() throws OseeCoreException {
      if (estimatedReleaseXDate != null && estimatedReleaseXDate.getDate() != null) {
         return estimatedReleaseXDate.getDate();
      }
      if (iAtsMetricsProvider.getMetricsVersionArtifact() != null) {
         return iAtsMetricsProvider.getMetricsVersionArtifact().getEstimatedReleaseDate();
      }
      // Try to find an estimated release date from one of the workflows
      for (Artifact art : iAtsMetricsProvider.getMetricsArtifacts()) {
         if (art instanceof TeamWorkFlowArtifact) {
            VersionArtifact verArt = ((TeamWorkFlowArtifact) art).getWorldViewTargetedVersion();
            if (verArt != null) {
               if (verArt.getEstimatedReleaseDate() != null) {
                  return verArt.getEstimatedReleaseDate();
               }
            }
         }
      }
      return null;
   }

   private boolean isUsingEstimatedReleaseDateFromOverride() throws OseeCoreException {
      if (estimatedReleaseXDate != null && estimatedReleaseXDate.getDate() != null) {
         return true;
      }
      return false;
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

      lines.add(XBarGraphLine.getTextLine("Loaded", sMet.toStringObjectBreakout()));
      lines.add(XBarGraphLine.getTextLineRedIfTrue("Workflows", String.format(
            "Estimates off %d workflows with %d having 0 estimates.", sMet.getNumSMAs(), sMet.getNumNotEstimated()),
            sMet.getNumNotEstimated() > 0));

      lines.add(XBarGraphLine.getPercentLine(
            "By Workflow Percents (" + sMet.getCummulativeWorkflowPercentComplete() + "/" + sMet.getNumTeamWfs() + ")",
            (int) sMet.getPercentCompleteByWorkflowPercents()));
      lines.add(XBarGraphLine.getPercentLine(
            "By Number of Workflows (" + sMet.getCompletedWorkflows().size() + "/" + sMet.getNumSMAs() + ")",
            (int) sMet.getPercentCompleteByWorkflow()));

      lines.add(XBarGraphLine.getTextLine("Estimated Hours: ", String.format("%5.2f Hours", sMet.getEstHours())));
      lines.add(XBarGraphLine.getTextLine("Remaining Hours: ", String.format(
            "%5.2f Hours = (Estimated hours %5.2f - (Estimated hours %5.2f x Percent Complete %5.2f))",
            sMet.getHrsRemainFromEstimates(), sMet.getEstHours(), sMet.getEstHours(),
            sMet.getPercentCompleteByWorkflowPercents())));
      lines.add(XBarGraphLine.getTextLine("Hours Spent: ", String.format("%5.2f Hours", sMet.getHrsSpent())));
      lines.add(XBarGraphLine.getTextLine("Hours Per Man Day Preference: ", String.format("%5.2f Hours per Day",
            sMet.getHoursPerManDay())));
      lines.add(XBarGraphLine.getTextLine("Man Days Needed: ", String.format(
            "%5.2f Days = Remaining Hours %5.2f / Hours Per Day of %5.2f", sMet.getManDaysNeeded(),
            sMet.getHrsRemainFromEstimates(), sMet.getHoursPerManDay())));

      try {
         lines.add(new XBarGraphLine(
               "Targeted Version",
               0,
               iAtsMetricsProvider.getMetricsVersionArtifact() == null ? "Not Set" : iAtsMetricsProvider.getMetricsVersionArtifact().getDescriptiveName()));
         lines.add(new XBarGraphLine(
               "Version Estimated Release Date",
               0,
               iAtsMetricsProvider.getMetricsVersionArtifact() == null ? "Not Set" : iAtsMetricsProvider.getMetricsVersionArtifact().getSoleAttributeValueAsString(
                     ATSAttributes.ESTIMATED_RELEASE_DATE_ATTRIBUTE.getStoreName(), "Not Set")));
         lines.add(new XBarGraphLine("Metrics Estimated Release Date", 0,
               getEstimatedReleaseDate() == null ? "Not Set" : getEstimatedReleaseDate().toString()));
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
                  XBarGraphLine.DEFAULT_RED_BACKGROUND, 100, String.format(
                        "%5.2f hours exceeds remaining release hours %5.2f;  Over by %5.2f hours.",
                        hoursRemainingFromEstimates, hoursTillRelease, hoursRemainingFromEstimates - hoursTillRelease)));
         } else {
            lines.add(new XBarGraphLine("Release Effort Remaining", XBarGraphLine.DEFAULT_GREEN_FOREGROUND,
                  XBarGraphLine.DEFAULT_GREEN_BACKGROUND, SWT.COLOR_WHITE, SWT.COLOR_WHITE, percent, String.format(
                        "%5.2f remaining work hours", hoursRemainingFromEstimates), String.format(
                        "%5.2f release remaining hours", hoursRemainingFromEstimates)));
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
         try {
            int numCompleted = sMet.getUserToCompletedSmas(user).size();
            double cummulativePercentComplete = numCompleted * 100;
            int numInWork = sMet.getUserToAssignedSmas(user).size();
            // Since table is loaded with arts and also shows children, don't want to count artifacts twice
            Set<Artifact> processedArts = new HashSet<Artifact>();
            if (sMet.getUserToAssignedSmas().getValues(user) != null) {
               for (Artifact sma : sMet.getUserToAssignedSmas().getValues(user)) {
                  if (!processedArts.contains(sma) && !sMet.getUserToCompletedSmas().containsValue(sma)) {
                     cummulativePercentComplete += ((StateMachineArtifact) sma).getWorldViewPercentCompleteTotal();
                     processedArts.add(sma);
                  }
               }
            }
            int numTotal = numCompleted + numInWork;
            int percentCompleteByNumber = 0;
            if (numCompleted == numTotal) {
               percentCompleteByNumber = 100;
            } else if (numCompleted != 0 && numTotal != 0) {
               double percent = new Double(numCompleted) / numTotal * 100.0;
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
      XBarGraphTable table =
            new XBarGraphTable("Completed by Assignee per Assigned Workflow (Team, Task and Review)", "User",
                  "Percent Complete", lines);
      table.setFillHorizontally(true);
      table.createWidgets(parent, 1);
      adapt(table);
   }

   public void createHoursRemainingByAssigneesChart(SMAMetrics sMet, Composite parent) throws OseeCoreException {
      List<XBarGraphLine> lines = new ArrayList<XBarGraphLine>();
      Double hoursRemain = null;
      if (sMet.getEstRelDate() != null) {
         hoursRemain = sMet.getHoursTillRel();
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
               lines.add(new XBarGraphLine(user.getName(), (int) userHoursRemain, String.format(
                     "%5.2f - (Estimated release date not set)", userHoursRemain)));
            } else if (hoursRemain == null) {
               lines.add(new XBarGraphLine(user.getName(), (int) userHoursRemain, String.format("%5.2f",
                     userHoursRemain)));
            } else {
               if (userHoursRemain == 0.0) {
                  lines.add(new XBarGraphLine(user.getName(), XBarGraphLine.DEFAULT_GREEN_FOREGROUND,
                        XBarGraphLine.DEFAULT_GREEN_BACKGROUND, 100, "No Estimated Hours Remain"));
               } else if (userHoursRemain > hoursRemain) {
                  lines.add(new XBarGraphLine(user.getName(), XBarGraphLine.DEFAULT_RED_FOREGROUND,
                        XBarGraphLine.DEFAULT_RED_BACKGROUND,
                        (((int) userHoursRemain) > 1 ? (int) userHoursRemain : 1), String.format(
                              "%5.2f - Exceeds release remaining hours %5.2f.", userHoursRemain, hoursRemain)));
               } else {
                  lines.add(new XBarGraphLine(user.getName(), XBarGraphLine.DEFAULT_GREEN_FOREGROUND,
                        XBarGraphLine.DEFAULT_GREEN_BACKGROUND, (int) userHoursRemain, String.format(
                              "%5.2f - Within remaining hours %5.2f.", userHoursRemain, hoursRemain)));
               }
            }
         } catch (OseeCoreException ex) {
            lines.add(new XBarGraphLine(user.getName(), 0, "Exception: " + ex.getLocalizedMessage()));
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      XBarGraphTable table =
            new XBarGraphTable(
                  "Hours Remaining by Assignee (green = within remaining hours; red = exceeds remaining hours till release)",
                  "User", "Hours Remaining", lines);
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
