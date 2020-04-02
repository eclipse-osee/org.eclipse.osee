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
package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.orcs.core.internal.artifact.ArtifactTestSuite;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeTestSuite;
import org.eclipse.osee.orcs.core.internal.branch.BranchTestSuite;
import org.eclipse.osee.orcs.core.internal.graph.GraphTestSuite;
import org.eclipse.osee.orcs.core.internal.proxy.impl.ProxyImplTestSuite;
import org.eclipse.osee.orcs.core.internal.relation.RelationTestSuite;
import org.eclipse.osee.orcs.core.internal.script.ScriptEngineTestSuite;
import org.eclipse.osee.orcs.core.internal.search.QueryTestSuite;
import org.eclipse.osee.orcs.core.internal.transaction.TransactionTestSuite;
import org.eclipse.osee.orcs.core.internal.util.UtilTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   ArtifactTestSuite.class,
   AttributeTestSuite.class,
   BranchTestSuite.class,
   GraphTestSuite.class,
   RelationTestSuite.class,
   ProxyImplTestSuite.class,
   ScriptEngineTestSuite.class,
   QueryTestSuite.class,
   TransactionTestSuite.class,
   UtilTestSuite.class})
public class InternalTestSuite {
   // Test Suite
}
