/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.column;

import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class ParentAtsIdColumn extends BackgroundLoadingColumn {

   public static ParentAtsIdColumn instance = new ParentAtsIdColumn();

   public static ParentAtsIdColumn getInstance() {
      return instance;
   }

   private ParentAtsIdColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".parentatsid", "Parent ATS ID", 75, XViewerAlign.Left, false,
         SortDataType.String, false, "ATS ID of Parent Action or Team Workflow");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ParentAtsIdColumn copy() {
      ParentAtsIdColumn newXCol = new ParentAtsIdColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      try {
         if (workItem.isTeamWorkflow()) {
            IAtsAction parentAction = workItem.getParentAction();
            return parentAction == null ? "" : parentAction.getAtsId();
         } else {
            return workItem.getParentTeamWorkflow().getAtsId();
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
   }

}
