/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnToken {

   public static AtsAttributeValueColumn TitleColumn = new AtsAttributeValueColumn(CoreAttributeTypes.Name,
      "framework.artifact.name.Title", "Title", 150, ColumnAlign.Left.name(), true, ColumnType.String, true, "", true);
   public static AtsAttributeValueColumn LegacyPcrIdColumn = new AtsAttributeValueColumn(AtsAttributeTypes.LegacyPcrId,
      AtsColumnId.LegacyPcrId.getId(), AtsAttributeTypes.LegacyPcrId.getUnqualifiedName(), 40, ColumnAlign.Left.name(),
      false, ColumnType.String, false, "");

}
