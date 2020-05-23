/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.writer;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.rest.internal.writer.IOrcsValidationHelper;
import org.eclipse.osee.orcs.rest.internal.writer.OrcsCollectorValidator;
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

      // Branch is valid and exists by default
      when(collector.getBranch()).thenReturn(branch);
      when(collector.getBranchId()).thenReturn(COMMON);
      when(helper.isBranchExists(COMMON)).thenReturn(true);

      // User valid
      when(collector.getAsUserId()).thenReturn("3443");
      when(helper.isUserExists("3443")).thenReturn(true);

      when(branch.getId()).thenReturn(570L);
      when(branch.toString()).thenReturn("OwBranch [id=570]");
      when(branch.isValid()).thenReturn(true);
      when(collector.getPersistComment()).thenReturn("persist comment");

      validator = createValidator(collector);
   }

   @Test
   public void test_validateArtifactType() {
      XResultData rd = validator.run();

      OwArtifact artifact = new OwArtifact();
      OwArtifactType artType = new OwArtifactType(11L, "Folder");
      artifact.setType(artType);

      when(collector.getCreate()).thenReturn(Arrays.asList(artifact));
      when(helper.isArtifactTypeExist(11)).thenReturn(false);
      rd = validator.run();
      assertTrue(rd.toString().contains("Artifact Type [OwArtifactType [id=11, data=null]] does not exist."));

      when(helper.isArtifactTypeExist(11)).thenReturn(true);
      rd = validator.run();
      assertFalse(rd.toString().contains("Artifact Type [OwArtifactType [id=11, data=null]] does not exist."));

      artType.setId(0L);
      rd = validator.run();
      assertTrue(rd.toString().contains("Invalid Artifact Type id [OwArtifactType [id=0, data=null]]."));
   }

   @Test
   public void test_validateArtifactDoesNotExist() {

      when(helper.isBranchExists(COMMON)).thenReturn(true);
      XResultData rd = validator.run();
      assertFalse(rd.toString().contains("Branch [OwBranch [id=570]] not valid."));

      OwArtifact artifact = new OwArtifact(5555L, "");
      OwArtifactType artType = new OwArtifactType(11L, "Folder");
      artifact.setType(artType);

      when(helper.isArtifactExists(COMMON, 5555L)).thenReturn(false);
      when(collector.getCreate()).thenReturn(Arrays.asList(artifact));
      rd = validator.run();
      assertTrue(rd.toString().contains("Artifact Type [OwArtifactType [id=11, data=null]] does not exist."));

      when(helper.isBranchExists(COMMON)).thenReturn(false);
      rd = validator.run();
      assertTrue(rd.toString().startsWith("Error: Branch [OwBranch [id=570]] not valid.\n"));

   }

   @Test
   public void test_validateCreateAttributes() {

      when(helper.isBranchExists(COMMON)).thenReturn(true);
      XResultData rd = validator.run();
      assertFalse(rd.toString().contains("Branch [OwBranch [id=570]] not valid."));

      OwArtifact artifact = new OwArtifact(5555L, "");
      artifact.setName("my name");
      OwArtifactType artType = new OwArtifactType(11L, "Folder");
      artifact.setType(artType);
      when(helper.isArtifactTypeExist(11L)).thenReturn(true);
      when(collector.getUpdate()).thenReturn(Arrays.asList(artifact));
      when(helper.isArtifactExists(COMMON, 5555L)).thenReturn(true);

      OwAttribute attribute = new OwAttribute();
      artifact.getAttributes().add(attribute);
      rd = validator.run();
      assertTrue(rd.toString().startsWith(
         "Error: Invalid Attribute Type [null] for artifact [OwArtifact [type=OwArtifactType [id=11, data=null], id=5555, data=null]]."));

      OwAttributeType attrType = new OwAttributeType(234L, "Static Id");
      attribute.setType(attrType);

      when(helper.isAttributeTypeExists(234)).thenReturn(false);
      rd = validator.run();
      assertTrue(rd.toString().startsWith(
         "Error: Invalid Attribute Type [OwAttributeType [id=234, data=null]] for artifact [OwArtifact [type=OwArtifactType [id=11, data=null], id=5555, data=null]]."));

      when(helper.isAttributeTypeExists(234)).thenReturn(true);
      rd = validator.run();
      assertFalse(rd.toString().startsWith(
         "Error: Invalid Attribute Type [OwAttributeType [id=234, data=null]] for artifact [OwArtifact [type=OwArtifactType [id=11, data=null], id=5555, data=null]]."));
   }

   @Test
   public void test_validateCreateRelations() {

      when(helper.isBranchExists(COMMON)).thenReturn(true);
      XResultData rd = validator.run();
      assertFalse(rd.toString().contains("Branch [OwBranch [id=570]] not valid."));

      OwArtifact artifact = new OwArtifact(5555L, "");
      OwArtifactType artType = new OwArtifactType(11L, "Folder");
      artifact.setType(artType);

      when(collector.getCreate()).thenReturn(Arrays.asList(artifact));

      OwRelation relation = new OwRelation();
      artifact.getRelations().add(relation);
      rd = validator.run();
      assertTrue(rd.toString().contains(
         "Invalid Relation Type [null] for artifact [OwArtifact [type=OwArtifactType [id=11, data=null], id=5555, data=null]]."));

      OwRelationType relType = new OwRelationType(65656L, "");
      relation.setType(relType);
      when(helper.isRelationTypeExist(65656)).thenReturn(true);
      rd = validator.run();
      assertTrue(rd.toString().contains(
         "Invalid artifact token [null] for artifact [OwArtifact [type=OwArtifactType [id=11, data=null], id=5555, data=null]] and relation [OwRelation [type=OwRelationType [sideA=false, sideName=null, id=65656, data=null], artToken=null, data=null]]."));

      OwArtifactToken token = new OwArtifactToken(9999L, "Default Hierarchy - Child");
      relation.setArtToken(token);
      rd = validator.run();
      assertTrue(rd.toString().contains(
         "Artifact from token [OwArtifactToken [id=9999, data=null]] does not exist to relate to artifact [OwArtifact [type=OwArtifactType [id=11, data=null], id=5555, data=null]] for relation [OwRelation [type=OwRelationType [sideA=false, sideName=null, id=65656, data=null], artToken=OwArtifactToken [id=9999, data=null], data=null]]."));

      when(helper.isArtifactExists(BranchId.valueOf(collector.getBranch().getId()), 9999)).thenReturn(true);
      rd = validator.run();
      assertFalse(rd.toString().contains(
         "Artifact from token [OwArtifactToken [id=9999, data=null]] does not exist to relate to artifact [OwArtifact [type=OwArtifactType [id=11, data=null], id=5555, data=null]] for relation [OwRelation [type=OwRelationType [sideA=false, sideName=null, id=65656, data=null], artToken=OwArtifactToken [id=9999, data=null], data=null]]."));
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
      assertTrue(rd.toString().contains("Branch [OwBranch [id=570]] not valid."));

      when(helper.isBranchExists(COMMON)).thenReturn(true);
      rd = validator.run();
      assertFalse(rd.toString().contains("Branch [OwBranch [id=570]] not valid."));
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
