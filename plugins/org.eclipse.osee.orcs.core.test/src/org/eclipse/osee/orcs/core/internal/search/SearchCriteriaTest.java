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
import org.junit.Assert;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
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
   private AttributeTypeCache cache;
   @Mock
   private AttributeType type;
   private final ArrayList<IAttributeType> types = new ArrayList<IAttributeType>();

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      types.add(CoreAttributeTypes.Active);
      types.add(CoreAttributeTypes.FavoriteBranch);
   }

   @Test
   public void isTagged() throws OseeCoreException {
      when(cache.get(CoreAttributeTypes.Name)).thenReturn(type);
      when(type.getName()).thenReturn("Name");
      when(type.isTaggable()).thenReturn(true);
      CriteriaAttributeKeywords keyword =
         new CriteriaAttributeKeywords(false, Collections.singletonList(CoreAttributeTypes.Name), cache, "");
      keyword.checkNotTaggable();
      Assert.assertTrue("Attribute type is taggable", true);
   }

   @Test(expected = OseeArgumentException.class)
   public void notTagged() throws OseeCoreException {
      when(cache.get(CoreAttributeTypes.Active)).thenReturn(type);
      when(type.getName()).thenReturn("Active");
      when(type.isTaggable()).thenReturn(false);
      CriteriaAttributeKeywords keyword =
         new CriteriaAttributeKeywords(false, Collections.singletonList(CoreAttributeTypes.Active), cache, "");
      keyword.checkNotTaggable();
      Assert.fail("checkNotTaggable should have thrown an exception on this attribute type");

   }

   @Test(expected = OseeArgumentException.class)
   public void notTaggedList() throws OseeCoreException {
      when(cache.get(CoreAttributeTypes.Active)).thenReturn(type);
      when(type.getName()).thenReturn("Active");
      when(cache.get(CoreAttributeTypes.FavoriteBranch)).thenReturn(type);
      when(type.getName()).thenReturn("Favorite Branch");
      when(type.isTaggable()).thenReturn(false);
      CriteriaAttributeKeywords keyword = new CriteriaAttributeKeywords(false, types, cache, "");

      keyword.checkNotTaggable();
      Assert.fail("checkNotTaggable should have thrown an exception on this attribute type");
   }
}
