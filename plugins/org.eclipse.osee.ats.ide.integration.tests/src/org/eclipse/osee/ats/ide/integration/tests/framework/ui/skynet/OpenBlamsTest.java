/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Open all blams and look for errors
 *
 * @author Donald G. Dunne
 */
@RunWith(Parameterized.class)
public class OpenBlamsTest {

   private final AbstractBlam blam;
   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   public OpenBlamsTest(AbstractBlam blam, String name) {
      this.blam = blam;
   }

   @Test
   public void test() throws Exception {
      try {
         BlamEditor.edit(blam);
         BlamEditor.closeAll();
      } catch (Exception ex) {
         Assert.fail(Lib.exceptionToString(ex));
      }
   }

   @Parameters(name = "Open BLAM Test: {index} - {1}")
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<>();
      ExtensionDefinedObjects<AbstractBlam> definedObjects =
         new ExtensionDefinedObjects<>("org.eclipse.osee.framework.ui.skynet.BlamOperation", "Operation", "className");
      for (AbstractBlam blam : definedObjects.getObjects()) {
         data.add(new Object[] {blam, blam.getName()});
      }
      return data;
   }
}
