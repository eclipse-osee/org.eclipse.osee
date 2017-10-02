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
package org.eclipse.osee.orcs.core.internal.search;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link CriteriaAttributeKeywords}
 *
 * @author Megumi Telles
 */
public class SearchCriteriaTest {

   @Mock
   private AttributeTypes cache;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(cache.get(CoreAttributeTypes.Name)).thenReturn(CoreAttributeTypes.Name);
      when(cache.get(CoreAttributeTypes.Active)).thenReturn(CoreAttributeTypes.Active);
      when(cache.get(CoreAttributeTypes.FavoriteBranch)).thenReturn(CoreAttributeTypes.FavoriteBranch);
   }

   @Test
   public void isTagged() {
      when(cache.isTaggable(CoreAttributeTypes.Name)).thenReturn(true);

      CriteriaAttributeKeywords keyword =
         new CriteriaAttributeKeywords(false, Collections.singletonList(CoreAttributeTypes.Name), cache, "");
      keyword.checkNotTaggable();
      Assert.assertTrue("Attribute type is taggable", true);
   }

   @Test(expected = OseeArgumentException.class)
   public void notTagged() {

      when(cache.isTaggable(CoreAttributeTypes.Active)).thenReturn(false);

      CriteriaAttributeKeywords keyword =
         new CriteriaAttributeKeywords(false, Collections.singletonList(CoreAttributeTypes.Active), cache, "");
      keyword.checkNotTaggable();
      Assert.fail("checkNotTaggable should have thrown an exception on this attribute type");

   }

   @Test(expected = OseeArgumentException.class)
   public void notTaggedList() {

      when(cache.isTaggable(CoreAttributeTypes.FavoriteBranch)).thenReturn(false);

      ArrayList<AttributeTypeId> types = new ArrayList<>();
      types.add(CoreAttributeTypes.Active);
      types.add(CoreAttributeTypes.FavoriteBranch);

      CriteriaAttributeKeywords keyword = new CriteriaAttributeKeywords(false, types, cache, "");

      keyword.checkNotTaggable();
      Assert.fail("checkNotTaggable should have thrown an exception on this attribute type");
   }
}
