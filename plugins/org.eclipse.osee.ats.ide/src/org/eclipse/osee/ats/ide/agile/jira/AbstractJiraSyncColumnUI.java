/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.agile.jira;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.jira.JiraEndpoint;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault.CoreCodeColumnTokenDefault;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.jira.JiraSearch;
import org.eclipse.osee.ats.api.workflow.jira.JiraSearches;
import org.eclipse.osee.ats.core.column.SprintColumn;
import org.eclipse.osee.ats.ide.column.BackgroundLoadingPreComputedColumnUI;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractJiraSyncColumnUI extends BackgroundLoadingPreComputedColumnUI {

   private static final LoadingCache<IAtsWorkItem, JiraSearch> workIdtoJiraSearch = CacheBuilder.newBuilder() //
      .expireAfterWrite(5, TimeUnit.MINUTES).build( //
         new CacheLoader<IAtsWorkItem, JiraSearch>() {
            @Override
            public JiraSearch load(IAtsWorkItem workItem) {
               return performSearch(workItem);
            }
         });
   private static final LoadingCache<IAtsWorkItem, AtsUser> workIdtoJiraAssignee = CacheBuilder.newBuilder() //
      .expireAfterWrite(5, TimeUnit.MINUTES).build( //
         new CacheLoader<IAtsWorkItem, AtsUser>() {
            @Override
            public AtsUser load(IAtsWorkItem workItem) {
               return loadAssignee(workItem);
            }
         });

   protected AbstractJiraSyncColumnUI(CoreCodeColumnTokenDefault codeCol) {
      super(codeCol);
   }

   protected static AtsUser getAssignee(IAtsWorkItem workItem) {
      try {
         return workIdtoJiraAssignee.get(workItem);
      } catch (Exception ex) {
         OseeLog.log(AbstractJiraSyncColumnUI.class, Level.WARNING,
            "Exception getting assignee: " + Lib.exceptionToString(ex));
      }
      return null;
   }

   public static AtsUser loadAssignee(IAtsWorkItem workItem) {
      JiraSearch srch = search(workItem);
      if (!srch.issues.isEmpty() && srch.issues != null) {
         String userId = srch.issues.iterator().next().getAssigneeUserId();
         if (Strings.isValid(userId)) {
            AtsUser user = AtsApiService.get().getUserService().getUserByLoginId(userId);
            return user;
         }
      }
      return AtsCoreUsers.UNASSIGNED_USER;
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      return SprintColumn.getTextValue(workItem, AtsApiService.get());
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         // refresh data; maybe open full report
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         // refresh data
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static JiraSearch search(IAtsWorkItem workItem) {
      try {
         return workIdtoJiraSearch.get(workItem);
      } catch (Exception ex) {
         JiraSearch srch = new JiraSearch();
         srch.getRd().errorf(Lib.exceptionToString(ex));
         return srch;
      }
   }

   protected static JiraSearch performSearch(IAtsWorkItem workItem) {
      try {
         JiraEndpoint jiraEndpoint = AtsApiService.get().getServerEndpoints().getJiraEndpoint();
         String json = JiraSearches.getTwSearch(workItem, getTeamId(workItem));
         String searchResults = jiraEndpoint.searchJira(json);
         if (searchResults.contains("errorMessages")) {
            JiraSearch srch = new JiraSearch();
            srch.getRd().errorf(searchResults);
            return srch;
         }
         JiraSearch srch = JsonUtil.readValue(searchResults, JiraSearch.class);
         return srch;
      } catch (Exception ex) {
         JiraSearch srch = new JiraSearch();
         srch.getRd().errorf(Lib.exceptionToString(ex));
         return srch;
      }
   }

   protected static Integer getTeamId(IAtsWorkItem workItem) {
      Integer teamId = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(
         workItem.getParentTeamWorkflow().getTeamDefinition(), AtsAttributeTypes.JiraTeamId, -1L).intValue();
      if (teamId <= 0) {
         throw new OseeArgumentException("Not JIRA Team Id specified for %s",
            workItem.getParentTeamWorkflow().getTeamDefinition().toStringWithId());
      }
      return teamId;
   }

}
