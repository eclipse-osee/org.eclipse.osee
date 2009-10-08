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
package org.eclipse.osee.framework.skynet.core.test.relation;

import org.eclipse.osee.framework.skynet.core.test.relation.order.RelationOrderDataTest;
import org.eclipse.osee.framework.skynet.core.test.relation.order.RelationOrderParserTest;
import org.eclipse.osee.framework.skynet.core.test.relation.order.RelationSorterProviderTest;
import org.eclipse.osee.framework.skynet.core.test.relation.sorters.RelationSorterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {RelationOrderParserTest.class, RelationSorterProviderTest.class, RelationOrderDataTest.class,
      RelationSorterTest.class})
/**
 * @author Roberto E. Escobar
 */
public class RelationOrderTestSuite {

}
