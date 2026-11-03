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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import javax.ws.rs.core.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXTeamDefinitionsWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXTargetedVersionWidget;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Stephen J. Molaro
 */
@Component(service = AbstractBlam.class, immediate = true)
public class SoftwareReqVolatilityMetricsBlam extends AbstractAtsBlam {

   private static final String BLAM_NAME = "Software Requirements Volatility Metrics";
   static final String INCLUDE_IMPACTS = "Include Safety and Security counts";

   private XXTeamDefinitionsWidget teamDefWidget;
   private XXTargetedVersionWidget versionWidget;
   private XXDateSelWidget startDateWidget;
   private XXDateSelWidget endDateWidget;
   private Date startDate;
   private Date endDate;
   private boolean allTime;
   private boolean includeImpacts;
   private Collection<IAtsVersion> versions;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public SoftwareReqVolatilityMetricsBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wba.andTeamDefinitionsWidget();
      wba.andTargetedVersionWidget();
      wba.andDateWidget(XWidgetLabels.START_DATE);
      wba.andDateWidget(XWidgetLabels.END_DATE);
      wba.andCheckBoxWidget(XWidgetLabels.ALL_TIME);
      wba.andCheckBoxWidget(INCLUDE_IMPACTS);
      return wba.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener modListener, boolean isEditable) {
      if (xWidget.isWidget(WidgetIdAts.XXTargetedVersionWidget)) {
         versions = new ArrayList<>();
         versionWidget = (XXTargetedVersionWidget) xWidget;
         versionWidget.getLabelLinkWidget().redraw();
      } else if (xWidget.isWidget(WidgetIdAts.XXTeamDefinitionsWidget)) {
         teamDefWidget = (XXTeamDefinitionsWidget) xWidget;
         teamDefWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               setProgramVersions();
               versionWidget.setSelectable(AtsObjects.toArtifactTokens(versions));
               versionWidget.getLabelLinkWidget().redraw();
            }
         });
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

      ArtifactToken selectedVersion = versionWidget.getSelectedFirst();
      if (selectedVersion == null) {
         return;
      }

      Response res = null;
      BufferedWriter bwr = null;
      try {
         String fileLocation = String.format("C:%sUsers%s%s%sDownloads", File.separator, File.separator,
            System.getProperty("user.name"), File.separator);

         startDate = (Date) variableMap.getValue(XWidgetLabels.START_DATE);
         endDate = (Date) variableMap.getValue(XWidgetLabels.END_DATE);
         allTime = variableMap.getBoolean(XWidgetLabels.ALL_TIME);
         includeImpacts = variableMap.getBoolean(INCLUDE_IMPACTS);
         res = AtsApiService.get().getServerEndpoints().getMetricsEp().softwareReqVolatility(
            selectedVersion.getIdString(), startDate, endDate, allTime, includeImpacts);

         if (res == null || res.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new OseeStateException("No response from metrics endpoint");
         }

         String filePath = String.format("%s%s%s", fileLocation, File.separator, res.getHeaderString("FileName"));
         bwr = new BufferedWriter(new FileWriter(new File(filePath)));

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
      } finally {
         if (res != null) {
            res.close();
         }
         if (bwr != null) {
            try {
               bwr.close();
            } catch (IOException e) {
               OseeLog.log(Activator.class, Level.WARNING, "Error closing writer", e);
            }
         }
      }
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   public void setProgramVersions() {
      HashSet<IAtsVersion> versionSet = new HashSet<>();
      Collection<IAtsTeamDefinition> teamDefs = teamDefWidget.getSelected();
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

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.REPORTS);
   }

   @Override
   public String getDescriptionUsage() {
      return "Generates Software Requirements Volatility Metrics Report based on selected version. The date selection chooses requirements based on the completion date. Software Requirements are always included by default. Results are in the downloads folder.";
   }

}
