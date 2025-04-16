/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.publishing.markdown;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Jaden W. Puckett
 */
@RunWith(Suite.class)
@SuiteClasses({//
   PublishingMarkdownConversionTest.class,
   PublishingMarkdownTest.class,
   PublishingMarkdownAsHtmlTest.class})
public class PublishingMarkdownTestSuite {
   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("Begin Markdown Test Suite");
   }

   @AfterClass
   public static void cleanup() throws Exception {
      System.out.println("End Markdown Test Suite\n");
   }
}
