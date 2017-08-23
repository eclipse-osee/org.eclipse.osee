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
package org.eclipse.osee.ats.client.integration.tests.ats.demo;

import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.populate.Pdd10SetupAndImportReqs;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class Pdd10SetupAndImportReqsTest implements IPopulateDemoDatabaseTest {

   @Test
   public void setupAndImportRequirements() {
      DemoUtil.setPopulateDbSuccessful(false);

      Pdd10SetupAndImportReqs create = new Pdd10SetupAndImportReqs();
      create.run();

      DemoUtil.setPopulateDbSuccessful(true);
   }

}
