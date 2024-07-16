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

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttrTokenXColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class GoalOrderVoteColumnUI extends XViewerAtsAttrTokenXColumn {

   public static final Integer DEFAULT_WIDTH = 40;
   public static GoalOrderVoteColumnUI instance = new GoalOrderVoteColumnUI();

   public static GoalOrderVoteColumnUI getInstance() {
      return instance;
   }

   private GoalOrderVoteColumnUI() {
      super(AtsAttributeTypes.GoalOrderVote, WorldXViewerFactory.COLUMN_NAMESPACE + ".goalOrderVote",
         AtsAttributeTypes.GoalOrderVote.getUnqualifiedName(), DEFAULT_WIDTH, XViewerAlign.Left, false,
         SortDataType.String, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GoalOrderVoteColumnUI copy() {
      GoalOrderVoteColumnUI newXCol = new GoalOrderVoteColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return AtsApiService.get().getQueryServiceIde().getArtifact(element).getSoleAttributeValue(
               AtsAttributeTypes.GoalOrderVote, "");
         }
      } catch (Exception ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
