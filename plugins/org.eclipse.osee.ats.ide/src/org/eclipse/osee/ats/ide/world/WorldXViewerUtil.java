/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.config.AtsAttrValCol;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewerUtil {

   public static void registerOtherColumns(SkynetXViewerFactory factory) {
      registerAtsAttributeColumns(factory);
      registerPluginColumns(factory);
      registerConfigurationsColumns(factory);
   }

   public static void registerPluginColumns(SkynetXViewerFactory factory) {
      // Register any columns from other plugins
      try {
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {

            Collection<IUserGroupArtifactToken> itemGroups = item.getUserGroups();
            if (AtsApiService.get().userService().isInUserGroup(
               itemGroups.toArray(new IUserGroupArtifactToken[itemGroups.size()]))) {
               for (XViewerColumn xCol : item.getXViewerColumns()) {
                  factory.registerColumns(xCol);
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   // Add Access controlled - show all attr columns
   public static void registerAtsAttributeColumns(SkynetXViewerFactory factory) {
      try {
         if (AtsApiService.get().userService().isInUserGroup(AtsUserGroups.AtsAddAttrColumns)) {
            for (AttributeTypeToken attributeType : AttributeTypeManager.getAllTypes()) {
               if (attributeType.getName().startsWith("ats.")) {
                  factory.registerColumns(SkynetXViewerFactory.getAttributeColumn(attributeType));
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public static void registerConfigurationsColumns(SkynetXViewerFactory factory) {
      List<XViewerAtsAttributeValueColumn> configColumns = getConfigurationColumns();
      for (XViewerAtsAttributeValueColumn col : configColumns) {
         factory.registerColumns(col);
      }
   }

   public static List<XViewerAtsAttributeValueColumn> getConfigurationColumns() {
      List<AtsAttrValCol> columns =
         AtsApiService.get().getConfigService().getConfigurations().getViews().getAttrColumns();
      List<XViewerAtsAttributeValueColumn> configColumns = new ArrayList<>();
      for (AtsAttrValCol column : columns) {
         try {
            AttributeTypeToken attrType = null;
            try {
               attrType = AttributeTypeManager.getAttributeType(column.getAttrTypeId());
            } catch (OseeTypeDoesNotExist ex) {
               continue;
            }
            XViewerAtsAttributeValueColumn valueColumn = new XViewerAtsAttributeValueColumn(attrType, column.getWidth(),
               AtsEditors.getXViewerAlign(column.getAlign()), column.isVisible(),
               SortDataType.valueOf(column.getSortDataType()), column.isColumnMultiEdit(), column.getDescription());
            valueColumn.setBooleanNotSetShow(column.getBooleanNotSetShow());
            valueColumn.setBooleanOnFalseShow(column.getBooleanOnFalseShow());
            valueColumn.setBooleanOnTrueShow(column.getBooleanOnTrueShow());
            valueColumn.setActionRollup(column.isActionRollup());
            valueColumn.setInheritParent(column.isInheritParent());
            configColumns.add(valueColumn);

         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return configColumns;
   }

   public static XViewerColumn getConfigColumn(String columnId, List<XViewerAtsAttributeValueColumn> configCols) {
      for (XViewerAtsAttributeValueColumn col : configCols) {
         if (col.getId().equals(columnId)) {
            return col;
         }
      }
      return null;
   }

   public static void addColumn(SkynetXViewerFactory factory, XViewerColumn taskCol, int width, List<XViewerColumn> sprintCols) {
      XViewerColumn newCol = taskCol.copy();
      newCol.setShow(true);
      newCol.setWidth(width);
      factory.registerColumns(newCol);
      sprintCols.add(newCol);
   }

}
