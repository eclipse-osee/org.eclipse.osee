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
package org.eclipse.osee.ats.test.testDb.review;

import junit.framework.TestCase;
import org.eclipse.osee.ats.test.testDb.DemoTestUtil;

/**
 * This test is intended to be run against a demo database. It tests the ability to create and set rules on a workflow
 * page that causes decision and peerToPeer reviews to be auto-created during transition, createBranch and commitBranch
 * 
 * @author Donald G. Dunne
 */
public class AtsReviewRuleTest extends TestCase {

   public void testDemoDatabase() throws Exception {
      DemoTestUtil.setUpTest();
   }

   public void testReviewCreation() throws Exception {

   }
}
