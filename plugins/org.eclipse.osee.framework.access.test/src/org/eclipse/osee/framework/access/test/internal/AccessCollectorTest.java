/*
 * Created on Jul 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access.test.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.exp.AccessCollector;
import org.eclipse.osee.framework.core.model.access.exp.ArtifactAccessFilter;
import org.eclipse.osee.framework.core.model.access.exp.AttributeTypeAccessFilter;
import org.eclipse.osee.framework.core.model.access.exp.BranchAccessFilter;
import org.eclipse.osee.framework.core.model.access.exp.IAccessFilter;
import org.junit.Test;

/**
 * @author Jeff C. Phillips
 */
public class AccessCollectorTest {

   @Test
   public void testAttrUseCase() {
      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "123");
      IAttributeType wordAttrType = CoreAttributeTypes.WORD_TEMPLATE_CONTENT;
      IAttributeType phoneType = CoreAttributeTypes.PHONE;
      List<IAccessFilter> filters = new ArrayList<IAccessFilter>();

      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(basicArtifact, PermissionEnum.READ);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(basicArtifact, PermissionEnum.WRITE);
      AttributeTypeAccessFilter wordTypeFilter =
         new AttributeTypeAccessFilter(PermissionEnum.WRITE, basicArtifact, wordAttrType);
      AttributeTypeAccessFilter phoneTypeFilter =
         new AttributeTypeAccessFilter(PermissionEnum.READ, basicArtifact, phoneType);

      filters.add(artifactAccessFilter);
      filters.add(branchAccessFilter);
      filters.add(phoneTypeFilter);
      filters.add(wordTypeFilter);

      AccessCollector collector = new AccessCollector(filters);
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.WRITE).size() == 1);
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.READ).size() == 2);
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.DENY).isEmpty());
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.FULLACCESS).isEmpty());
   }

   @Test
   public void testAttrUseCaseMissingBranchFilters() {
      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "123");
      IAttributeType wordAttrType = CoreAttributeTypes.WORD_TEMPLATE_CONTENT;
      IAttributeType phoneType = CoreAttributeTypes.PHONE;
      List<IAccessFilter> filters = new ArrayList<IAccessFilter>();

      AttributeTypeAccessFilter wordTypeFilter =
         new AttributeTypeAccessFilter(PermissionEnum.WRITE, basicArtifact, wordAttrType);
      AttributeTypeAccessFilter phoneTypeFilter =
         new AttributeTypeAccessFilter(PermissionEnum.READ, basicArtifact, phoneType);

      filters.add(phoneTypeFilter);
      filters.add(wordTypeFilter);

      AccessCollector collector = new AccessCollector(filters);
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.WRITE).size() == 1);
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.READ).size() == 2);
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.DENY).isEmpty());
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.FULLACCESS).isEmpty());
   }

   @Test
   public void testAttrUseCaseMissingAttrFilters() {
      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "123");
      IAttributeType wordAttrType = CoreAttributeTypes.WORD_TEMPLATE_CONTENT;
      IAttributeType phoneType = CoreAttributeTypes.PHONE;
      List<IAccessFilter> filters = new ArrayList<IAccessFilter>();

      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(basicArtifact, PermissionEnum.READ);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(basicArtifact, PermissionEnum.WRITE);

      filters.add(artifactAccessFilter);
      filters.add(branchAccessFilter);

      AccessCollector collector = new AccessCollector(filters);
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.WRITE).size() == 2);
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.READ).size() == 2);
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.DENY).isEmpty());
      Assert.assertTrue(collector.getAttributeTypes(basicArtifact, Arrays.asList(wordAttrType, phoneType),
         PermissionEnum.FULLACCESS).isEmpty());
   }
}
