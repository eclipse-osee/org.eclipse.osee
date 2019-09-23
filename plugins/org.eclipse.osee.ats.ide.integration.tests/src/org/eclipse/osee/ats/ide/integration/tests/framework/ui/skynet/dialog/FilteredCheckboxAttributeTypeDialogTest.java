/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.framework.ui.skynet.dialog;

import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxAttributeTypeDialog;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class FilteredCheckboxAttributeTypeDialogTest {

   @Test
   public void test() {
      FilteredCheckboxAttributeTypeDialog dialog =
         new FilteredCheckboxAttributeTypeDialog("Select Attribute Types", "Select attribute types to display.");
      List<AttributeTypeToken> types = Collections.castAll(AttributeTypeManager.getAllTypes());
      dialog.setSelectable(types);

      try {
         dialog.setBlockOnOpen(false);
         dialog.open();

         int count = dialog.getTreeViewer().getViewer().getTree().getItemCount();
         Assert.assertTrue(count >= 25);
      } finally {
         dialog.close();
      }
   }
}
