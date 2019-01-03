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

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.core.column.CompletedCancelledByColumn;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumnIdColumn;

/**
 * @author Donald G. Dunne
 */
public class CompletedCancelledByColumnUI extends XViewerAtsColumnIdColumn {

   public static CompletedCancelledByColumnUI instance = new CompletedCancelledByColumnUI();

   public static CompletedCancelledByColumnUI getInstance() {
      return instance;
   }

   public CompletedCancelledByColumnUI() {
      super(AtsColumnToken.CompletedCancelledByColumn);
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return CompletedCancelledByColumn.getCompletedCancelledBy(element, AtsClientService.get());
   }
}