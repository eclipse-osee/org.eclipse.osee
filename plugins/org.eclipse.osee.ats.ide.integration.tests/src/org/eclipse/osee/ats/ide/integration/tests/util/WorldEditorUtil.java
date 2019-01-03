/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.util;

import static org.junit.Assert.assertTrue;
import java.util.Collection;
import org.eclipse.osee.ats.ide.world.WorldEditor;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorUtil {

   public static WorldEditor getSingleEditorOrFail() {
      // Retrieve results from opened editor and test
      Collection<WorldEditor> editors = WorldEditor.getEditors();
      assertTrue("Expecting 1 editor open, currently " + editors.size(), editors.size() == 1);
      return editors.iterator().next();
   }

}
