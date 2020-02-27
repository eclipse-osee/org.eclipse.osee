/*********************************************************************
 * Copyright (c) 2013 Boeing
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
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.AtsActionUiEndpointApi;
import org.eclipse.osee.ats.rest.internal.util.ActionPage;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.ats.rest.internal.workitem.journal.JournalWebOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@Path("/ui/action")
public final class AtsActionUiEndpointImpl implements AtsActionUiEndpointApi {

   private final AtsApi atsApi;
   private final Log logger;

   public AtsActionUiEndpointImpl(AtsApi atsApi, Log logger) {
      this.atsApi = atsApi;
      this.logger = logger;
   }

   @Override
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String get() {
      return AHTML.simplePage("ATS UI Endpoint");
   }

   /**
    * @return html5 journal entry/comments page
    */
   @Override
   @Path("{atsId}/journal/{aid}")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getJournal(@PathParam("atsId") String atsId, @PathParam("aid") String useraid) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAtsId(atsId);
      if (workItem == null) {
         return AHTML.simplePage(String.format("Invalid ATS Id [%s]", atsId));
      }
      AtsUser user = null;
      if (Strings.isNumeric(useraid)) {
         user = atsApi.getUserService().getUserById(ArtifactId.valueOf(Long.valueOf(useraid)));
         if (user == null) {
            return AHTML.simplePage(String.format("Invalid ATS User Art Id [%s]", useraid));
         }
      } else {
         return AHTML.simplePage(String.format("Invalid ATS User Art Id [%s]", useraid));
      }

      JournalWebOperations op = new JournalWebOperations(workItem, user, atsApi);
      return op.getHtml();
   }

   /**
    * @param id (artId, atsId) of action to display
    * @return html representation of the action
    */
   @Override
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
   @Override
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
   @Override
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
   @Override
   @Path("Search")
   @GET
   @Produces(MediaType.TEXT_HTML)
   public ViewModel getSearch() throws Exception {
      return new ViewModel("search.html");
   }

   /**
    * @param id (id, atsId) of action to display
    * @return html representation w/ transition ui
    */
   @Override
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
