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
package org.eclipse.osee.ats.core.workdef;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionSheetTest {

   @Test
   public void testWorkDefinitionSheet() {
      WorkDefinitionSheet sheet = new WorkDefinitionSheet("name", new File("\\file\\path"));
      Assert.assertNotNull(sheet.getFile());
      Assert.assertEquals("name", sheet.getName());
      Assert.assertEquals("name   - file[\\file\\path]", sheet.toString());
   }
}
