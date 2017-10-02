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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;
import org.junit.Test;

/**
 * @author John Misinco
 */
public class AttributeCriteriaTest {

   @Test
   public void testAddToQueryBuilder()  {
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
