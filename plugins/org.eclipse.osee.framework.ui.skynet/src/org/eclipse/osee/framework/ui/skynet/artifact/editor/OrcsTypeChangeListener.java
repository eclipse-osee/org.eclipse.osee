package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchUuidEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;

/**
 * @author Donald G. Dunne
 */
public class OrcsTypeChangeListener implements IArtifactEventListener {

   List<IEventFilter> filters;

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      if (filters == null) {
         filters = new LinkedList<>();
         filters.add(new ArtifactTypeEventFilter(CoreArtifactTypes.OseeTypeDefinition));
         filters.add(new BranchUuidEventFilter(CoreBranches.COMMON));
      }
      return filters;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (sender.isRemote() || !artifactEvent.isOnBranch(CoreBranches.COMMON)) {
         return;
      }
      boolean found = false;
      for (EventBasicGuidArtifact art : artifactEvent.getArtifacts()) {
         if (art.getArtifactType().equals(CoreArtifactTypes.OseeTypeDefinition)) {
            found = true;
            break;
         }
      }
      if (!found) {
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            boolean reload = true;
            if (!OseeProperties.isInTest()) {
               reload = MessageDialog.openConfirm(Displays.getActiveShell(), "Reload Server Types Cache",
                  "OSEE has detected a change to the ORCS Types.\n\nWould you like to notify the server to reload types cache?");
            }
            if (reload) {
               reloadServerAndClientTypes();
            }
         }

      });
   }

   public static void reloadServerAndClientTypes() {
      try {
         String appServer = OseeClientProperties.getOseeApplicationServer();
         String atsUri = String.format("%s/orcs", appServer);
         JaxRsClient jaxRsClient = JaxRsClient.newBuilder().createThreadSafeProxyClients(true).build();
         JaxRsWebTarget target = jaxRsClient.target(atsUri);
         if (target != null) {
            TypesEndpoint typesEndpoint = target.newProxy(TypesEndpoint.class);
            if (typesEndpoint != null) {
               typesEndpoint.invalidateCaches();
            }
         }
         ServiceUtil.getOseeCacheService().reloadTypes();
      } catch (Exception ex) {
         // do nothing
      }
   }

}
