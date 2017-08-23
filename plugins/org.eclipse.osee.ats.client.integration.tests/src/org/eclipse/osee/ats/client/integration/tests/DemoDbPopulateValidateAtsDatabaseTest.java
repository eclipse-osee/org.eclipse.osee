/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests;

import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.integration.tests.ats.health.AtsValidateAtsDatabaseTest;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.BeforeClass;

/**
 * Extension to validate ats database so test will run once after populate and once at end of the tests. Without this,
 * JUnit only runs test once.
 *
 * @author Donald G Dunne
 */
public class DemoDbPopulateValidateAtsDatabaseTest extends AtsValidateAtsDatabaseTest {

   @BeforeClass
   public static void cleanup() throws OseeCoreException {
      DemoUtil.checkDbInitAndPopulateSuccess();
   }
}
