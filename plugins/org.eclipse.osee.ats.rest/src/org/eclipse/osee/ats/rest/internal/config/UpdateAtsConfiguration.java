/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.config.IAtsConfigurationViewsProvider;
import org.eclipse.osee.ats.api.util.ColorColumns;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class UpdateAtsConfiguration {
   public static final String VIEWS_KEY = "views";
   private static final String VIEWS_EQUAL_KEY = VIEWS_KEY + "=";
   public static final String COLOR_COLUMN_KEY = "colorColumns";
   public static final String VALID_STATE_NAMES_KEY = "validStateNames";

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public UpdateAtsConfiguration(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public XResultData createUpdateConfig(XResultData rd) {
      UserId user = SystemUser.OseeSystem;
      ArtifactReadable atsConfigArt = (ArtifactReadable) AtsDbConfigBase.getOrCreateAtsConfig(atsApi);
      createUpdateConfigAttributes(atsConfigArt, user, rd);
      try {
         createUpdateValidStateAttributes();
      } catch (Exception ex) {
         rd.errorf("Error in createUpdateValidStateAttributes [%s]", Lib.exceptionToString(ex));
      }
      return rd;
   }

   private List<String> getViewsJsonStrings() throws Exception {
      List<String> viewsJson = new LinkedList<>();
      viewsJson.add(OseeInf.getResourceContents("atsConfig/views.json", getClass()));
      for (IAtsConfigurationViewsProvider provider : AtsConfigurationViewsService.getViewsProviders()) {
         viewsJson.add(provider.getViewsJson());
      }
      return viewsJson;
   }

   private void createUpdateConfigAttributes(ArtifactReadable configArt, UserId userId, XResultData rd) {
      try {
         AtsViews databaseViews = atsApi.getConfigService().getConfigurations().getViews();
         for (String viewsJson : getViewsJsonStrings()) {
            AtsViews atsViews = JsonUtil.readValue(viewsJson, AtsViews.class);
            // merge any new default view items to current database view items
            List<AtsAttributeValueColumn> toAdd = new LinkedList<>();
            for (AtsAttributeValueColumn defaultView : atsViews.getAttrColumns()) {
               boolean found = false;
               for (AtsAttributeValueColumn dbView : databaseViews.getAttrColumns()) {
                  boolean defaultViewNameValid =
                     Strings.isValid(dbView.getName()) && Strings.isValid(defaultView.getName());
                  if (defaultViewNameValid && dbView.getName().equals(defaultView.getName())) {
                     found = true;
                     break;
                  }
                  if (!found && dbView.getAttrTypeName().equals(defaultView.getAttrTypeName())) {
                     found = true;
                     break;
                  }
               }
               if (!found) {
                  toAdd.add(defaultView);
               }
            }
            databaseViews.getAttrColumns().addAll(toAdd);
         }

         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userId,
            "Create Update Config Attributes");
         Iterator<? extends AttributeReadable<Object>> iterator =
            configArt.getAttributes(CoreAttributeTypes.GeneralStringData, DeletionFlag.EXCLUDE_DELETED).iterator();
         boolean found = false;
         while (iterator.hasNext()) {
            AttributeReadable<Object> attributeReadable = iterator.next();

            if (attributeReadable != null && ((String) attributeReadable.getValue()).startsWith(VIEWS_EQUAL_KEY)) {
               tx.setAttributeById(configArt, attributeReadable, getViewsAttrValue(databaseViews));
               rd.log("Create or update AtsConfig.VIEWS attribute\n");
               found = true;
               break;
            }
         }
         if (!found) {
            tx.createAttribute(configArt, CoreAttributeTypes.GeneralStringData, getViewsAttrValue(databaseViews));
            rd.log("Creating VIEWS attribute\n");
         }
         tx.commit();
      } catch (Exception ex) {
         OseeLog.log(UpdateAtsConfiguration.class, Level.SEVERE, ex);
         rd.error("Error loading column views.json file (see log for details) " + ex.getLocalizedMessage());
      }
   }

   private String getViewsAttrValue(AtsViews defaultViews) {
      return VIEWS_EQUAL_KEY + JsonUtil.toJson(defaultViews);
   }

   private void createUpdateValidStateAttributes() throws Exception {

      Collection<String> validStateNames = atsApi.getWorkDefinitionService().getAllValidStateNames(new XResultData());
      atsApi.setConfigValue(VALID_STATE_NAMES_KEY, Collections.toString(",", validStateNames));
   }

   public Collection<String> getValidStateNames(String stateNamesStr) {
      List<String> stateNames = new LinkedList<>();
      if (Strings.isValid(stateNamesStr)) {
         for (String stateName : stateNamesStr.split(",")) {
            stateNames.add(stateName);
         }
      }
      return stateNames;
   }

   public AtsViews getConfigViews(String viewsStr) {
      AtsViews views = null;
      if (Strings.isValid(viewsStr)) {
         views = JsonUtil.readValue(viewsStr, AtsViews.class);
      } else {
         views = new AtsViews();
      }
      return views;
   }

   public ColorColumns getColorColumns(String colorStr) {
      ColorColumns columns = null;
      if (Strings.isValid(colorStr)) {
         columns = JsonUtil.readValue(colorStr, ColorColumns.class);
      } else {
         columns = new ColorColumns();
      }
      return columns;
   }

}
