/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.demo;

import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.populate.Pdd95CreateDemoEVConfigAndWorkPackages;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd95CreateDemoWorkPackagesTest implements IPopulateDemoDatabaseTest {

   @Test
   public void testAction() throws Exception {
      DemoUtil.checkDbInitAndPopulateSuccess();
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd95CreateDemoEVConfigAndWorkPackages create = new Pdd95CreateDemoEVConfigAndWorkPackages();
      create.run();

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
