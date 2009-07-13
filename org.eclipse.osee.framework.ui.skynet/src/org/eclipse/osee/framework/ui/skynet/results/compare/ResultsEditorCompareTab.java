/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.results.compare;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorCompareTab implements IResultsEditorCompareTab {

   private final String tabName;
   private final String rightStr;
   private final String leftStr;

   public ResultsEditorCompareTab(String title, String tabName, String leftStr, String rightStr) {
      this.tabName = tabName;
      this.leftStr = leftStr;
      this.rightStr = rightStr;
      org.eclipse.core.runtime.Assert.isNotNull(tabName);
      org.eclipse.core.runtime.Assert.isNotNull(leftStr);
      org.eclipse.core.runtime.Assert.isNotNull(rightStr);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab#getTabName()
    */
   @Override
   public String getTabName() {
      return tabName;
   }

   public Composite createTab(Composite parent, ResultsEditor resultsEditor) throws OseeCoreException {

      Composite comp = ALayout.createCommonPageComposite(parent);
      ToolBar toolBar = resultsEditor.createToolBar(comp);
      createToolbar(toolBar);

      GridData gd = new GridData(GridData.FILL_BOTH);
      return comp;
   }

   private void createToolbar(ToolBar toolBar) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.compare.IResultsEditorCompareTab#getLeftString()
    */
   @Override
   public String getLeftString() throws OseeCoreException {
      return leftStr;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.compare.IResultsEditorCompareTab#getRightString()
    */
   @Override
   public String getRightString() throws OseeCoreException {
      return rightStr;
   }

}
