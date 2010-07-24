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

package org.eclipse.osee.framework.skynet.core.test.cases;

import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkParser;

/**
 * @author Jeff C. Phillips
 */
public class OseeLinkParserTest {

   @org.junit.Test
   public void testOldSchoolLink() throws Exception {
      OseeLinkParser parser = new OseeLinkParser();
      String guid = GUID.create();
      parser.parse(String.format("http://127.0.0.1:8081/get/guid/%s/Define", guid));
     
      Assert.assertEquals(guid, parser.getGuid());
   }
   
   @org.junit.Test
   public void testNewSchoolLink() throws Exception {
      OseeLinkParser parser = new OseeLinkParser();
      String guid = GUID.create();
      int branchId = 12;
      parser.parse(String.format("http://127.0.0.1:8081/Define?guid=%s&branchId=%s", guid, branchId));
      
      Assert.assertEquals(guid, parser.getGuid());
      Assert.assertEquals(branchId, parser.getId());
   }
}
