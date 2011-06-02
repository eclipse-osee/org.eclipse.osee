/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.artifact;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeAttribute;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.DbUtil;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * This test is intended to be run against a demo database. It tests the purge logic by counting the rows of the version
 * and txs tables, creating artifacts, changing them and then purging them. If it works properly, all rows should be
 * equal.
 * 
 * @author Jeff C. Phillips
 */
public class AttributePurgeTest extends AbstractPurgeTest {
   private static final List<String> tables = Arrays.asList("osee_attribute", "osee_txs");

   @Override
   public void runPurgeOperation() throws OseeCoreException {
      // Create some software artifacts
      Branch branch = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_2.getName());
      SkynetTransaction transaction = new SkynetTransaction(branch, "Test purge artifacts");
      Collection<Artifact> softArts =
         FrameworkTestUtil.createSimpleArtifacts(CoreArtifactTypes.SoftwareRequirement, 10, getClass().getSimpleName(),
            branch);
      for (Artifact softArt : softArts) {
         softArt.persist(transaction);
      }
      transaction.execute();

      getPreTableCount();
      // make more changes to artifacts
      for (Artifact softArt : softArts) {
         softArt.addAttribute(CoreAttributeTypes.StaticId, getClass().getSimpleName());
         softArt.persist();
      }

      // Count rows and check that increased
      DbUtil.getTableRowCounts(postCreateArtifactsCount, tables);
      TestUtil.checkThatIncreased(preCreateArtifactsCount, postCreateArtifactsCount);

      Set<Attribute<?>> attributesToPurge = new HashSet<Attribute<?>>();
      for (Artifact softArt : softArts) {
         attributesToPurge.addAll(softArt.getAttributes(CoreAttributeTypes.StaticId));
      }
      new PurgeAttribute(attributesToPurge).execute();

      getPostTableCount();
   }

   @Override
   public List<String> getTables() {
      return tables;
   }
}
