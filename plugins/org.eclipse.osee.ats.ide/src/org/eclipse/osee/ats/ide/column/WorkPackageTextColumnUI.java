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

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttrTokenXColumn;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * Provides string based Work Package widget
 *
 * @author Donald G. Dunne
 */
public class WorkPackageTextColumnUI extends XViewerAtsAttrTokenXColumn {

   public static WorkPackageTextColumnUI instance = new WorkPackageTextColumnUI();

   public static WorkPackageTextColumnUI getInstance() {
      return instance;
   }

   protected WorkPackageTextColumnUI() {
      super(AtsAttributeTypes.WorkPackage, WorldXViewerFactory.COLUMN_NAMESPACE + ".workPackage", "Work Package (text)",
         80, XViewerAlign.Left, false, SortDataType.String, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public WorkPackageTextColumnUI copy() {
      WorkPackageTextColumnUI newXCol = new WorkPackageTextColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (Artifacts.isOfType(element, AtsArtifactTypes.Task)) {
         try {
            return getColumnText(((TaskArtifact) element).getParentTeamWorkflow(), column, columnIndex);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return super.getColumnText(element, column, columnIndex);
   }
}
