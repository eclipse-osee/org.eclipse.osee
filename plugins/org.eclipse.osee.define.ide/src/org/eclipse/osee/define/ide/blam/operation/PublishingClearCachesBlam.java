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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * BLAM to clear the Publishing Template Manager and Data Rights Manager caches.
 *
 * @author Loren K. Ashley
 */

public class PublishingClearCachesBlam extends AbstractBlam {

   /**
    * Enumeration of the caches that need to be cleared.
    */

   private enum Target {

      //@formatter:off
      /**
       * Parameters for clearing the Data Rights Manager cache.
       */

      DATARIGHTS_MANAGER
         (
            ( oseeClient ) -> oseeClient.getTemplateManagerEndpoint().deleteCache(),
            "/define/datarights"
         ),

      /**
       * Parameters for clean the Template Manager cache.
       */

      TEMPLATE_MANAGER
         (
            ( oseeClient ) -> oseeClient.getDataRightsEndpoint().deleteCache(),
            "/define/templatemanager"
         );
      //@formatter:on

      /**
       * Saves a {@link Consumer} implementation that clears the server cache of the connected server. This method is
       * used when the property {@link OseeProperties#OSEE_HEALTH_SERVERS_KEY} is not set.
       */

      private Consumer<OseeClient> attachedServerClear;

      /**
       * Saves the server URL path for clearing the cache. The URL tail is used when the property
       * {@link OseeProperties#OSEE_HEALTH_SERVERS_KEY} is set.
       */

      private String urlTail;

      /**
       * Creates the enumeration member and saves the cache clear parameters.
       *
       * @param attachedServerClear a {@link Consumer} implementation to clear the cache of the connected server.
       * @param urlTail the server URL path for clearing the cache.
       */

      private Target(Consumer<OseeClient> attachedServerClear, String urlTail) {
         this.attachedServerClear = attachedServerClear;
         this.urlTail = urlTail;
      }

      /**
       * Gets the REST API end point class for clearing the cache.
       *
       * @return the {@link Class} implementing the REST API end point for clearing the cache.
       */

      void clearAttachedServerCache(OseeClient oseeClient) {
         this.attachedServerClear.accept(oseeClient);
      }

      /**
       * Gets the URL path for clearing the server's cache.
       *
       * @return gets the server's URL path for clearing the cache.
       */

      String getUrlTail() {
         return this.urlTail;
      }
   }

   /**
    * Description string for the BLAM
    */

   private static String blamDescription =
      "Clears the Publishing Template and Datarights Configuration caches on all servers.";

   /**
    * Name string for the BLAM
    */

   private static String blamName = "Publishing Clear Caches BLAM";

   /**
    * Creates a new {@link PublishingClearCachesBlam} for clearing publishing configuration caches.
    */

   public PublishingClearCachesBlam() {
      super(PublishingClearCachesBlam.blamName, PublishingClearCachesBlam.blamDescription, null);
   }

   /**
    * Clears the <code>target</code> cache on the <code>server</code>.
    *
    * @param jaxRsApi {@link JaxRsApi} used to create the {@link WebTarget} used to send the cache clear request.
    * @param target the {@link PublishingClearCachesBlam.Target} for the cache to be cleared.
    * @param server the URL of the server to be cleared.
    * @param xResultData status message are written to the {@link XResultData} object.
    */

   private void clearServerCache(JaxRsApi jaxRsApi, Target target, String server, XResultData xResultData) {

      try {

         var webTarget = jaxRsApi.newTargetUrl(String.format("http://%s%s", server, target.getUrlTail()));

         var response = webTarget.request().delete();

         var responseStatus = response.getStatus();

         //@formatter:off
         if(    ( responseStatus == HttpURLConnection.HTTP_NO_CONTENT )
             || ( responseStatus == HttpURLConnection.HTTP_OK         )
             || ( responseStatus == HttpURLConnection.HTTP_ACCEPTED   ) ) {
         //@formatter:on
            xResultData.logf("\n\nServer %s cache clear was successful.", server);

         } else {

            var statusType = response.getStatusInfo();

            xResultData.logf("\n\nERROR: Server %s cache clear was not successful.", server);
            xResultData.logf("\nResponse: %d - %s", statusType.getStatusCode(), statusType.getReasonPhrase());

         }

      } catch (Exception e) {

         //@formatter:off
         xResultData.logf
            (
               "\n\nERROR: Server %s cache clear exception %s.\n",
               server,
               e.getLocalizedMessage()
            );
         //@formatter:on
      }

   }

   /**
    * Set the OSEE Navigator menu location for the BLAM.
    */

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_HEALTH);
   }

   /**
    * The BLAM has no options.
    *
    * @return an empty list.
    */

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      return List.of();
   }

   /**
    * The BLAM has no options.
    *
    * @return an empty "xWdigets" XML element.
    * @implNote This override is necessary to prevent the base class for defaulting to a branch selector for the
    * parameters box.
    */

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets/>";
   }

   /**
    * When the property {@link OseeProperties#OSEE_HEALTH_SERVERS_KEY} contains a list of servers a JaxRS
    * {@link WebTarget} is used to request the remote server to clear it's caches. When the property is not set REST API
    * end points are used to clear the caches of the connected server.
    */

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      monitor.beginTask(PublishingClearCachesBlam.blamName, IProgressMonitor.UNKNOWN);

      var oseeClient = OsgiUtil.getService(this.getClass(), OseeClient.class);
      var jaxRsApi = oseeClient.jaxRsApi();
      var xResultData = new XResultData();

      xResultData.log("Clear Publishing Template and Data Rights Configuration Caches All Servers");

      //@formatter:off
      var serverCount =
      Arrays
         .stream
            (
               OseeInfo
                  .getValue(OseeProperties.OSEE_HEALTH_SERVERS_KEY)
                  .replaceAll( " ", "")
                  .split(",")
            )
         .filter( Predicate.not(Strings::isInvalidOrBlank) )
         .peek
            (
               ( server ) -> Arrays
                                .stream( Target.values() )
                                .forEach( ( target ) -> this.clearServerCache( jaxRsApi, target, server, xResultData ) )
            )
         .count();
      //@formatter:on

      if (serverCount == 0) {

         xResultData.logf("\n\nNo %s configured. Other server caches were not cleared.\n\n",
            OseeProperties.OSEE_HEALTH_SERVERS_KEY);

         Arrays.stream(Target.values()).forEach((target) -> target.clearAttachedServerCache(oseeClient));

         xResultData.logf("Connected server caches cleared.");
      }

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            XResultDataUI.report(xResultData, "Cache Clear Results");
         }
      });

      monitor.done();

   }

}

/* EOF */
