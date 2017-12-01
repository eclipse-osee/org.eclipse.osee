/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchDslTest;
import org.eclipse.osee.orcs.rest.internal.search.predicate.AttributeTypePredicateHandlerTest;
import org.eclipse.osee.orcs.rest.internal.search.predicate.ExistenceTypePredicateHandlerTest;
import org.eclipse.osee.orcs.rest.internal.search.predicate.GuidsPredicateHandlerTest;
import org.eclipse.osee.orcs.rest.internal.search.predicate.IdsPredicateHandlerTest;
import org.eclipse.osee.orcs.rest.internal.search.predicate.IsOfTypePredicateHandlerTest;
import org.eclipse.osee.orcs.rest.internal.search.predicate.TypeEqualsPredicateHandlerTest;
import org.eclipse.osee.orcs.rest.internal.writer.OrcsValidationHelperAdapterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author John R. Misinco
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   SearchDslTest.class,
   AttributeTypePredicateHandlerTest.class,
   ExistenceTypePredicateHandlerTest.class,
   IdsPredicateHandlerTest.class,
   OrcsValidationHelperAdapterTest.class,
   GuidsPredicateHandlerTest.class,
   IsOfTypePredicateHandlerTest.class,
   TypeEqualsPredicateHandlerTest.class})
public class InternalTestSuite {
   // Test Suite
}
