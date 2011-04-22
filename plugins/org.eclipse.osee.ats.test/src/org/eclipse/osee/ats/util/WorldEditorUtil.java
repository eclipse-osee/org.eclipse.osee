/*
 * Created on Apr 18, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import static org.junit.Assert.assertTrue;
import java.util.Collection;
import org.eclipse.osee.ats.world.WorldEditor;

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
