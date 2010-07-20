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
