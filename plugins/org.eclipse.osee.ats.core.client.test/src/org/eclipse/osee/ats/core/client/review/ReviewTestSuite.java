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
package org.eclipse.osee.ats.core.client.review;

import org.eclipse.osee.ats.core.client.review.defect.AtsXDefectValidatorTest;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItemTest;
import org.eclipse.osee.ats.core.client.review.role.AtsXUserRoleValidatorTest;
import org.eclipse.osee.ats.core.client.review.role.UserRoleTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsXDefectValidatorTest.class,
   ReviewDefectItemTest.class,
   AtsXUserRoleValidatorTest.class,
   UserRoleTest.class})
/**
 * @author Donald G. Dunne
 */
public class ReviewTestSuite {
   // TestSuite
}
