/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.util.ActionPage;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@Path("/ui/action")
public final class ActionUiResource {

   private final IAtsServer atsServer;
   private final Log logger;

   public ActionUiResource(IAtsServer atsServer, Log logger) {
      this.atsServer = atsServer;
      this.logger = logger;
   }

   /**
    * @return resource name
    */
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() throws Exception {
      return RestUtil.simplePageHtml("ATS UI Resource");
   }

   /**
    * @param id (artId, atsId) of action to display
    * @return html representation of the action
    */
   @Path("{ids}")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getAction(@PathParam("ids") String ids) throws Exception {
      List<IAtsWorkItem> workItems = atsServer.getQueryService().getWorkItemsByIds(ids);
      if (workItems.isEmpty()) {
         return RestUtil.simplePage(String.format("Action with id(s) [%s] can not be found", ids));
      }
      if (workItems.size() == 1) {
         ActionPage page =
            new ActionPage(logger, atsServer, (ArtifactReadable) workItems.iterator().next().getStoreObject(), false);
         return page.generate();
      } else {
         String idStr = "";
         for (IAtsWorkItem workItem : workItems) {
            idStr += workItem.getAtsId() + ",";
         }
         idStr = idStr.replaceFirst(",$", "");

         String url = "/ats/action/" + idStr + "/details";

         return new ViewModel("templates/world.html") //
            .param("PUT_ACTION_JSON_URL_HERE", url);
      }
   }

   /**
    * @param id (guid, atsId) of action to display
    * @return html representation of the action
    */
   @Path("{id}/details")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getActionWithDetails(@PathParam("id") String id) throws Exception {
      ArtifactReadable action = (ArtifactReadable) atsServer.getQueryService().getArtifactById(id);
      if (action == null) {
         return RestUtil.simplePage(String.format("Action with id [%s] can not be found", id));
      }
      ActionPage page = new ActionPage(logger, atsServer, action, true);
      return page.generate();
   }

   /**
    * @return html5 action entry page
    */
   @Path("NewAction")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getNewSource() throws Exception {
      List<ArtifactReadable> sortedAis = new ArrayList<>();
      for (ArtifactReadable ai : getAis()) {
         sortedAis.add(ai);
      }
      Collections.sort(sortedAis, new IdComparator());

      return new ViewModel("newAction.html") //
         .param("ActionableItemDataList", getAiDataList(sortedAis));
   }

   private String getAiDataList(List<ArtifactReadable> sortedAis) {
      StringBuilder sb = new StringBuilder("<datalist id=\"actionableItemList\">");
      for (ArtifactReadable art : sortedAis) {
         sb.append("<option value=\"");
         sb.append(art.getName());
         sb.append("\">");
      }
      sb.append("</datalist>");
      return sb.toString();
   }
   private static final class IdComparator implements Comparator<ArtifactReadable> {

      @Override
      public int compare(ArtifactReadable arg0, ArtifactReadable arg1) {
         return arg0.getName().compareTo(arg1.getName());
      }
   };

   private ResultSet<ArtifactReadable> getAis() {
      return atsServer.getQuery().andIsOfType(AtsArtifactTypes.ActionableItem).getResults();
   }

   /**
    * @return html5 action entry page
    */
   @Path("Search")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getSearch() throws Exception {
      return new ViewModel("search.html");
   }

   /**
    * @param id
    * @return html representation w/ transition ui
    */
   @Path("{id}/Transition")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getTransition(@PathParam("id") String id) throws Exception {
      ArtifactReadable action = (ArtifactReadable) atsServer.getQueryService().getArtifactById(id);
      if (action == null) {
         return RestUtil.simplePage(String.format("Action with id [%s] can not be found", id));
      }
      IAtsWorkItem workItem = atsServer.getWorkItemFactory().getWorkItem(action);
      ActionPage page = new ActionPage(logger, atsServer, workItem, false);
      page.setAddTransition(true);
      return page.generate();
   }

}
