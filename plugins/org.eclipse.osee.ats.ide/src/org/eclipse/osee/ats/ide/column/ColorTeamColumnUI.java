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
package org.eclipse.osee.ats.ide.column;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.column.ColorTeamColumn;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class ColorTeamColumnUI extends XViewerAtsAttributeValueColumn implements IXViewerPreComputedColumn {

   public static final Integer DEFAULT_WIDTH = 45;
   Map<Long, String> workItemIdToColorTeam = new HashMap<>(100);
   public static ColorTeamColumnUI instance = new ColorTeamColumnUI();

   public static ColorTeamColumnUI getInstance() {
      return instance;
   }

   public ColorTeamColumnUI() {
      super(AtsAttributeTypes.ColorTeam, ColorTeamColumn.ATS_COLOR_TEAM_COLUMN_ID, "Color Team", 45, XViewerAlign.Left,
         false, SortDataType.String, true, "Color Team associated by related Work Package");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ColorTeamColumnUI copy() {
      ColorTeamColumnUI newXCol = new ColorTeamColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public Long getKey(Object obj) {
      Long result = 0L;
      if (obj instanceof IAtsObject) {
         result = ((IAtsObject) obj).getId();
      }
      return result;
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return cachedValue;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object element : objects) {
         try {
            if (element instanceof IAtsWorkItem) {
               IAtsWorkItem workItem = (IAtsWorkItem) element;
               Pair<String, Boolean> result =
                  ColorTeamColumn.getWorkItemColorTeam(workItem, AtsClientService.get().getServices());
               preComputedValueMap.put(workItem.getId(), result.getFirst());
            }
         } catch (OseeCoreException ex) {
            LogUtil.getCellExceptionString(ex);
         }
      }
   }

}
