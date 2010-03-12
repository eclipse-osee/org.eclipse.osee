/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.internal;

import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ats.IOseeAtsService;
import org.eclipse.osee.framework.ui.skynet.ats.OseeEditor;

/**
 * @author Roberto E. Escobar
 */
public class OseeAtsServiceImpl implements IOseeAtsService {

   @Override
   public boolean isAtsAdmin() {
      return AtsUtil.isAtsAdmin();
   }

   @Override
   public void openArtifact(Artifact artifact) {
      AtsUtil.openATSArtifact(artifact);
   }

   @Override
   public void openArtifact(String guid, OseeEditor view) {
      AtsUtil.openArtifact(guid, view);
   }

}
