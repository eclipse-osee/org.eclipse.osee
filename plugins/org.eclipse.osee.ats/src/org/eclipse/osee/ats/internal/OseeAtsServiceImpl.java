/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.internal;

import java.util.Collection;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 */
public class OseeAtsServiceImpl implements IOseeCmService {

   @Override
   public boolean isCmAdmin() {
      return AtsUtil.isAtsAdmin();
   }

   @Override
   public void openArtifact(String guid, OseeCmEditor oseeCmEditor) {
      AtsUtil.openArtifact(guid, oseeCmEditor);
   }

   @Override
   public void openArtifact(Artifact artifact, OseeCmEditor oseeCmEditor) {
      AtsUtil.openATSArtifact(artifact);
   }

   @Override
   public void openArtifacts(String name, Collection<Artifact> artifacts, OseeCmEditor oseeCmEditor) {
      WorldEditor.open(new WorldEditorSimpleProvider(name, artifacts));
   }

   @Override
   public KeyedImage getOpenImage(OseeCmEditor oseeCmEditor) {
      if (oseeCmEditor == OseeCmEditor.CmPcrEditor) {
         return AtsImage.TEAM_WORKFLOW;
      } else if (oseeCmEditor == OseeCmEditor.CmMultiPcrEditor) {
         return AtsImage.GLOBE;
      }
      return FrameworkImage.LASER;
   }

}
