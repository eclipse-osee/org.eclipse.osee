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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.rest.internal.util.ActionPage;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@Path("/ui/action")
public final class ActionUiResource {

   private final AtsApi atsApi;
   private final Log logger;

   public ActionUiResource(AtsApi atsApi, Log logger) {
      this.atsApi = atsApi;
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
      List<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItemsByIds(ids);
      if (workItems.isEmpty()) {
         return RestUtil.simplePage(String.format("Action with id(s) [%s] can not be found", ids));
      }
      if (workItems.size() == 1) {
         ActionPage page =
            new ActionPage(logger, atsApi, (ArtifactReadable) workItems.iterator().next().getStoreObject(), false);
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
    * @param id (id, atsId) of action to display
    * @return html representation of the action
    */
   @Path("{id}/details")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getActionWithDetails(@PathParam("id") String id) throws Exception {
      ArtifactReadable action = (ArtifactReadable) atsApi.getQueryService().getArtifactById(id);
      if (action == null) {
         return RestUtil.simplePage(String.format("Action with id [%s] can not be found", id));
      }
      ActionPage page = new ActionPage(logger, atsApi, action, true);
      return page.generate();
   }

   /**
    * @return html5 action entry page
    */
   @Path("NewAction")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getNewSource() throws Exception {
      List<ArtifactToken> sortedAis = new ArrayList<>();
      for (ArtifactToken ai : getAis()) {
         sortedAis.add(ai);
      }
      Collections.sort(sortedAis, new IdComparator());

      return new ViewModel("newAction.html") //
         .param("ActionableItemDataList", getAiDataList(sortedAis));
   }

   private String getAiDataList(List<ArtifactToken> sortedAis) {
      StringBuilder sb = new StringBuilder("<datalist id=\"actionableItemList\">");
      for (ArtifactToken art : sortedAis) {
         sb.append("<option value=\"");
         sb.append(art.getName());
         sb.append("\">");
      }
      sb.append("</datalist>");
      return sb.toString();
   }
   private static final class IdComparator implements Comparator<ArtifactToken> {

      @Override
      public int compare(ArtifactToken arg0, ArtifactToken arg1) {
         return arg0.getName().compareTo(arg1.getName());
      }
   };

   private Collection<ArtifactToken> getAis() {
      return atsApi.getQueryService().getArtifacts(AtsArtifactTypes.ActionableItem);
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
      ArtifactReadable action = (ArtifactReadable) atsApi.getQueryService().getArtifactById(id);
      if (action == null) {
         return RestUtil.simplePage(String.format("Action with id [%s] can not be found", id));
      }
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(action);
      ActionPage page = new ActionPage(logger, atsApi, workItem, false);
      page.setAddTransition(true);
      return page.generate();
   }

}
