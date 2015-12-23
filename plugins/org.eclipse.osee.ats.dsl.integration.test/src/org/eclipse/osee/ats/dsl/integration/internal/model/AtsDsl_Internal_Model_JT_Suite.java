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
package org.eclipse.osee.ats.dsl.integration.internal.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   AbstractWorkDefItemTest.class,
   CompositeStateItemTest.class,
   DecisionReviewDefinitionTest.class,
   DecisionReviewOptionTest.class,
   ModelUtilTest.class,
   PeerReviewDefinitionTest.class,
   StateDefinitionTest.class,
   WorkDefinitionTest.class,
   WidgetOptionHandlerTest.class,
   WidgetOptionTest.class,
   WidgetDefinitionTest.class})
/**
 * @author Donald G. Dunne
 */
public class AtsDsl_Internal_Model_JT_Suite {
   // Test Suite
}
