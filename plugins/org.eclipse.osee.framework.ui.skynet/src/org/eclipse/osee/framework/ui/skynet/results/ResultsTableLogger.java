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

package org.eclipse.osee.framework.ui.skynet.results;

import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

public class ResultsTableLogger extends OperationLogger {
   private final ResultsEditorTableTab resultsTab;

   public ResultsTableLogger(ResultsEditorTableTab resultsTab) {
      this.resultsTab = resultsTab;
   }

   @Override
   public void log(String... row) {
      resultsTab.addRow(new ResultsXViewerRow(row));
   }
}