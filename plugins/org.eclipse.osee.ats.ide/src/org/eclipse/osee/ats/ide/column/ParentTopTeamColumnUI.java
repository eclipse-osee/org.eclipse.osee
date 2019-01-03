/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.column.ParentTopTeamColumn;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class ParentTopTeamColumnUI extends XViewerAtsColumn implements IXViewerPreComputedColumn {

   private final static ParentTopTeamColumnUI instance = new ParentTopTeamColumnUI();

   public static ParentTopTeamColumnUI getInstance() {
      return instance;
   }

   private ParentTopTeamColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".topTeam", "Parent Top Team", 50, XViewerAlign.Left, false,
         SortDataType.String, false,
         "Top Team (if available) or parent Team that has been assigned to work this Action.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ParentTopTeamColumnUI copy() {
      ParentTopTeamColumnUI newXCol = new ParentTopTeamColumnUI();
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
            String result = "";
            if (element instanceof IAtsObject) {
               try {
                  result = ParentTopTeamColumn.getColumnText((IAtsObject) element);
               } catch (OseeCoreException ex) {
                  result = XViewerCells.getCellExceptionString(ex);
               }
            }
            preComputedValueMap.put(getKey(element), result);
         } catch (OseeCoreException ex) {
            preComputedValueMap.put(getKey(element), LogUtil.getCellExceptionString(ex));
         }
      }
   }

}
