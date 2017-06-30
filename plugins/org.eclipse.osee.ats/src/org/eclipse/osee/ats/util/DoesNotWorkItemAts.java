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
package org.eclipse.osee.ats.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemAts extends XNavigateItemAction {

   public DoesNotWorkItemAts(XNavigateItem parent) {
      super(parent, "Does Not Work - ATS - Task Realated To Artifact", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      XResultData results = new XResultData(false);
      results.addRaw("[");

      WorldXViewerFactory factory = new WorldXViewerFactory();
      for (XViewerColumn column : factory.getColumns()) {
         if (column instanceof XViewerAtsAttributeValueColumn) {
            XViewerAtsAttributeValueColumn attrColumn = (XViewerAtsAttributeValueColumn) column;

            AttributeType attrType = AttributeTypeManager.getType(attrColumn.getAttributeType());

            AtsAttributeValueColumn valueColumn = new AtsAttributeValueColumn();
            valueColumn.setAttrTypeId(attrType.getId());
            valueColumn.setAttrTypeName(attrType.getName());
            valueColumn.setWidth(column.getWidth());
            valueColumn.setAlign(AtsUtil.getColumnAlign(column.getAlign()));
            valueColumn.setVisible(column.isShow());
            valueColumn.setSortDataType(column.getSortDataType().name());
            valueColumn.setColumnMultiEdit(column.isMultiColumnEditable());
            valueColumn.setDescription(column.getDescription());
            valueColumn.setNamespace("org.eclipse.osee.ats.WorldXViewer");
            valueColumn.setName(column.getName());

            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = "";
            try {
               jsonInString = mapper.writeValueAsString(valueColumn);
            } catch (Exception ex) {
               ex.printStackTrace();
            }

            if (Strings.isValid(jsonInString)) {
               results.log(jsonInString);
               results.log(",\n");
            }
         }
      }
      results.addRaw("]");
      XResultDataUI.report(results, "views.json");
   }

}
