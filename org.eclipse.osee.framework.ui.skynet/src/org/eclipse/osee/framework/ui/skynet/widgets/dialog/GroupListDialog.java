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
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.ArrayList;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class GroupListDialog extends ArtifactListDialog {

   public GroupListDialog(Shell parent) {
      super(parent, null);
      setTitle("Select group");
      setMessage("Select group");
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      for (Artifact art : UniversalGroup.getGroups(BranchPersistenceManager.getInstance().getDefaultBranch()))
         if (!art.getDescriptiveName().equals(ArtifactPersistenceManager.ROOT_ARTIFACT_TYPE_NAME)) arts.add(art);
      setArtifacts(arts);
   }
}
