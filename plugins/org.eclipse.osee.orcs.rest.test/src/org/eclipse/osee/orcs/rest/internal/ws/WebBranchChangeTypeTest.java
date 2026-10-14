/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.internal.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import org.eclipse.osee.framework.core.event.WebBranchChangeType;
import org.junit.Test;

/**
 * Test Case for {@link WebBranchChangeType}. The web values are a client contract shared with the
 * {@code branchChangeType} union in {@code sse-event.service.ts}, so the exact literals and the
 * {@code getWebValue}/{@code fromWebValue} round-trip are pinned here.
 *
 * @author Boeing
 */
public class WebBranchChangeTypeTest {

   @Test
   public void testWebValuesAreTheClientContractLiterals() {
      assertEquals("created", WebBranchChangeType.CREATED.getWebValue());
      assertEquals("committed", WebBranchChangeType.COMMITTED.getWebValue());
      assertEquals("renamed", WebBranchChangeType.RENAMED.getWebValue());
      assertEquals("archived", WebBranchChangeType.ARCHIVED.getWebValue());
      assertEquals("unarchived", WebBranchChangeType.UNARCHIVED.getWebValue());
      assertEquals("state_changed", WebBranchChangeType.STATE_CHANGED.getWebValue());
      assertEquals("type_changed", WebBranchChangeType.TYPE_CHANGED.getWebValue());
      assertEquals("deleted", WebBranchChangeType.DELETED.getWebValue());
      assertEquals("purged", WebBranchChangeType.PURGED.getWebValue());
      assertEquals("rebaselined", WebBranchChangeType.REBASELINED.getWebValue());
   }

   @Test
   public void testFromWebValueRoundTripsEveryConstant() {
      for (WebBranchChangeType type : WebBranchChangeType.values()) {
         assertSame("round-trip failed for " + type, type, WebBranchChangeType.fromWebValue(type.getWebValue()));
      }
   }

   @Test
   public void testFromWebValueReturnsNullForNull() {
      assertNull(WebBranchChangeType.fromWebValue(null));
   }

   @Test
   public void testFromWebValueReturnsNullForUnknown() {
      assertNull(WebBranchChangeType.fromWebValue("committing"));
      assertNull(WebBranchChangeType.fromWebValue("not_a_change_type"));
      assertNull(WebBranchChangeType.fromWebValue(""));
   }

   @Test
   public void testFromWebValueIsCaseSensitive() {
      // The wire contract is exact lower_snake_case; a differently-cased token is unknown.
      assertNull(WebBranchChangeType.fromWebValue("CREATED"));
      assertNull(WebBranchChangeType.fromWebValue("Created"));
   }
}
