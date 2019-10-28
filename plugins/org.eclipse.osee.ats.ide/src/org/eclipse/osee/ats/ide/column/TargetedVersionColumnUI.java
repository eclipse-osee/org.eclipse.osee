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

import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Donald G. Dunne
 */
public class TargetedVersionColumnUI extends AbstractVersionSelector {

   public static TargetedVersionColumnUI instance = new TargetedVersionColumnUI();

   public TargetedVersionColumnUI() {
      super(AtsColumnToken.TargtedVersionColumn);
   }

   public static TargetedVersionColumnUI getInstance() {
      return instance;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TargetedVersionColumnUI copy() {
      TargetedVersionColumnUI newXCol = new TargetedVersionColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public RelationTypeSide getRelation() {
      return AtsRelationTypes.TeamWorkflowTargetedForVersion_Version;
   }

}
