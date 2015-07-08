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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.util.ColorColumns;
import org.eclipse.osee.ats.core.column.ColorTeamColumn;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.rest.internal.util.RestUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Donald G. Dunne
 */
public class UpdateAtsConfiguration {

   private final Gson gson;
   private final IAtsServer atsServer;
   private static final String VIEWS_KEY = "views";
   private static final String VIEWS_EQUAL_KEY = VIEWS_KEY + "=";
   private static final String COLOR_COLUMN_KEY = "colorColumns";
   private static final String COLOR_COLUMN_EQUAL_KEY = COLOR_COLUMN_KEY + "=";

   public UpdateAtsConfiguration(IAtsServer atsServer) {
      this.atsServer = atsServer;
      gson = new GsonBuilder().setPrettyPrinting().create();
   }

   public XResultData createUpdateConfig(XResultData rd) {
      ArtifactReadable userArt = atsServer.getArtifact(AtsCoreUsers.SYSTEM_USER);
      getOrCreateConfigFolder(userArt, rd);
      getOrCreateAtsConfig(userArt, rd);
      return rd;
   }

   private TransactionBuilder setConfigAttributes(ArtifactReadable configArt, ArtifactReadable userArt, TransactionBuilder tx, XResultData rd) throws OseeCoreException {
      try {
         String viewsJson = RestUtil.getResource("support/views.json");
         AtsViews defaultViews = gson.fromJson(viewsJson, AtsViews.class);
         AtsViews databaseViews = getConfigViews();
         if (databaseViews.getAttrColumns().isEmpty()) {
            tx = getOrCreateTx(userArt, tx);
            tx.createAttribute(configArt, CoreAttributeTypes.GeneralStringData, createViewsAttrValue(defaultViews));
            rd.log("Creating VIEWS attribute\n");
         } else {
            // merge any new default view items to current database view items
            List<AtsAttributeValueColumn> toAdd = new LinkedList<AtsAttributeValueColumn>();
            for (AtsAttributeValueColumn defaultView : defaultViews.getAttrColumns()) {
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
            Iterator<? extends AttributeReadable<Object>> iterator =
               configArt.getAttributes(CoreAttributeTypes.GeneralStringData, DeletionFlag.EXCLUDE_DELETED).iterator();
            while (iterator.hasNext()) {
               AttributeReadable<Object> attributeReadable = iterator.next();
               if (((String) attributeReadable.getValue()).startsWith(VIEWS_EQUAL_KEY)) {
                  tx = getOrCreateTx(userArt, tx);
                  tx.setAttributeById(configArt, attributeReadable, createViewsAttrValue(databaseViews));
                  rd.log("Create or update AtsConfig.VIEWS attribute\n");
                  break;
               }
            }
         }
      } catch (Exception ex) {
         throw new OseeWrappedException("Error loading column views.json file", ex);
      }
      return tx;
   }

   private TransactionBuilder getOrCreateTx(ArtifactReadable userArt, TransactionBuilder tx) {
      if (tx == null) {
         tx = atsServer.getOrcsApi().getTransactionFactory().createTransaction(CoreBranches.COMMON, userArt,
            "Update AtsConfig attributes");
      }
      return tx;
   }

   private String createViewsAttrValue(AtsViews defaultViews) {
      return VIEWS_EQUAL_KEY + gson.toJson(defaultViews);
   }

   @SuppressWarnings("unchecked")
   public ArtifactId getOrCreateConfigFolder(ArtifactReadable userArt, XResultData rd) {
      ArtifactReadable configFolderArt =
         atsServer.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(
            AtsArtifactToken.ConfigFolder).getResults().getAtMostOneOrNull();
      if (configFolderArt == null) {
         TransactionBuilder tx = atsServer.getOrcsApi().getTransactionFactory().createTransaction(CoreBranches.COMMON,
            userArt, "Create Config Folder");
         ArtifactReadable headingFolderArt =
            atsServer.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(
               AtsArtifactToken.HeadingFolder).getResults().getExactlyOne();
         configFolderArt = (ArtifactReadable) tx.createArtifact(AtsArtifactToken.ConfigFolder);
         tx.relate(headingFolderArt, CoreRelationTypes.Default_Hierarchical__Parent, configFolderArt);
         tx.commit();
         rd.log("Created Config Folder");
      }

      return configFolderArt;
   }

   @SuppressWarnings("unchecked")
   public ArtifactId getOrCreateAtsConfig(ArtifactReadable userArt, XResultData rd) {
      ArtifactReadable atsConfigArt = atsServer.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(
         AtsArtifactToken.AtsConfig).getResults().getAtMostOneOrNull();
      if (atsConfigArt == null) {
         TransactionBuilder tx = atsServer.getOrcsApi().getTransactionFactory().createTransaction(CoreBranches.COMMON,
            userArt, "Create AtsConfig");
         ArtifactReadable headingFolderArt = (ArtifactReadable) getOrCreateConfigFolder(userArt, rd);
         atsConfigArt = (ArtifactReadable) tx.createArtifact(AtsArtifactToken.AtsConfig);
         tx.relate(headingFolderArt, CoreRelationTypes.Default_Hierarchical__Parent, atsConfigArt);
         setConfigAttributes(atsConfigArt, userArt, tx, rd);
         setColorColumnAttributes(atsConfigArt, userArt, tx, rd);
         tx.commit();
         rd.log("Created AtsConfig");
      } else {
         TransactionBuilder tx = setConfigAttributes(atsConfigArt, userArt, null, rd);
         if (tx != null) {
            tx.commit();
         }
      }

      return atsConfigArt;
   }

   private void setColorColumnAttributes(ArtifactReadable atsConfigArt, ArtifactReadable userArt, TransactionBuilder tx, XResultData rd) {
      ColorColumns columns = new ColorColumns();
      columns.addColumn(ColorTeamColumn.getColor());
      String colorColumnsJson = gson.toJson(columns);

      Iterator<? extends AttributeReadable<Object>> iterator =
         atsConfigArt.getAttributes(CoreAttributeTypes.GeneralStringData, DeletionFlag.EXCLUDE_DELETED).iterator();
      boolean found = false;
      while (iterator.hasNext()) {
         AttributeReadable<Object> attributeReadable = iterator.next();
         if (((String) attributeReadable.getValue()).startsWith(COLOR_COLUMN_EQUAL_KEY)) {
            tx = getOrCreateTx(userArt, tx);
            tx.setAttributeById(atsConfigArt, attributeReadable, colorColumnsJson);
            found = true;
            rd.log("Create or update AtsConfig.colorColumn attribute\n");
            break;
         }
      }
      if (!found) {
         tx = getOrCreateTx(userArt, tx);
         tx.createAttribute(atsConfigArt, CoreAttributeTypes.GeneralStringData,
            COLOR_COLUMN_EQUAL_KEY + colorColumnsJson);
      }

   }

   @SuppressWarnings("unchecked")
   public ArtifactId getOrCreateConfigsFolder(ArtifactReadable userArt, XResultData rd) {
      ArtifactId configFolderArt = getOrCreateConfigFolder(userArt, rd);
      ArtifactId configsFolderArt = atsServer.getOrcsApi().getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(
         AtsArtifactToken.ConfigsFolder).getResults().getAtMostOneOrNull();
      if (configsFolderArt == null) {
         TransactionBuilder tx = atsServer.getOrcsApi().getTransactionFactory().createTransaction(CoreBranches.COMMON,
            userArt, "Create Configs Folder");
         configsFolderArt = tx.createArtifact(AtsArtifactToken.ConfigsFolder);
         tx.relate(configsFolderArt, CoreRelationTypes.Default_Hierarchical__Parent, configFolderArt);
         tx.commit();
         rd.log("Created Configs Folder");
      }
      return configsFolderArt;
   }

   public AtsViews getConfigViews() {
      String viewsStr = atsServer.getConfigValue(VIEWS_KEY);
      AtsViews views = null;
      if (Strings.isValid(viewsStr)) {
         views = gson.fromJson(viewsStr, AtsViews.class);
      } else {
         views = new AtsViews();
      }
      return views;
   }

   public ColorColumns getColorColumns() {
      String colorStr = atsServer.getConfigValue(COLOR_COLUMN_KEY);
      ColorColumns columns = null;
      if (Strings.isValid(colorStr)) {
         columns = gson.fromJson(colorStr, ColorColumns.class);
      } else {
         columns = new ColorColumns();
      }
      return columns;
   }

}
