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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.orcs.rest.internal.ws.ServerToServerEvent.PresenceEntry;
import org.junit.Test;

/**
 * Test Case for {@link ServerToServerEvent}. This is the cross-server (S2S) wire message. The
 * factories must stamp the correct {@code eventType} discriminator and populate only the fields
 * relevant to that event type (leaving the rest null), and the type must round-trip through Jackson
 * unchanged since it is serialized onto the ActiveMQ bus.
 *
 * @author Boeing
 */
public class ServerToServerEventTest {

   private static final ObjectMapper MAPPER = new ObjectMapper();

   @Test
   public void testArtifactChangedFactory() {
      List<String> artIds = Arrays.asList("11", "22");
      List<String> changeTypes = Arrays.asList("attribute_modified");
      List<String> changedAttributeTypeIds = Arrays.asList("1152921504606847088");
      ServerToServerEvent event = ServerToServerEvent.artifactChanged("570", artIds, "999", "user-1", "server-A",
         "[{\"typeId\":\"1\",\"encoding\":\"artId\",\"userIds\":[\"3\"]}]", "tab-7", changeTypes,
         changedAttributeTypeIds);

      assertEquals(ServerToServerEvent.ARTIFACT_CHANGED, event.getEventType());
      assertEquals("570", event.getBranchId());
      assertEquals(artIds, event.getArtifactIds());
      assertEquals("999", event.getTransactionId());
      assertEquals("user-1", event.getAuthorUserId());
      assertEquals("server-A", event.getOriginServerId());
      assertEquals("tab-7", event.getOriginId());
      assertEquals(changeTypes, event.getChangeTypes());
      assertEquals(changedAttributeTypeIds, event.getChangedAttributeTypeIds());
      // Branch/presence-only fields stay null on an artifact event.
      assertNull(event.getChangeType());
      assertNull(event.getNewBranchId());
      assertNull(event.getContext());
      assertNull(event.getPresenceUsers());
   }

   @Test
   public void testBranchChangedFactory() {
      ServerToServerEvent event =
         ServerToServerEvent.branchChanged("570", "committed", "user-1", "server-A", "tab-7", "12345");

      assertEquals(ServerToServerEvent.BRANCH_CHANGED, event.getEventType());
      assertEquals("570", event.getBranchId());
      assertEquals("committed", event.getChangeType());
      assertEquals("12345", event.getAssociatedArtifactId());
      assertEquals("tab-7", event.getOriginId());
      // Artifact/rebaseline/presence-only fields stay null.
      assertNull(event.getArtifactIds());
      assertNull(event.getTransactionId());
      assertNull(event.getNewBranchId());
      assertNull(event.getChangeTypes());
      assertNull(event.getContext());
   }

   @Test
   public void testBranchRebaselinedFactoryMapsOldAndNewIds() {
      ServerToServerEvent event =
         ServerToServerEvent.branchRebaselined("oldB", "newB", "rebaselined", "user-1", "server-A", "tab-7");

      assertEquals(ServerToServerEvent.BRANCH_REBASELINED, event.getEventType());
      // The retired (old) branch id is the primary branchId; the successor is newBranchId.
      assertEquals("oldB", event.getBranchId());
      assertEquals("newB", event.getNewBranchId());
      assertEquals("rebaselined", event.getChangeType());
      assertNull(event.getAssociatedArtifactId());
      assertNull(event.getArtifactIds());
   }

   @Test
   public void testPresenceFactory() {
      List<PresenceEntry> users = Arrays.asList(new PresenceEntry("3", "Joe"), new PresenceEntry("4", "Sam"));
      ServerToServerEvent event = ServerToServerEvent.presence("workflow/200683", "server-A", users);

      assertEquals(ServerToServerEvent.PRESENCE, event.getEventType());
      assertEquals("workflow/200683", event.getContext());
      assertEquals("server-A", event.getOriginServerId());
      assertEquals(2, event.getPresenceUsers().size());
      assertEquals("3", event.getPresenceUsers().get(0).getUserId());
      // Change fields are absent on a presence event.
      assertNull(event.getBranchId());
      assertNull(event.getChangeType());
      assertNull(event.getArtifactIds());
   }

   @Test
   public void testArtifactChangedJsonRoundTrip() throws Exception {
      ServerToServerEvent original = ServerToServerEvent.artifactChanged("570", Arrays.asList("11"), "999", "user-1",
         "server-A", null, "tab-7", Arrays.asList("attribute_modified"), Arrays.asList("1152921504606847088"));

      ServerToServerEvent restored =
         MAPPER.readValue(MAPPER.writeValueAsString(original), ServerToServerEvent.class);

      assertEquals(original.getEventType(), restored.getEventType());
      assertEquals(original.getBranchId(), restored.getBranchId());
      assertEquals(original.getArtifactIds(), restored.getArtifactIds());
      assertEquals(original.getTransactionId(), restored.getTransactionId());
      assertEquals(original.getOriginId(), restored.getOriginId());
      assertEquals(original.getChangeTypes(), restored.getChangeTypes());
      assertEquals(original.getChangedAttributeTypeIds(), restored.getChangedAttributeTypeIds());
   }

   @Test
   public void testPresenceJsonRoundTrip() throws Exception {
      ServerToServerEvent original =
         ServerToServerEvent.presence("workflow/1", "server-A", Arrays.asList(new PresenceEntry("3", "Joe")));

      ServerToServerEvent restored =
         MAPPER.readValue(MAPPER.writeValueAsString(original), ServerToServerEvent.class);

      assertEquals(ServerToServerEvent.PRESENCE, restored.getEventType());
      assertEquals("workflow/1", restored.getContext());
      assertEquals(1, restored.getPresenceUsers().size());
      assertEquals("3", restored.getPresenceUsers().get(0).getUserId());
      assertEquals("Joe", restored.getPresenceUsers().get(0).getUserName());
   }
}
