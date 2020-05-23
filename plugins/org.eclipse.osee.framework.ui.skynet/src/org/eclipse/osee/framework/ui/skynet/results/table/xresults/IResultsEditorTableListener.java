/*********************************************************************
 * Copyright (c) 2013 Boeing
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
