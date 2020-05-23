/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.core.internal.search;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import java.util.ArrayList;
import java.util.Collections;
import org.eclipse.osee.framework.core.OrcsTokenService;
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
   @Mock
   private OrcsTokenService tokenService;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      doReturn(CoreAttributeTypes.Name).when(tokenService).getAttributeType(CoreAttributeTypes.Name.getId());
      assertEquals(CoreAttributeTypes.Name, tokenService.getAttributeType(CoreAttributeTypes.Name.getId()));
      doReturn(CoreAttributeTypes.Developmental).when(tokenService).getAttributeType(
         CoreAttributeTypes.Developmental.getId());
      assertEquals(CoreAttributeTypes.Developmental,
         tokenService.getAttributeType(CoreAttributeTypes.Developmental.getId()));
      doReturn(CoreAttributeTypes.FavoriteBranch).when(tokenService).getAttributeType(
         CoreAttributeTypes.FavoriteBranch.getId());
      assertEquals(CoreAttributeTypes.FavoriteBranch,
         tokenService.getAttributeType(CoreAttributeTypes.FavoriteBranch.getId()));

   }

   @Test
   public void isTagged() {
      assertEquals(true, tokenService.getAttributeType(CoreAttributeTypes.Name.getId()).isTaggable());

      CriteriaAttributeKeywords keyword =
         new CriteriaAttributeKeywords(false, Collections.singletonList(CoreAttributeTypes.Name), tokenService, "");
      keyword.checkNotTaggable();
      Assert.assertTrue("Attribute type is taggable", true);
   }

   @Test(expected = OseeArgumentException.class)
   public void notTagged() {
      assertEquals(false, tokenService.getAttributeType(CoreAttributeTypes.Developmental.getId()).isTaggable());

      CriteriaAttributeKeywords keyword = new CriteriaAttributeKeywords(false,
         Collections.singletonList(CoreAttributeTypes.Developmental), tokenService, "");
      keyword.checkNotTaggable();
      Assert.fail("checkNotTaggable should have thrown an exception on this attribute type");

   }

   @Test(expected = OseeArgumentException.class)
   public void notTaggedList() {
      assertEquals(false, tokenService.getAttributeType(CoreAttributeTypes.FavoriteBranch.getId()).isTaggable());

      ArrayList<AttributeTypeId> types = new ArrayList<>();
      types.add(CoreAttributeTypes.Developmental);
      types.add(CoreAttributeTypes.FavoriteBranch);

      CriteriaAttributeKeywords keyword = new CriteriaAttributeKeywords(false, types, tokenService, "");

      keyword.checkNotTaggable();
      Assert.fail("checkNotTaggable should have thrown an exception on this attribute type");
   }
}
