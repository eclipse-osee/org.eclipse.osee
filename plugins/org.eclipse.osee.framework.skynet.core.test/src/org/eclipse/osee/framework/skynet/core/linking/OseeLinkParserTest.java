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

package org.eclipse.osee.framework.skynet.core.linking;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;

/**
 * @author Jeff C. Phillips
 */
public class OseeLinkParserTest {

   @org.junit.Test
   public void testNewSchoolLink() throws Exception {
      OseeLinkParser parser = new OseeLinkParser();
      String guid = GUID.create();
      BranchId branch = CoreBranches.COMMON;
      parser.parse(String.format("http://127.0.0.1:8081/Define?guid=%s&branchUuid=%s", guid, branch.getIdString()));

      Assert.assertEquals(guid, parser.getGuid());
      Assert.assertEquals(branch, parser.getId());
   }
}
