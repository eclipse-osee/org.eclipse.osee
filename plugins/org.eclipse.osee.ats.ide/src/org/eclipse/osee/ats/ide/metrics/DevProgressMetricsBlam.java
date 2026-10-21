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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.ws.rs.core.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXTeamDefinitionsWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXVersionSelWidget;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXDateSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Stephen J. Molaro
 */
@Component(service = AbstractBlam.class, immediate = true)
public class DevProgressMetricsBlam extends AbstractAtsBlam {

   private static final String BLAM_NAME = "Development Progress Metrics";

   private XXTeamDefinitionsWidget teamdDefWidget;
   private XXVersionSelWidget versionWidget;
   private XXDateSelWidget startDateWidget;
   private XXDateSelWidget endDateWidget;
   private Date startDate;
   private Date endDate;
   private boolean allTime;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public DevProgressMetricsBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wba.andTeamDefinitionsWidget();
      wba.andVersionWidget();
      wba.andDateWidget(XWidgetLabels.START_DATE);
      wba.andDateWidget(XWidgetLabels.END_DATE);
      wba.andCheckBoxWidget(XWidgetLabels.ALL_TIME);
      return wba.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener modListener, boolean isEditable) {
      if (xWidget.isWidget(WidgetIdAts.XXTeamDefinitionsWidget)) {
         teamdDefWidget = (XXTeamDefinitionsWidget) xWidget;
         teamdDefWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               setProgramVersions();
               versionWidget.getLabelLinkWidget().redraw();
            }
         });
      } else if (xWidget.isWidget(WidgetIdAts.XXVersionSelWidget)) {
         versionWidget = (XXVersionSelWidget) xWidget;
      } else if (xWidget.getLabel().equalsIgnoreCase(XWidgetLabels.START_DATE)) {
         startDateWidget = (XXDateSelWidget) xWidget;
         initializeWidgets();
      } else if (xWidget.getLabel().equalsIgnoreCase(XWidgetLabels.END_DATE)) {
         endDateWidget = (XXDateSelWidget) xWidget;
         initializeWidgets();
      }
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return;
      }

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               String fileLocation = String.format("C:%sUsers%s%s%sDownloads", File.separator, File.separator,
                  System.getProperty("user.name"), File.separator);

               ArtifactToken selectedVersion = versionWidget.getSelectedFirst();

               startDate = (Date) variableMap.getValue(XWidgetLabels.START_DATE);
               endDate = (Date) variableMap.getValue(XWidgetLabels.END_DATE);
               allTime = variableMap.getBoolean(XWidgetLabels.ALL_TIME);

               try (Response res = AtsApiService.get().getServerEndpoints().getMetricsEp().devProgressReport(
                  selectedVersion.getName(), startDate, endDate, allTime);) {

                  if (res == null) {
                     return;
                  }

                  String filePath =
                     String.format("%s%s%s", fileLocation, File.separator, res.getHeaderString("FileName"));
                  try (BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(filePath)));) {

                     GZIPInputStream gzInputStream = (GZIPInputStream) res.getEntity();
                     StringBuffer sb = new StringBuffer();
                     try (BufferedReader in = new BufferedReader(new InputStreamReader(gzInputStream));) {
                        String inputLine = "";
                        while ((inputLine = in.readLine()) != null) {
                           sb.append(inputLine);
                        }
                     }
                     bwr.write(sb.toString());
                     bwr.flush();
                     bwr.close();
                  }
                  res.close();
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }

         };
      });
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   public void setProgramVersions() {
      HashSet<IAtsVersion> versionSet = new HashSet<>();
      Collection<IAtsTeamDefinition> teamDefs = teamdDefWidget.getSelected();
      for (IAtsTeamDefinition teamDef : teamDefs) {
         if (teamDef.isValid()) {
            versionSet.addAll(AtsApiService.get().getVersionService().getVersionsFromTeamDefHoldingVersions(teamDef));
         }
      }
      versionWidget.setSelectableVersions(versionSet);
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

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.REPORTS);
   }

   @Override
   public String getDescriptionUsage() {
      return "Generates Dev Progress Report based on Version. Results are in the downloads folder.";
   }

}
