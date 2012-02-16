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

import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.swt.SWT;

public class NotesColumn extends XViewerAtsAttributeValueColumn {

   public static NotesColumn instance = new NotesColumn();

   public static NotesColumn getInstance() {
      return instance;
   }

   private NotesColumn() {
      super(AtsAttributeTypes.SmaNote, WorldXViewerFactory.COLUMN_NAMESPACE + ".notes", "Notes", 80, SWT.LEFT, true,
         SortDataType.String, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public NotesColumn copy() {
      NotesColumn newXCol = new NotesColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean isMultiLineStringAttribute() {
      return true;
   }

}
