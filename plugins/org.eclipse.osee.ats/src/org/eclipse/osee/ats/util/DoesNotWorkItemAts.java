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
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemAts extends XNavigateItemAction {

   public DoesNotWorkItemAts(XNavigateItem parent) {
      super(parent, "Does Not Work - ATS - Task Realated To Artifact", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      TaskArtifact taskArt = (TaskArtifact) ArtifactQuery.getArtifactFromId(167072066, AtsUtilCore.getAtsBranch());

      Artifact robotReq = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirement, "Robot API",
         DemoBranches.SAW_Bld_1);

      taskArt.addAttribute(AtsAttributeTypes.TaskToChangedArtifactReference, robotReq);
      taskArt.persist(getClass().getSimpleName());

      System.err.println("dirty (false) " + taskArt.isDirty());
      Artifact robotReq2 = taskArt.getSoleAttributeValue(AtsAttributeTypes.TaskToChangedArtifactReference, null);

      System.err.println(String.format("Robot Artifact  [%s]", robotReq2));
   }

}
