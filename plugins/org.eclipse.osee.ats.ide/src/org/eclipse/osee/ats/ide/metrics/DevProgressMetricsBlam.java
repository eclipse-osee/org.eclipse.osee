/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.ide.metrics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.ws.rs.core.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelVersionSelection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XDateDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Stephen J. Molaro
 */
public class DevProgressMetricsBlam extends AbstractBlam {

   private static final String NAME = "Development Progress Metrics BLAM";
   private static final String TEAM_DEFINITIONS = "Team Definition(s)";
   private static final String VERSION = "Version";
   private static final String START_DATE = "Start Date";
   private static final String END_DATE = "End Date";
   private static final String DAY_OF_WEEK = "Select they day of the week (Default - Monday)";
   private static final String DURATION = "Select they length of iteration (Default - Weekly)";
   private static final String SHOW_PERIODIC = "Show Periodic Workflow Table";
   private static final String SHOW_NONPERIODIC = "Show Workflow Table";
   private static final String SHOW_PERIODIC_TASK = "Show Periodic Task Table";
   private static final String SHOW_NONPERIODIC_TASK = "Show Task Table";

   private XHyperlabelTeamDefinitionSelection programWidget;
   private XHyperlabelVersionSelection versionWidget;
   private XDateDam startDateWidget;
   private XDateDam endDateWidget;
   private Date startDate;
   private Date endDate;
   private int dayOfWeek;
   private boolean showPeriodic;
   private boolean showNonPeriodic;
   private boolean showPeriodicTask;
   private boolean showNonPeriodicTask;
   private int duration;
   private Collection<IAtsVersion> versions;

   public enum Days {
      SUNDAY,
      MONDAY,
      TUESDAY,
      WEDNESDAY,
      THURSDAY,
      FRIDAY,
      SATURDAY
   }

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public String getDescriptionUsage() {
      return "Generates Dev Progress Report based on Version";
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      if (xWidget.getLabel().equalsIgnoreCase(VERSION)) {
         versions = new ArrayList<>();
         versionWidget = (XHyperlabelVersionSelection) xWidget;
         versionWidget.getLabelHyperlink().redraw();
      } else if (xWidget.getLabel().equals(TEAM_DEFINITIONS)) {
         programWidget = (XHyperlabelTeamDefinitionSelection) xWidget;
         programWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               setProgramVersions();
               versionWidget.setSelectableVersions(versions);
               versionWidget.getLabelHyperlink().redraw();
            }
         });
      } else if (xWidget.getLabel().equalsIgnoreCase(START_DATE)) {
         startDateWidget = (XDateDam) xWidget;
         initializeWidgets();
      } else if (xWidget.getLabel().equalsIgnoreCase(END_DATE)) {
         endDateWidget = (XDateDam) xWidget;
         initializeWidgets();
      }
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               String fileLocation = String.format("C:%sUsers%s%s%sDownloads", File.separator, File.separator,
                  System.getProperty("user.name"), File.separator);

               Version selectedVersion = versionWidget.getSelectedVersion();

               startDate = (Date) variableMap.getValue(START_DATE);
               endDate = (Date) variableMap.getValue(END_DATE);
               dayOfWeek = getDayOfWeekAsInt(variableMap.getString(DAY_OF_WEEK));
               duration = getIterationInt(variableMap.getString(DURATION));
               showPeriodic = variableMap.getBoolean(SHOW_PERIODIC);
               showNonPeriodic = variableMap.getBoolean(SHOW_NONPERIODIC);
               showPeriodicTask = variableMap.getBoolean(SHOW_PERIODIC_TASK);
               showNonPeriodicTask = variableMap.getBoolean(SHOW_NONPERIODIC_TASK);

               Response res = AtsApiService.get().getServerEndpoints().getMetricsEp().devProgressReport(
                  selectedVersion.getName(), startDate, endDate, dayOfWeek, duration, showPeriodic, showNonPeriodic,
                  showPeriodicTask, showNonPeriodicTask);

               if (res == null) {
                  return;
               }

               String filePath = String.format("%s%s%s", fileLocation, File.separator, res.getHeaderString("FileName"));
               BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(filePath)));

               GZIPInputStream gzInputStream = (GZIPInputStream) res.getEntity();
               StringBuffer sb = new StringBuffer();
               BufferedReader in = new BufferedReader(new InputStreamReader(gzInputStream));
               String inputLine = "";
               while ((inputLine = in.readLine()) != null) {
                  sb.append(inputLine);
               }
               bwr.write(sb.toString());
               bwr.flush();
               bwr.close();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.REPORTS);
   }

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      XWidgetBuilder wb = new XWidgetBuilder();
      wb.andWidget(TEAM_DEFINITIONS, "XHyperlabelTeamDefinitionSelection").endWidget();
      wb.andWidget(VERSION, "XHyperlabelVersionSelection").endWidget();
      wb.andWidget(START_DATE, "XDateDam").endWidget();
      wb.andWidget(END_DATE, "XDateDam").endWidget();
      wb.andWidget(DAY_OF_WEEK, getWeekdaysXCombo()).andDefault("Monday").endWidget();
      wb.andWidget(DURATION, "XCombo(Week,1-Day,3-Days)").andDefault("Week").endWidget();
      wb.andWidget(SHOW_NONPERIODIC, "XCheckBox").endWidget();
      wb.andWidget(SHOW_NONPERIODIC_TASK, "XCheckBox").endWidget();
      wb.andWidget(SHOW_PERIODIC, "XCheckBox").endWidget();
      wb.andWidget(SHOW_PERIODIC_TASK, "XCheckBox").endWidget();
      return wb.getItems();
   }

   public void setProgramVersions() {
      HashSet<IAtsVersion> versionSet = new HashSet<>();
      Collection<TeamDefinition> teamDefs = programWidget.getSelectedTeamDefintions();
      for (IAtsTeamDefinition teamDef : teamDefs) {
         if (teamDef.isValid()) {
            versionSet.addAll(AtsApiService.get().getVersionService().getVersionsFromTeamDefHoldingVersions(teamDef));
         }
      }
      versions.clear();
      versions.addAll(versionSet);
   }

   private void initializeWidgets() {
      if (startDateWidget != null) {
         initializeStartDate();
      }
      if (endDateWidget != null) {
         endDateWidget.setDate(Calendar.getInstance().getTime());
      }
   }

   private void initializeStartDate() {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.DAY_OF_MONTH, 1);
      startDateWidget.setDate(cal.getTime());
   }

   private String getWeekdaysXCombo() {
      StringBuilder builder = new StringBuilder();
      String[] weekdays = new DateFormatSymbols().getWeekdays();
      builder.append("XCombo(");
      for (int i = 1; i < 8; i++) {
         if (i != 1) {
            builder.append(",");
         }
         builder.append(weekdays[i]);
      }
      builder.append(")");
      return builder.toString();
   }

   public static int getDayOfWeekAsInt(String dayAsString) {
      int toReturn = 2;
      try {
         if (!dayAsString.contains("select")) {
            Days day = Days.valueOf(dayAsString.toUpperCase());
            toReturn = day.ordinal() + 1;
         }
      } catch (Exception ex) {
         //Do Nothing
      }
      return toReturn;
   }

   public static int getIterationInt(String durationString) {
      int toReturn = 7;
      try {
         if (durationString.equals("1-Day")) {
            toReturn = 1;
         } else if (durationString.equals("3-Days")) {
            toReturn = 3;
         }
      } catch (Exception ex) {
         //Do Nothing
      }
      return toReturn;
   }

}