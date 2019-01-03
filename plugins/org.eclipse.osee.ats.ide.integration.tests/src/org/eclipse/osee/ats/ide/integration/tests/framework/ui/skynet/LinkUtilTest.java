/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.framework.ui.skynet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.account.rest.model.Link;
import org.eclipse.osee.framework.ui.skynet.links.LinkUtil;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class LinkUtilTest {

   @Test
   public void test() throws Exception {
      for (Boolean global : Arrays.asList(false, true)) {
         AccountWebPreferences data = LinkUtil.getAccountsPreferencesData(global);
         assertNotNull(data);
         assertTrue(data.getLinks().isEmpty());

         Link link = new Link();
         link.setId("my.id");
         link.setName("Test URL");
         link.setUrl("http://www.test.com");
         data.getLinks().put(link.getId(), link);

         LinkUtil.saveWebPreferences(data, global);

         data = LinkUtil.getAccountsPreferencesData(global);
         assertNotNull(data);
         assertEquals(1, data.getLinks().size());
         Link link2 = data.getLinks().get("my.id");
         assertEquals("my.id", link2.getId());
         assertEquals("Test URL", link2.getName());
         assertEquals("http://www.test.com", link2.getUrl());

         link2.setName("Test URL2");
         link2.setUrl("http://www.test2.com");
         LinkUtil.addUpdateLink(link2, global);
         data = LinkUtil.getAccountsPreferencesData(global);
         assertNotNull(data);
         assertEquals(1, data.getLinks().size());
         Link link3 = data.getLinks().get("my.id");
         assertEquals("my.id", link3.getId());
         assertEquals("Test URL2", link3.getName());
         assertEquals("http://www.test2.com", link3.getUrl());

         LinkUtil.deleteLink(link2, global);

         data = LinkUtil.getAccountsPreferencesData(global);
         assertNotNull(data);
         assertTrue(data.getLinks().isEmpty());

      }
   }
}
