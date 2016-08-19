/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.writer;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifact;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactToken;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttribute;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttributeType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwBranch;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwCollector;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwRelation;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwRelationType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link OrcsCollectorValidator}
 *
 * @author Donald G. Dunne
 */
public class OrcsCollectorValidatorTest {

   // @formatter:off
   @Mock private IOrcsValidationHelper helper;
   @Mock private OwCollector collector;
   @Mock private OwBranch branch;
   // @formatter:on
   private OrcsCollectorValidator validator;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(collector.getAsUserId()).thenReturn("3443");
      when(branch.getUuid()).thenReturn(570L);
      when(branch.toString()).thenReturn("OwBranch [uuid=570]");
      when(collector.getBranch()).thenReturn(branch);
      when(collector.getPersistComment()).thenReturn("persist comment");

      validator = createValidator(collector);
   }

   @Test
   public void test_validateArtifactType() {
      XResultData rd = validator.run();

      OwArtifact artifact = new OwArtifact();
      OwArtifactType artType = new OwArtifactType();
      artType.setUuid(11);
      artType.setName("Folder");
      artifact.setType(artType);

      when(collector.getCreate()).thenReturn(Arrays.asList(artifact));
      when(helper.isArtifactTypeExist(11)).thenReturn(false);
      rd = validator.run();
      assertTrue(rd.toString().contains("Artifact Type [OwArtifactType [uuid=11, data=null]] does not exist."));

      when(helper.isArtifactTypeExist(11)).thenReturn(true);
      rd = validator.run();
      assertFalse(rd.toString().contains("Artifact Type [OwArtifactType [uuid=11, data=null]] does not exist."));

      artType.setUuid(0L);
      rd = validator.run();
      assertTrue(rd.toString().contains("Invalid Artifact Type uuid [OwArtifactType [uuid=0, data=null]]."));
   }

   @Test
   public void test_validateArtifactDoesNotExist() {

      when(helper.isBranchExists(COMMON)).thenReturn(true);
      XResultData rd = validator.run();
      assertFalse(rd.toString().contains("Branch [OwBranch [uuid=570]] not valid."));

      OwArtifact artifact = new OwArtifact();
      OwArtifactType artType = new OwArtifactType();
      artType.setUuid(11);
      artType.setName("Folder");
      artifact.setType(artType);
      artifact.setUuid(5555L);

      when(helper.isArtifactExists(COMMON, 5555L)).thenReturn(false);
      when(collector.getCreate()).thenReturn(Arrays.asList(artifact));
      rd = validator.run();
      assertTrue(rd.toString().contains("Artifact Type [OwArtifactType [uuid=11, data=null]] does not exist."));

      when(branch.getUuid()).thenReturn(0L);
      rd = validator.run();
      assertTrue(rd.toString().contains(
         "Invalid Branch; can't validate artifact uuid for [OwArtifact [type=OwArtifactType [uuid=11, data=null], uuid=5555, data=null]]."));

   }

   @Test
   public void test_validateCreateAttributes() {

      when(helper.isBranchExists(COMMON)).thenReturn(true);
      XResultData rd = validator.run();
      assertFalse(rd.toString().contains("Branch [OwBranch [uuid=570]] not valid."));

      OwArtifact artifact = new OwArtifact();
      OwArtifactType artType = new OwArtifactType();
      artType.setUuid(11);
      artType.setName("Folder");
      artifact.setType(artType);
      artifact.setUuid(5555L);

      when(collector.getCreate()).thenReturn(Arrays.asList(artifact));

      rd = validator.run();
      assertTrue(rd.toString().contains(
         "Artifact [OwArtifact [type=OwArtifactType [uuid=11, data=null], uuid=5555, data=null]] does not have Name attribute."));

      artifact.setName("my name");
      OwAttribute attribute = new OwAttribute();
      artifact.getAttributes().add(attribute);
      rd = validator.run();
      assertTrue(rd.toString().contains(
         "Invalid Attribute Type uuid [null] for artifact [OwArtifact [type=OwArtifactType [uuid=11, data=null], uuid=5555, data=null]]."));

      OwAttributeType attrType = new OwAttributeType();
      attrType.setName("Static Id");
      attrType.setUuid(234);
      attribute.setType(attrType);

      when(helper.isAttributeTypeExists(234)).thenReturn(false);
      rd = validator.run();
      assertTrue(rd.toString().contains(
         "Error: Attribute Type [OwAttributeType [uuid=234, data=null]] does not exist for artifact [OwArtifact [type=OwArtifactType [uuid=11, data=null], uuid=5555, data=null]]."));

      when(helper.isAttributeTypeExists(234)).thenReturn(true);
      rd = validator.run();
      assertFalse(rd.toString().contains(
         "Error: Attribute Type [OwAttributeType [uuid=234, data=null]] does not exist for artifact [OwArtifact [type=OwArtifactType [uuid=11, data=null], uuid=5555, data=null]]."));
   }

   @Test
   public void test_validateCreateRelations() {

      when(helper.isBranchExists(COMMON)).thenReturn(true);
      XResultData rd = validator.run();
      assertFalse(rd.toString().contains("Branch [OwBranch [uuid=570]] not valid."));

      OwArtifact artifact = new OwArtifact();
      OwArtifactType artType = new OwArtifactType();
      artType.setUuid(11);
      artType.setName("Folder");
      artifact.setType(artType);
      artifact.setUuid(5555L);

      when(collector.getCreate()).thenReturn(Arrays.asList(artifact));

      OwRelation relation = new OwRelation();
      artifact.getRelations().add(relation);
      rd = validator.run();
      assertTrue(rd.toString().contains(
         "Invalid Relation Type [null] for artifact [OwArtifact [type=OwArtifactType [uuid=11, data=null], uuid=5555, data=null]]."));

      OwRelationType relType = new OwRelationType();
      relType.setUuid(65656);
      relation.setType(relType);
      when(helper.isRelationTypeExist(65656)).thenReturn(true);
      rd = validator.run();
      assertTrue(rd.toString().contains(
         "Invalid artifact token [null] for artifact [OwArtifact [type=OwArtifactType [uuid=11, data=null], uuid=5555, data=null]] and relation [OwRelation [type=OwRelationType [sideA=false, sideName=null, uuid=65656, data=null], artToken=null, data=null]]."));

      OwArtifactToken token = new OwArtifactToken();
      token.setUuid(9999);
      token.setName("Default Hierarchy - Child");
      relation.setArtToken(token);
      rd = validator.run();
      assertTrue(rd.toString().contains(
         "Artifact from token [OwArtifactToken [uuid=9999, data=null]] does not exist to relate to artifact [OwArtifact [type=OwArtifactType [uuid=11, data=null], uuid=5555, data=null]] for relation [OwRelation [type=OwRelationType [sideA=false, sideName=null, uuid=65656, data=null], artToken=OwArtifactToken [uuid=9999, data=null], data=null]]."));

      when(helper.isArtifactExists(BranchId.valueOf(collector.getBranch().getUuid()), 9999)).thenReturn(true);
      rd = validator.run();
      assertFalse(rd.toString().contains(
         "Artifact from token [OwArtifactToken [uuid=9999, data=null]] does not exist to relate to artifact [OwArtifact [type=OwArtifactType [uuid=11, data=null], uuid=5555, data=null]] for relation [OwRelation [type=OwRelationType [sideA=false, sideName=null, uuid=65656, data=null], artToken=OwArtifactToken [uuid=9999, data=null], data=null]]."));
   }

   @Test
   public void test_createEmpty() {
      assertTrue(collector.getCreate().isEmpty());

      XResultData rd = validator.run();
      assertTrue(rd.toString().contains("No create, update or delete entries."));

      when(collector.getCreate()).thenReturn(Arrays.asList(new OwArtifact()));
      rd = validator.run();
      assertFalse(rd.toString().contains("No create, update or delete entries."));
   }

   @Test
   public void test_branch() {
      when(helper.isBranchExists(COMMON)).thenReturn(false);
      XResultData rd = validator.run();
      assertTrue(rd.toString().contains("Branch [OwBranch [uuid=570]] not valid."));

      when(helper.isBranchExists(COMMON)).thenReturn(true);
      rd = validator.run();
      assertFalse(rd.toString().contains("Branch [OwBranch [uuid=570]] not valid."));
   }

   @Test
   public void test_asUserId() {

      when(helper.isUserExists("3443")).thenReturn(false);
      XResultData rd = validator.run();
      assertTrue(rd.toString().contains("Invalid asUserId [3443]"));

      when(helper.isUserExists("3443")).thenReturn(true);
      rd = validator.run();
      assertFalse(rd.toString().contains("Invalid asUserId [3443]"));
   }

   @Test
   public void test_persistComment() {

      when(collector.getPersistComment()).thenReturn(null);
      XResultData rd = validator.run();
      assertTrue(rd.toString().contains("Invalid persistComment [null]"));

      when(collector.getPersistComment()).thenReturn("persist comment");
      rd = validator.run();
      assertFalse(rd.toString().contains("Invalid persistComment"));
   }

   private OrcsCollectorValidator createValidator(OwCollector collector) {
      return new OrcsCollectorValidator(collector, helper);
   }

}
