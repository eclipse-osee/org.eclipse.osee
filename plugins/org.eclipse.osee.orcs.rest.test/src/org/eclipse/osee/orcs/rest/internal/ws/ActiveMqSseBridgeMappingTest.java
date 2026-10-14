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
import org.eclipse.osee.framework.core.event.BranchEventGuids;
import org.eclipse.osee.framework.core.event.WebBranchChangeType;
import org.junit.Test;

/**
 * Test Case for the branch-event vocabulary mapping in {@link ActiveMqSseBridge}
 * ({@code webChangeTypeToBranchEventGuid} and {@code branchEventGuidToWebChangeType}). This is the
 * web-vocabulary <-> desktop-GUID bridge. The mapping is deliberately asymmetric/lossy at a few
 * points; those edges are pinned here so a future edit can't quietly change the wire behavior.
 *
 * @author Boeing
 */
public class ActiveMqSseBridgeMappingTest {

   // --- web value -> desktop GUID (web change relayed to the desktop bus) ---

   @Test
   public void testWebToGuidForTerminalTypes() {
      assertEquals(BranchEventGuids.ADDED,
         ActiveMqSseBridge.webChangeTypeToBranchEventGuid(WebBranchChangeType.CREATED.getWebValue()));
      assertEquals(BranchEventGuids.COMMITTED,
         ActiveMqSseBridge.webChangeTypeToBranchEventGuid(WebBranchChangeType.COMMITTED.getWebValue()));
      assertEquals(BranchEventGuids.RENAMED,
         ActiveMqSseBridge.webChangeTypeToBranchEventGuid(WebBranchChangeType.RENAMED.getWebValue()));
      assertEquals(BranchEventGuids.STATE_UPDATED,
         ActiveMqSseBridge.webChangeTypeToBranchEventGuid(WebBranchChangeType.STATE_CHANGED.getWebValue()));
      assertEquals(BranchEventGuids.TYPE_UPDATED,
         ActiveMqSseBridge.webChangeTypeToBranchEventGuid(WebBranchChangeType.TYPE_CHANGED.getWebValue()));
      assertEquals(BranchEventGuids.DELETED,
         ActiveMqSseBridge.webChangeTypeToBranchEventGuid(WebBranchChangeType.DELETED.getWebValue()));
      assertEquals(BranchEventGuids.PURGED,
         ActiveMqSseBridge.webChangeTypeToBranchEventGuid(WebBranchChangeType.PURGED.getWebValue()));
   }

   @Test
   public void testArchivedAndUnarchivedBothMapToArchiveStateUpdated() {
      // The desktop has a single ARCHIVE_STATE_UPDATED for both directions; the web split into
      // archived/unarchived collapses to it on the way out.
      assertEquals(BranchEventGuids.ARCHIVE_STATE_UPDATED,
         ActiveMqSseBridge.webChangeTypeToBranchEventGuid(WebBranchChangeType.ARCHIVED.getWebValue()));
      assertEquals(BranchEventGuids.ARCHIVE_STATE_UPDATED,
         ActiveMqSseBridge.webChangeTypeToBranchEventGuid(WebBranchChangeType.UNARCHIVED.getWebValue()));
   }

   @Test
   public void testRebaselinedIsNotRelayedToDesktop() {
      // REBASELINED is a web-specific swap the desktop derives from its own granular events; it
      // has no single desktop GUID, so it must not be relayed.
      assertNull(ActiveMqSseBridge.webChangeTypeToBranchEventGuid(WebBranchChangeType.REBASELINED.getWebValue()));
   }

   @Test
   public void testWebToGuidForUnknownOrNull() {
      assertNull(ActiveMqSseBridge.webChangeTypeToBranchEventGuid(null));
      assertNull(ActiveMqSseBridge.webChangeTypeToBranchEventGuid("committing"));
      assertNull(ActiveMqSseBridge.webChangeTypeToBranchEventGuid("bogus"));
   }

   // --- desktop GUID -> web value (desktop change relayed to web SSE) ---

   @Test
   public void testGuidToWebForTerminalTypes() {
      assertEquals(WebBranchChangeType.CREATED.getWebValue(),
         ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.ADDED));
      assertEquals(WebBranchChangeType.COMMITTED.getWebValue(),
         ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.COMMITTED));
      assertEquals(WebBranchChangeType.RENAMED.getWebValue(),
         ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.RENAMED));
      assertEquals(WebBranchChangeType.STATE_CHANGED.getWebValue(),
         ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.STATE_UPDATED));
      assertEquals(WebBranchChangeType.TYPE_CHANGED.getWebValue(),
         ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.TYPE_UPDATED));
      assertEquals(WebBranchChangeType.DELETED.getWebValue(),
         ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.DELETED));
      assertEquals(WebBranchChangeType.PURGED.getWebValue(),
         ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.PURGED));
   }

   @Test
   public void testArchiveStateUpdatedMapsToArchivedOnly() {
      // Lossy inbound: the desktop ARCHIVE_STATE_UPDATED can't distinguish archive from unarchive,
      // so it resolves to "archived" (the web consumer re-GETs the branch either way).
      assertEquals(WebBranchChangeType.ARCHIVED.getWebValue(),
         ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.ARCHIVE_STATE_UPDATED));
   }

   @Test
   public void testTransientDesktopMarkersAreNotRelayedToWeb() {
      // In-progress markers have nothing for the web to GET, so they map to null (not relayed).
      assertNull(ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.COMMITTING));
      assertNull(ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.DELETING));
      assertNull(ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.PURGING));
      assertNull(ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.COMMIT_FAILED));
   }

   @Test
   public void testGuidToWebForUnknownOrNull() {
      assertNull(ActiveMqSseBridge.branchEventGuidToWebChangeType(null));
      assertNull(ActiveMqSseBridge.branchEventGuidToWebChangeType("not-a-guid"));
      assertNull(ActiveMqSseBridge.branchEventGuidToWebChangeType(BranchEventGuids.FAVORITES_UPDATED));
   }
}
