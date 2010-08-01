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
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation;

/**
 * @author Ryan D. Brooks
 */
public class MultiPageResultsProvider implements IResultsEditorProvider {
   private final DatabaseHealthOperation healthOperation;
   private final List<IResultsEditorTab> resultsTabs = new ArrayList<IResultsEditorTab>(4);

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