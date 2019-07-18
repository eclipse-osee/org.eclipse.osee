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
package org.eclipse.osee.framework.ui.skynet.results.table.xresults;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

public interface IResultsEditorTableListener {

   default void handleDoubleClick(ArrayList<ResultsXViewerRow> selectedRows) {
      // do nothing
   }

   default void handleSelectionListener(Collection<ResultsXViewerRow> selectedRows) {
      // do nothing
   }

}
