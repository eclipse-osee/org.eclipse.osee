/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.integration;

import static org.junit.Assert.assertEquals;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.integration.util.DispositionInitializer;
import org.eclipse.osee.disposition.rest.integration.util.DispositionIntegrationRule;
import org.eclipse.osee.disposition.rest.integration.util.DispositionTestUtil;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;

/**
 * @author Angel Avila
 */
public class DispositionIntegrationTest {

   @Rule
   public TemporaryFolder folder = new TemporaryFolder();

   @Rule
   public TestRule rule = DispositionIntegrationRule.integrationRule(this);

   @OsgiService
   public OrcsApi orcsApi;

   @OsgiService
   public DispoApi dispoApi;

   @Before
   public void setUp() throws Exception {
      DispositionInitializer initializer = new DispositionInitializer(orcsApi, dispoApi);
      initializer.initialize();

      // Get the sample TMO file into a file in a temp folder
      File newFile = folder.newFile("sampleTmo.tmo");

      URL resource = getClass().getResource("../../../../../../../support/sampleTmo.tmo");
      InputStream stream = null;
      FileOutputStream output = null;
      try {
         stream = new BufferedInputStream(resource.openStream());

         output = new FileOutputStream(newFile);
         byte[] buffer = new byte[1024];
         int bytesRead;
         while ((bytesRead = stream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
         }
      } finally {
         Lib.close(stream);
         Lib.close(output);
      }
   }

   @Test
   public void testDispositionApi() {
      // We have one item with discrepancies: 1-10, 12-20, 23, 25, 32-90

      BranchId branch = DispositionTestUtil.SAW_Bld_1_FOR_DISPO;

      List<DispoSet> dispoSets = dispoApi.getDispoSets(branch, "code_coverage");
      DispoSet devSet = dispoSets.get(0);
      String devSetId = devSet.getGuid();

      DispoSetData devSetEdited = new DispoSetData();
      devSetEdited.setImportPath(folder.getRoot().toString());
      dispoApi.editDispoSet(branch, devSetId, devSetEdited, DemoUsers.Kay_Jones.getIdString());

      DispoSetData devSetEdited2 = new DispoSetData();
      devSetEdited2.setOperation(DispoStrings.Operation_Import);
      dispoApi.editDispoSet(branch, devSetId, devSetEdited2, DemoUsers.Kay_Jones.getIdString());

      // should have new items now
      List<DispoItem> dispoItems = dispoApi.getDispoItems(branch, devSetId, false);
      assertEquals(5, dispoItems.size());
   }

}
