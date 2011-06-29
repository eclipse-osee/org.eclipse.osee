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

import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.swt.SWT;

public class UserCommunityColumn extends XViewerAtsAttributeValueColumn {

   public static UserCommunityColumn instance = new UserCommunityColumn();

   public static UserCommunityColumn getInstance() {
      return instance;
   }

   private UserCommunityColumn() {
      super(AtsAttributeTypes.UserCommunity, 60, SWT.LEFT, false, SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public UserCommunityColumn copy() {
      UserCommunityColumn newXCol = new UserCommunityColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
