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

package org.eclipse.osee.framework.core.model.access;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class ScopeTest {

   @Test
   public void testGetPath() {
      Scope scope = new Scope();
      scope.add("foo");
      scope.add("bar bar");
      scope.addSubPath("baz");
      String expectedPath = "/foo/bar_bar#baz";
      Assert.assertEquals(expectedPath, scope.getPath());
   }

   @Test
   public void testEquals() {
      Scope legacyScopeA = Scope.createLegacyScope();
      Scope legacyScopeB = Scope.createLegacyScope();
      legacyScopeA.add("foo");
      legacyScopeB.addSubPath("bar");
      Assert.assertTrue(legacyScopeA.equals(legacyScopeB));

      Scope scopeA = new Scope();
      Scope scopeB = new Scope();
      scopeA.add("foo");
      scopeA.addSubPath("bar");
      scopeA.add("baz");
      scopeB.add("foo");
      scopeB.addSubPath("bar");
      scopeB.add("baz");
      Assert.assertTrue(scopeA.equals(scopeB));

      Scope scopeC = new Scope();
      Assert.assertFalse(legacyScopeA.equals(scopeA));
      Assert.assertFalse(legacyScopeA.equals(scopeC));
   }

   @Test
   public void testIsLegacy() {
      Scope legacyScope = Scope.createLegacyScope();
      Scope scope = new Scope();
      legacyScope.add("foo");
      legacyScope.addSubPath("bar");
      String expectedScope = "##";
      Assert.assertEquals(expectedScope, legacyScope.getPath());
      Assert.assertTrue(legacyScope.isLegacy());
      Assert.assertFalse(scope.isLegacy());
   }

   @Test
   public void testClone() {
      Scope scope = new Scope();
      Scope legacyScope = Scope.createLegacyScope();

      scope.add("foo").add("bar").addSubPath("baz").addSubPath("baz");
      Scope scopeClone = scope.clone();
      Assert.assertEquals(scope, scopeClone);

      scopeClone = legacyScope.clone();
      Assert.assertTrue(legacyScope.equals(scopeClone));
   }

}
