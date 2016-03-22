/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.xviewer.column;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.util.IColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerIColumnAdapter extends XViewerColumn {

   public XViewerIColumnAdapter(IColumn column) {
      super(column.getId(), column.getName(), 80, XViewerAlign.Left, true,
         SortDataType.valueOf(column.getDataType().name()), false, column.getDescription());
   }
}
