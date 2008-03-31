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
package org.eclipse.osee.framework.ui.skynet.Import;

import junit.framework.TestCase;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Robert A. Fisher
 */
public class ImportMetaPageTest extends TestCase {

   /**
    * Test method for {@link org.eclipse.osee.framework.ui.skynet.Import.ImportMetaPage#getSelectedBranch()}.
    */
   public void testGetSelectedBranch() {
      ImportMetaPage page = new ImportMetaPage(null);
      page.createControl(AWorkbench.getActivePage().getWorkbenchWindow().getShell());

      assertEquals(page.getSelectedBranch(), BranchPersistenceManager.getInstance().getDefaultBranch());
   }

}
