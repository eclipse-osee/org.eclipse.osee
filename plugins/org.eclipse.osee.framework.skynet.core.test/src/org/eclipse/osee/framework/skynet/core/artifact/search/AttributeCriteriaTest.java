/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.skynet.core.artifact.search;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.client.QueryBuilder;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.junit.Test;

/**
 * @author John Misinco
 */
public class AttributeCriteriaTest {

   @Test
   public void testAddToQueryBuilder() {
      AttributeCriteria criteria = new AttributeCriteria(CoreAttributeTypes.Active);
      QueryBuilder builder = mock(QueryBuilder.class);
      criteria.addToQueryBuilder(builder);
      verify(builder).andExists(CoreAttributeTypes.Active);

      reset(builder);
      List<String> values = Arrays.asList("true", "false");
      criteria = new AttributeCriteria(CoreAttributeTypes.Active, values);
      criteria.addToQueryBuilder(builder);
      verify(builder).and(CoreAttributeTypes.Active, values);

      reset(builder);
      criteria = new AttributeCriteria(CoreAttributeTypes.Active, "true", QueryOption.TOKEN_MATCH_ORDER__ANY);
      criteria.addToQueryBuilder(builder);
      verify(builder).and(CoreAttributeTypes.Active, "true", QueryOption.TOKEN_MATCH_ORDER__ANY);

      reset(builder);
      criteria = new AttributeCriteria(CoreAttributeTypes.Active, "true");
      criteria.addToQueryBuilder(builder);
      verify(builder).and(CoreAttributeTypes.Active, "true");

   }
}
