/*********************************************************************
 * Copyright (c) 2011 Boeing
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

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.internal.PercentCompleteSMAStateUtil;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteSMAStateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteSMAStateColumn instance = new PercentCompleteSMAStateColumn();

   public static PercentCompleteSMAStateColumn getInstance() {
      return instance;
   }

   private PercentCompleteSMAStateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".statePercentComplete", "State Percent Complete", 40,
         XViewerAlign.Center, false, SortDataType.Percent, false,
         "Percent Complete for the changes to the current state.\n\nAmount entered from user.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteSMAStateColumn copy() {
      PercentCompleteSMAStateColumn newXCol = new PercentCompleteSMAStateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return String.valueOf(PercentCompleteSMAStateUtil.getPercentCompleteSMAState(
               AtsApiService.get().getQueryServiceIde().getArtifact(element)));
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
