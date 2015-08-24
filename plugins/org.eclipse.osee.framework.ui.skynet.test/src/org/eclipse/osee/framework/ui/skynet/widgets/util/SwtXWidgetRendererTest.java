/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.util;

import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link SwtXWidgetRenderer}
 *
 * @author Donald G. Dunne
 */
public class SwtXWidgetRendererTest {

   @Test
   public void testSetGetName() {

      SwtXWidgetRenderer renderer = new SwtXWidgetRenderer();
      XWidget xWidget = new XText("asdf");
      renderer.setName(xWidget, "this.that.one");

      Assert.assertEquals("this.that.one", xWidget.getLabel());

      renderer.setName(xWidget, null);
      Assert.assertEquals("this.that.one", xWidget.getLabel());
   }

}