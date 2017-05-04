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
package org.eclipse.osee.ats.column;

import java.util.Collection;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class ParentIdColumn extends XViewerAtsColumn implements IAtsXViewerPreComputedColumn {

   public static ParentIdColumn instance = new ParentIdColumn();

   public static ParentIdColumn getInstance() {
      return instance;
   }

   private ParentIdColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".parentid", "Parent Id", 75, XViewerAlign.Left, false,
         SortDataType.String, false, "ID of Parent Action or Team Workflow");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ParentIdColumn copy() {
      ParentIdColumn newXCol = new ParentIdColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   public String getText(Object element) {
      try {
         if (element instanceof AbstractWorkflowArtifact && ((AbstractWorkflowArtifact) element).getParentAWA() != null) {
            return AtsClientService.get().getWorkItemService().getCombinedPcrId(
               ((AbstractWorkflowArtifact) element).getParentAWA());
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object obj : objects) {
         preComputedValueMap.put(getKey(obj), getText(obj));
      }
   }

}
