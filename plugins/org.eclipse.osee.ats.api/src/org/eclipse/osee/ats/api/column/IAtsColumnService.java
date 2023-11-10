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

package org.eclipse.osee.ats.api.column;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAtsColumnService {

   String getColumnText(AtsCoreColumnToken column, IAtsObject atsObject);

   String getColumnText(String id, IAtsObject atsObject);

   AtsCoreColumn getColumn(AtsCoreColumnToken column);

   AtsCoreColumn getColumn(String id);

   String getColumnText(AtsConfigurations configurations, AtsCoreColumnToken column, IAtsObject atsObject);

   String getColumnText(AtsConfigurations configurations, String id, IAtsObject atsObject);

   Collection<IAtsColumnProvider> getColumProviders();

   Date getColumnDate(AtsCoreAttrTokColumnToken attrCol, IAtsWorkItem workItem);

   Date getColumnDate(AttributeTypeToken attrType, IAtsWorkItem element);

   Collection<AtsCoreColumn> getColumns();

   AtsCoreColumn getAttributeColumn(AttributeTypeToken attrType, String source);

   ColumnType getColumnType(AttributeTypeToken attrType);

   XResultData validateIdeColumns(List<XViewerColumn> ideColumns);

   void loadRemainingAtsWorkflowAttributes(String prefix);

   XResultData getLoadResults();

   String getIdFromLegacyId(String legacyId);

}
