/*
 * Created on Apr 18, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.util;

import static org.junit.Assert.assertTrue;
import java.util.Collection;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;

/**
 * @author Donald G. Dunne
 */
public class MassEditorUtil {

   public static MassArtifactEditor getSingleEditorOrFail() {
      // Retrieve results from opened editor and test
      Collection<MassArtifactEditor> editors = MassArtifactEditor.getEditors();
      assertTrue("Expecting 1 editor open, currently " + editors.size(), editors.size() == 1);
      return editors.iterator().next();
   }

}
