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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation;

/**
 * @author Ryan D. Brooks
 */
public class MultiPageResultsProvider implements IResultsEditorProvider {
   private final DatabaseHealthOperation healthOperation;
   private final List<IResultsEditorTab> resultsTabs = new ArrayList<>(4);

   public MultiPageResultsProvider(DatabaseHealthOperation healthOperation) {
      super();
      this.healthOperation = healthOperation;
   }

   @Override
   public String getEditorName() {
      return healthOperation.getName();
   }

   @Override
   public List<IResultsEditorTab> getResultsEditorTabs() {
      return resultsTabs;
   }

   public void addResultsTab(IResultsEditorTab resultsTab) {
      resultsTabs.add(resultsTab);
   }

   public void clearTabs() {
      resultsTabs.clear();
   }
}