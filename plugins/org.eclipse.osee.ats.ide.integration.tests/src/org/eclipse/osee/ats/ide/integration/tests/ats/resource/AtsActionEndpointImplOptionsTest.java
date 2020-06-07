/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.resource;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.WorkItemWriterOptions;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link AtsActionEndpointImpl}
 *
 * @author David W. Miller
 */
public class AtsActionEndpointImplOptionsTest extends AbstractRestTest {

   @Test
   public void testAtsActionsWriteWithGammasRestCall() {
      String urlPath = "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details";
      URI uri = UriBuilder.fromUri(OseeClientProperties.getOseeApplicationServer()).path(urlPath) //
         .queryParam(WorkItemWriterOptions.ValuesWithIds.name(), "true") //
         .build();
      JsonNode action = testActionRestCall(uri, 1);
      Assert.assertEquals(action.get("AtsId").asText(), action.get("ats.Id").get("value").asText());
      Assert.assertTrue(Strings.isNumeric(action.get("ats.Id").get("gammaId").asText()));
      Assert.assertFalse(Strings.isNumeric(action.get("CreatedDate").asText()));
   }

   @Test
   public void testAtsActionsFieldsAsIdsRestCall() {
      String urlPath = "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details";
      URI uri = UriBuilder.fromUri(OseeClientProperties.getOseeApplicationServer()).path(urlPath) //
         .queryParam(WorkItemWriterOptions.KeysAsIds.name(), "true") //
         .build();
      JsonNode action = testActionRestCall(uri, 1);
      Assert.assertEquals(action.get("AtsId").asText(), action.get("1152921504606847877").asText());
      Assert.assertFalse(Strings.isNumeric(action.get("CreatedDate").asText()));
   }

   @Test
   public void testAtsActionsDatesAsLongRestCall() {
      String urlPath = "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details";
      URI uri = UriBuilder.fromUri(OseeClientProperties.getOseeApplicationServer()).path(urlPath)//
         .queryParam(WorkItemWriterOptions.DatesAsLong.name(), "true") //
         .build();
      JsonNode action = testActionRestCall(uri, 1);
      Assert.assertTrue(Strings.isNumeric(action.get("CreatedDate").asText()));
   }

   @Test
   public void testAtsActionsWriteRelatedAsTokensRestCall() {
      String urlPath = "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details";
      URI uri = UriBuilder.fromUri(OseeClientProperties.getOseeApplicationServer()).path(urlPath) //
         .queryParam(WorkItemWriterOptions.WriteRelatedAsTokens.name(), "true") //
         .build();
      JsonNode action = testActionRestCall(uri, 1);
      JsonNode node = action.get("AssigneesTokens");
      Assert.assertTrue(node != null);
      node = action.get("TargetedVersionToken");
      Assert.assertTrue(node != null);
   }

   @Test
   public void testAtsActionsFieldsAsIdsAndDatesAsLong() {
      String urlPath = "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details";
      URI uri = UriBuilder.fromUri(OseeClientProperties.getOseeApplicationServer()).path(urlPath) //
         .queryParam(WorkItemWriterOptions.KeysAsIds.name(), "true") //
         .queryParam(WorkItemWriterOptions.DatesAsLong.name(), "true") //
         .build();
      JsonNode action = testActionRestCall(uri, 1);

      // FieldsAsIds should replace attr type names with id as the field
      Assert.assertFalse(action.has(AtsAttributeTypes.CreatedDate.getName()));
      String teamDefByAttrTypeId = action.get(AtsAttributeTypes.CreatedDate.getIdString()).asText();
      Assert.assertTrue(Strings.isNumeric(teamDefByAttrTypeId));

      // DatesAsLong should replace date value with long time value
      String dateValue = action.get(AtsAttributeTypes.CreatedDate.getIdString()).asText();
      Assert.assertTrue(Strings.isNumeric(dateValue));
   }

   private JsonNode testActionRestCall(URI uri, int size) {
      String json = getJson(uri);
      JsonNode arrayNode = JsonUtil.readTree(json);
      Assert.assertEquals(size, arrayNode.size());
      return arrayNode.get(0);
   }

}
