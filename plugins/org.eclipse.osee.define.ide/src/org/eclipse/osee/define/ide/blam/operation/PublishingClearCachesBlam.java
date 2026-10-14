/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.ide.blam.operation;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.component.annotations.Component;

/**
 * BLAM to clear the Publishing Template Manager and Data Rights Manager caches.
 *
 * @author Loren K. Ashley
 */

@Component(service = AbstractBlam.class, immediate = true)
public class PublishingClearCachesBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Publishing Clear Caches";
   private static final String BLAM_DESC =
      "Clears the Publishing Template and Datarights Configuration caches on all servers.";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public PublishingClearCachesBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      return List.of();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      monitor.beginTask(BLAM_NAME, IProgressMonitor.UNKNOWN);

      OseeClient oseeClient = OsgiUtil.getService(this.getClass(), OseeClient.class);
      JaxRsApi jaxRsApi = oseeClient.jaxRsApi();
      XResultData xResultData = new XResultData();
      String userAuthId = oseeClient.userService().getUser().getLoginIds().get(0);

      xResultData.log("Clear Publishing Template and Data Rights Configuration Caches All Servers");

      long serverCount =
         Arrays.stream(OseeInfo.getValue(OseeProperties.OSEE_HEALTH_SERVERS_KEY).replaceAll(" ", "").split(",")).filter(
            Predicate.not(Strings::isInvalidOrBlank)).peek(
               (server) -> Arrays.stream(Target.values()).forEach(
                  (target) -> this.clearServerCache(jaxRsApi, target, server, xResultData, userAuthId))).count();

      if (serverCount == 0) {

         xResultData.logf("\n\nNo %s configured. Other server caches were not cleared.\n\n",
            OseeProperties.OSEE_HEALTH_SERVERS_KEY);

         Arrays.stream(Target.values()).forEach((target) -> target.clearAttachedServerCache(oseeClient));

         xResultData.logf("Connected server caches cleared.");
      }

      Displays.ensureInDisplayThread(() -> XResultDataUI.report(xResultData, "Cache Clear Results"));

      monitor.done();

   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private enum Target {

      DATARIGHTS_MANAGER((oseeClient) -> oseeClient.getTemplateManagerEndpoint().deleteCache(), "/define/datarights"),

      TEMPLATE_MANAGER((oseeClient) -> oseeClient.getDataRightsEndpoint().deleteCache(), "/define/templatemanager");

      private final Consumer<OseeClient> attachedServerClear;
      private final String urlTail;

      private Target(Consumer<OseeClient> attachedServerClear, String urlTail) {
         this.attachedServerClear = attachedServerClear;
         this.urlTail = urlTail;
      }

      void clearAttachedServerCache(OseeClient oseeClient) {
         this.attachedServerClear.accept(oseeClient);
      }

      String getUrlTail() {
         return this.urlTail;
      }
   }

   private void clearServerCache(JaxRsApi jaxRsApi, Target target, String server, XResultData xResultData,
      String userAuthId) {

      try {

         WebTarget webTarget = jaxRsApi.newTargetUrl(String.format("%s%s", server, target.getUrlTail()));

         Response response = webTarget.request().delete();

         int responseStatus = response.getStatus();

         if ((responseStatus == HttpURLConnection.HTTP_NO_CONTENT) || (responseStatus == HttpURLConnection.HTTP_OK) || (responseStatus == HttpURLConnection.HTTP_ACCEPTED)) {
            xResultData.logf("\n\nServer %s cache clear was successful.", server);

         } else {

            Response.StatusType statusType = response.getStatusInfo();

            xResultData.logf("\n\nERROR: Server %s cache clear was not successful.", server);
            xResultData.logf("\nResponse: %d - %s", statusType.getStatusCode(), statusType.getReasonPhrase());

         }

      } catch (Exception e) {

         xResultData.logf("\n\nERROR: Server %s cache clear exception %s.\n", server, e.getLocalizedMessage());
      }

   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_HEALTH);
   }

   @Override
   public String getDescriptionUsage() {
      return BLAM_DESC;
   }

}
