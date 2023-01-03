/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateArtifactValidator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author John Misinco
 */
public class TemplateArtifactValidatorTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   private BranchToken testBranch;

   @Before
   public void setup() {
      testBranch = BranchManager.createWorkingBranch(CoreBranches.COMMON, testInfo.getQualifiedTestName());
   }

   @After
   public void tearDown() {
      BranchManager.deleteBranchAndPend(testBranch);
   }

   @Test
   public void testIsApplicable() {
      Artifact newTemplate = ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, testBranch);
      newTemplate.persist(testInfo.getQualifiedTestName());
      Artifact newNonTemplate = ArtifactTypeManager.addArtifact(CoreArtifactTypes.DesignMsWord, testBranch);
      newNonTemplate.persist(testInfo.getQualifiedTestName());

      TemplateArtifactValidator validator = new TemplateArtifactValidator();
      for (AttributeTypeToken attrType : AttributeTypeManager.getAllTypes()) {
         boolean correctAttrType = false;
         if (attrType.equals(CoreAttributeTypes.TemplateMatchCriteria)) {
            correctAttrType = true;
         }
         Assert.assertEquals(correctAttrType, validator.isApplicable(newTemplate, attrType));
         Assert.assertFalse(validator.isApplicable(newNonTemplate, attrType));
      }
   }

   @Test
   public void testValidateOkStatus() {
      Artifact newTemplate = ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, testBranch);
      newTemplate.persist(testInfo.getQualifiedTestName());
      TemplateArtifactValidator validator = new TemplateArtifactValidator();
      String templateId = GUID.create();
      XResultData status = validator.validate(newTemplate, CoreAttributeTypes.TemplateMatchCriteria, templateId);
      Assert.assertTrue(status.isOK());
      newTemplate.addAttributeFromString(CoreAttributeTypes.TemplateMatchCriteria, templateId);
      newTemplate.persist(testInfo.getQualifiedTestName());

      newTemplate = ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, testBranch);
      newTemplate.persist(testInfo.getQualifiedTestName());
      templateId = GUID.create();
      status = validator.validate(newTemplate, CoreAttributeTypes.TemplateMatchCriteria, templateId);
      Assert.assertTrue(status.isOK());
      newTemplate.persist(testInfo.getQualifiedTestName());
   }

   @Test
   public void testDuplicateCriteriaOnSeparateBranches() {
      Artifact newTemplate = ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, testBranch);
      newTemplate.persist(testInfo.getQualifiedTestName());
      TemplateArtifactValidator validator = new TemplateArtifactValidator();
      String templateId = GUID.create();
      XResultData status = validator.validate(newTemplate, CoreAttributeTypes.TemplateMatchCriteria, templateId);
      Assert.assertTrue(status.isOK());
      newTemplate.addAttributeFromString(CoreAttributeTypes.TemplateMatchCriteria, templateId);
      newTemplate.persist(testInfo.getQualifiedTestName());

      BranchToken testBranch2 =
         BranchManager.createWorkingBranch(CoreBranches.COMMON, testInfo.getQualifiedTestName() + "2");
      Artifact newTemplate2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, testBranch2);
      newTemplate2.persist(testInfo.getQualifiedTestName());
      status = validator.validate(newTemplate2, CoreAttributeTypes.TemplateMatchCriteria, templateId);
      Assert.assertTrue(status.isOK());
      newTemplate2.addAttributeFromString(CoreAttributeTypes.TemplateMatchCriteria, templateId);
      newTemplate2.persist(testInfo.getQualifiedTestName());
      BranchManager.deleteBranch(testBranch2);
   }

   @Test
   public void testValidateErrorStatus() {
      TemplateArtifactValidator validator = new TemplateArtifactValidator();
      String templateId = GUID.create();

      Artifact newTemplate = ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplateWholeWord, testBranch);
      newTemplate.addAttributeFromString(CoreAttributeTypes.TemplateMatchCriteria, templateId);
      newTemplate.persist(testInfo.getQualifiedTestName());

      // since newTemplate was already persisted with templateId, this check will produce and error status
      XResultData status = validator.validate(newTemplate, CoreAttributeTypes.TemplateMatchCriteria, templateId);
      Assert.assertFalse(status.isOK());
   }
}
