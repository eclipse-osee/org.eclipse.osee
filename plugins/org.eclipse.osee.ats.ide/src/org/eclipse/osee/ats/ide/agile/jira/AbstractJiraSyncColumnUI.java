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
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault.CoreCodeColumnTokenDefault;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.jira.JiraSearch;
import org.eclipse.osee.ats.core.column.SprintColumn;
import org.eclipse.osee.ats.ide.column.BackgroundLoadingPreComputedColumnUI;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
               return AtsApiService.get().getJiraService().search(workItem);
            }
         });
   private static final LoadingCache<IAtsWorkItem, AtsUser> workIdtoJiraAssignee = CacheBuilder.newBuilder() //
      .expireAfterWrite(5, TimeUnit.MINUTES).build( //
         new CacheLoader<IAtsWorkItem, AtsUser>() {
            @Override
            public AtsUser load(IAtsWorkItem workItem) {
               return AtsApiService.get().getJiraService().getJiraAssignee(workItem);
            }
         });

   protected AbstractJiraSyncColumnUI(CoreCodeColumnTokenDefault codeCol) {
      super(codeCol);
   }

   public static AtsUser getJiraAssignee(IAtsWorkItem workItem) {
      try {
         return workIdtoJiraAssignee.get(workItem);
      } catch (Exception ex) {
         OseeLog.log(AbstractJiraSyncColumnUI.class, Level.WARNING,
            "Exception getting assignee: " + Lib.exceptionToString(ex));
      }
      return null;
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

}
