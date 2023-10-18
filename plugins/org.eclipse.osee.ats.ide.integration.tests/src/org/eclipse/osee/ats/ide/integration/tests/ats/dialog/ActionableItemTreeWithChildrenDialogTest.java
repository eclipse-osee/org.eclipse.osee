/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.dialog;

import org.eclipse.osee.ats.ide.util.widgets.dialog.ActionableItemTreeWithChildrenDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemTreeWithChildrenDialogTest {

   @Test
   public void test() {
      ActionableItemTreeWithChildrenDialog dialog = new ActionableItemTreeWithChildrenDialog(Active.Active);
      try {
         dialog.setBlockOnOpen(false);
         dialog.open();

         int count = dialog.getTreeViewer().getViewer().getTree().getItemCount();
         Assert.assertTrue(count >= 5);
      } finally {
         dialog.close();
      }
   }
}
