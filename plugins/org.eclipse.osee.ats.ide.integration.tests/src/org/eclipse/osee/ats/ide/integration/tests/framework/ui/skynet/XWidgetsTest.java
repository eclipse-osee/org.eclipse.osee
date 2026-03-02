/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.framework.ui.skynet;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class XWidgetsTest {

   @Test
   public void test() throws Exception {
      try {
         Assert.assertEquals(XWidgetFactory.getErrorwidgetids().toString(), 0,
            XWidgetFactory.getErrorwidgetids().size());
      } catch (Exception ex) {
         Assert.fail(Lib.exceptionToString(ex));
      }
   }

}
