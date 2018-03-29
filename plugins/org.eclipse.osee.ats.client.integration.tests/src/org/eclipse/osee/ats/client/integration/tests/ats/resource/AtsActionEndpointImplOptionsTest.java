/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.resource;

import org.codehaus.jackson.JsonNode;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.WorkItemWriterOptions;
import org.eclipse.osee.ats.client.demo.DemoUtil;
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
      String url =
         "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details?" + WorkItemWriterOptions.ValuesWithIds.name() + "=true";
      ;
      JsonNode action = testActionRestCall(url, 1);
      Assert.assertEquals(action.get("AtsId").asText(), action.get("ats.Id").get("value").asText());
      Assert.assertTrue(Strings.isNumeric(action.get("ats.Id").get("gammaId").asText()));
      Assert.assertFalse(Strings.isNumeric(action.get("CreatedDate").asText()));
   }

   @Test
   public void testAtsActionsFieldsAsIdsRestCall() {
      String url =
         "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details?" + WorkItemWriterOptions.KeysAsIds.name() + "=true";
      JsonNode action = testActionRestCall(url, 1);
      Assert.assertEquals(action.get("AtsId").asText(), action.get("1152921504606847877").asText());
      Assert.assertFalse(Strings.isNumeric(action.get("CreatedDate").asText()));
   }

   @Test
   public void testAtsActionsDatesAsLongRestCall() {
      String url =
         "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details?" + WorkItemWriterOptions.DatesAsLong.name() + "=true";
      JsonNode action = testActionRestCall(url, 1);
      Assert.assertTrue(Strings.isNumeric(action.get("CreatedDate").asText()));
   }

   @Test
   public void testAtsActionsWriteRelatedAsTokensRestCall() {
      String url =
         "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details?" + WorkItemWriterOptions.WriteRelatedAsTokens.name() + "=true";
      JsonNode action = testActionRestCall(url, 1);
      JsonNode node = action.get("AssigneesTokens");
      Assert.assertTrue(node != null);
      node = action.get("TargetedVersionToken");
      Assert.assertTrue(node != null);
   }

   @Test
   public void testAtsActionsFieldsAsIdsAndDatesAsLong() {
      String url =
         "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details?" + WorkItemWriterOptions.KeysAsIds.name() + "=true&" + WorkItemWriterOptions.DatesAsLong.name() + "=true";
      JsonNode action = testActionRestCall(url, 1);

      // FieldsAsIds should replace attr type names with id as the field
      Assert.assertFalse(action.has(AtsAttributeTypes.CreatedDate.getName()));
      String teamDefByAttrTypeId = action.get(AtsAttributeTypes.CreatedDate.getIdString()).asText();
      Assert.assertTrue(Strings.isNumeric(teamDefByAttrTypeId));

      // DatesAsLong should replace date value with long time value
      String dateValue = action.get(AtsAttributeTypes.CreatedDate.getIdString()).asText();
      Assert.assertTrue(Strings.isNumeric(dateValue));
   }

   private JsonNode testActionRestCall(String url, int size) {
      String json = getJson(url);
      JsonNode arrayNode = JsonUtil.readTree(json);
      Assert.assertEquals(size, arrayNode.size());
      return arrayNode.get(0);
   }

}
