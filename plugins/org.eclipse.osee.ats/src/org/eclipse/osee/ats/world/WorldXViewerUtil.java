/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.column.StateAssigneesColumn;
import org.eclipse.osee.ats.column.StateCompletedColumn;
import org.eclipse.osee.ats.config.AtsConfigurationUtil;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewerUtil {

   public static void registerOtherColumns(XViewerFactory factory) {
      registerAtsAttributeColumns(factory);
      registerPluginColumns(factory);
      registerStateColumns(factory);
      registerConfigurationsColumns(factory);
   }

   public static void registerPluginColumns(XViewerFactory factory) {
      // Register any columns from other plugins
      try {
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            for (XViewerColumn xCol : item.getXViewerColumns()) {
               factory.registerColumns(xCol);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public static void registerAtsAttributeColumns(XViewerFactory factory) {
      // Register all ats.* attribute columns
      try {
         for (AttributeType attributeType : AttributeTypeManager.getAllTypes()) {
            if (attributeType.getName().startsWith("ats.")) {
               factory.registerColumns(SkynetXViewerFactory.getAttributeColumn(attributeType));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public static void registerConfigurationsColumns(XViewerFactory factory) {
      List<AtsAttributeValueColumn> columns = AtsConfigurationUtil.getConfigurations().getViews().getAttrColumns();
      for (AtsAttributeValueColumn column : columns) {
         try {
            factory.registerColumns(new XViewerAtsAttributeValueColumn(
               AttributeTypeManager.getTypeByGuid(column.getAttrTypeId()), column.getWidth(),
               getSwtAlign(column.getAlign()), column.isVisible(), SortDataType.valueOf(column.getSortDataType()),
               column.isColumnMultiEdit(), column.getDescription()));

         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private static int getSwtAlign(ColumnAlign align) {
      if (align == ColumnAlign.Left) {
         return SWT.LEFT;
      } else if (align == ColumnAlign.Center) {
         return SWT.CENTER;
      } else if (align == ColumnAlign.Right) {
         return SWT.RIGHT;
      }
      return 0;
   }

   public static void registerStateColumns(XViewerFactory factory) {
      for (String stateName : AtsWorkDefinitionSheetProviders.getAllValidStateNames()) {
         factory.registerColumns(new StateAssigneesColumn(stateName));
      }
      for (String stateName : AtsWorkDefinitionSheetProviders.getAllValidStateNames()) {
         factory.registerColumns(new StateCompletedColumn(stateName));
      }
   }

}
