/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.config.IAtsConfigurationViewsProvider;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.ColorColumns;
import org.eclipse.osee.ats.core.column.ColorTeamColumn;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
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
   private static final String VIEWS_KEY = "views";
   private static final String VIEWS_EQUAL_KEY = VIEWS_KEY + "=";
   private static final String COLOR_COLUMN_KEY = "colorColumns";
   public static final String VALID_STATE_NAMES_KEY = "validStateNames";

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public UpdateAtsConfiguration(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public XResultData createUpdateConfig(XResultData rd) {
      ArtifactReadable userArt =
         (ArtifactReadable) atsApi.getQueryService().getArtifact((IAtsObject) AtsCoreUsers.SYSTEM_USER);
      ArtifactId configFolder = getOrCreateConfigFolder(userArt, rd);
      ArtifactReadable atsConfigArt = (ArtifactReadable) getOrCreateAtsConfig(userArt, rd);
      createRuleDefinitions(userArt, configFolder, rd);
      createUpdateColorColumnAttributes(atsConfigArt, userArt, rd);
      createUpdateConfigAttributes(atsConfigArt, userArt, rd);
      try {
         createUpdateValidStateAttributes(atsConfigArt, userArt, rd);
      } catch (Exception ex) {
         rd.errorf("Error in createUpdateValidStateAttributes [%s]", Lib.exceptionToString(ex));
      }
      atsApi.setConfigValue(AtsUtil.SERVER_CONFIG_RELOAD_MIN_KEY, AtsUtil.SERVER_CONFIG_RELOAD_MIN_KEY);
      return rd;
   }

   private void createRuleDefinitions(ArtifactReadable userArt, ArtifactId configFolderArt, XResultData rd) {
      try {
         if ((ArtifactReadable) atsApi.getQueryService().getArtifact(AtsArtifactToken.RuleDefinitions) == null) {
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userArt,
               "Add Rule Definitions Artifact");
            ArtifactId ruleDefConfigArt = tx.createArtifact(AtsArtifactToken.RuleDefinitions);
            String ruleDefs = OseeInf.getResourceContents("atsConfig/ruleDefinitions.ats", getClass());
            tx.createAttribute(ruleDefConfigArt, AtsAttributeTypes.DslSheet, ruleDefs);
            if (rd.isErrors()) {
               throw new OseeStateException(rd.toString());
            }
            tx.relate(configFolderArt, CoreRelationTypes.Default_Hierarchical__Child, ruleDefConfigArt);
            tx.commit();
         }
      } catch (Exception ex) {
         OseeLog.log(UpdateAtsConfiguration.class, Level.SEVERE, ex);
         rd.error("Error loading column ruleDefinitions.ats file (see log for details) " + ex.getLocalizedMessage());
      }
   }

   private List<String> getViewsJsonStrings() throws Exception {
      List<String> viewsJson = new LinkedList<>();
      viewsJson.add(OseeInf.getResourceContents("atsConfig/views.json", getClass()));
      for (IAtsConfigurationViewsProvider provider : AtsConfigurationViewsService.getViewsProviders()) {
         viewsJson.add(provider.getViewsJson());
      }
      return viewsJson;
   }

   private void createUpdateConfigAttributes(ArtifactReadable configArt, ArtifactReadable userArt, XResultData rd) {
      try {
         AtsViews databaseViews = getConfigViews();
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

         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userArt,
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

   public ArtifactId getOrCreateConfigFolder(ArtifactId userArt, XResultData rd) {
      ArtifactToken configFolderArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.ConfigFolder);
      if (configFolderArt == null) {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userArt, "Create Config Folder");
         ArtifactToken headingFolderArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.HeadingFolder);
         configFolderArt = tx.createArtifact(AtsArtifactToken.ConfigFolder);
         tx.relate(headingFolderArt, CoreRelationTypes.Default_Hierarchical__Parent, configFolderArt);
         tx.commit();
         rd.log("Created Config Folder");
      }
      return configFolderArt;
   }

   public ArtifactId getOrCreateAtsConfig(ArtifactReadable userArt, XResultData rd) {
      ArtifactToken atsConfigArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.AtsConfig);
      if (atsConfigArt == null) {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userArt, "Create AtsConfig");
         ArtifactReadable headingFolderArt = (ArtifactReadable) getOrCreateConfigFolder(userArt, rd);
         atsConfigArt = tx.createArtifact(AtsArtifactToken.AtsConfig);
         tx.relate(headingFolderArt, CoreRelationTypes.Default_Hierarchical__Parent, atsConfigArt);
         tx.commit();
         rd.log("Created AtsConfig");
      }
      return atsConfigArt;
   }

   private void createUpdateColorColumnAttributes(ArtifactReadable atsConfigArt, ArtifactReadable userArt, XResultData rd) {
      ColorColumns columns = new ColorColumns();
      columns.addColumn(ColorTeamColumn.getColor());
      String colorColumnsJson = JsonUtil.toJson(columns);
      atsApi.setConfigValue(COLOR_COLUMN_KEY, colorColumnsJson);
   }

   private void createUpdateValidStateAttributes(ArtifactReadable atsConfigArt, ArtifactReadable userArt, XResultData rd) throws Exception {

      Collection<String> validStateNames = atsApi.getWorkDefinitionService().getAllValidStateNames(new XResultData());
      atsApi.setConfigValue(VALID_STATE_NAMES_KEY, Collections.toString(",", validStateNames));
   }

   public ArtifactId getOrCreateConfigsFolder(ArtifactId userArt, XResultData rd) {
      ArtifactId configFolderArt = getOrCreateConfigFolder(userArt, rd);
      ArtifactId configsFolderArt = atsApi.getQueryService().getArtifact(AtsArtifactToken.ConfigsFolder);
      if (configsFolderArt == null) {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, userArt, "Create Configs Folder");
         configsFolderArt = tx.createArtifact(AtsArtifactToken.ConfigsFolder);
         tx.relate(configsFolderArt, CoreRelationTypes.Default_Hierarchical__Parent, configFolderArt);
         tx.commit();
         rd.log("Created Configs Folder");
      }
      return configsFolderArt;
   }

   public Collection<String> getValidStateNames() {
      String stateNamesStr = atsApi.getConfigValue(VALID_STATE_NAMES_KEY);
      List<String> stateNames = new LinkedList<>();
      if (Strings.isValid(stateNamesStr)) {
         for (String stateName : stateNamesStr.split(",")) {
            stateNames.add(stateName);
         }
      }
      return stateNames;
   }

   public AtsViews getConfigViews() {
      String viewsStr = atsApi.getConfigValue(VIEWS_KEY);
      AtsViews views = null;
      if (Strings.isValid(viewsStr)) {
         views = JsonUtil.readValue(viewsStr, AtsViews.class);
      } else {
         views = new AtsViews();
      }
      return views;
   }

   public ColorColumns getColorColumns() {
      String colorStr = atsApi.getConfigValue(COLOR_COLUMN_KEY);
      ColorColumns columns = null;
      if (Strings.isValid(colorStr)) {
         columns = JsonUtil.readValue(colorStr, ColorColumns.class);
      } else {
         columns = new ColorColumns();
      }
      return columns;
   }

}
