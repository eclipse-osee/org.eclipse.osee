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

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class GoalOrderVoteColumn extends XViewerAtsAttributeValueColumn {

   public static final Integer DEFAULT_WIDTH = 40;
   public static GoalOrderVoteColumn instance = new GoalOrderVoteColumn();

   public static GoalOrderVoteColumn getInstance() {
      return instance;
   }

   private GoalOrderVoteColumn() {
      super(AtsAttributeTypes.GoalOrderVote, WorldXViewerFactory.COLUMN_NAMESPACE + ".goalOrderVote",
         AtsAttributeTypes.GoalOrderVote.getUnqualifiedName(), DEFAULT_WIDTH, SWT.LEFT, false, SortDataType.String,
         true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GoalOrderVoteColumn copy() {
      GoalOrderVoteColumn newXCol = new GoalOrderVoteColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return ((Artifact) element).getSoleAttributeValue(AtsAttributeTypes.GoalOrderVote, "");
         }
      } catch (Exception ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
