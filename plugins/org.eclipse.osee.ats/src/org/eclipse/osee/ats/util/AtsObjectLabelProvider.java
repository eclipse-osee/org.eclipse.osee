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
package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class AtsObjectLabelProvider extends LabelProvider {
   private final boolean showActive;
   private final boolean showAtsId;

   public AtsObjectLabelProvider() {
      this(false);
   }

   public AtsObjectLabelProvider(boolean showActive) {
      this(showActive, false);
   }

   public AtsObjectLabelProvider(boolean showActive, boolean showAtsId) {
      super();
      this.showActive = showActive;
      this.showAtsId = showAtsId;
   }

   @Override
   public Image getImage(Object element) {
      if (element instanceof Artifact) {
         return ArtifactImageManager.getImage(AtsClientService.get().getQueryServiceClient().getArtifact(element));
      } else if (element instanceof IAtsActionableItem) {
         return ArtifactImageManager.getImage(AtsArtifactTypes.ActionableItem);
      } else if (element instanceof IAtsTeamDefinition) {
         return ArtifactImageManager.getImage(AtsArtifactTypes.TeamDefinition);
      } else if (element instanceof IAtsVersion) {
         return ArtifactImageManager.getImage(AtsArtifactTypes.Version);
      } else if (element instanceof Match && ((Match) element).getElement() instanceof Artifact) {
         return ArtifactImageManager.getImage(
            AtsClientService.get().getQueryServiceClient().getArtifact(((Match) element).getElement()));
      }
      return ImageManager.getImage(ImageManager.MISSING);
   }

   @Override
   public String getText(Object element) {
      String result = "";
      if (element != null) {
         if (element instanceof Match) {
            element = ((Match) element).getElement();
         }
         if (element instanceof IAtsObject) {
            IAtsObject artifact = (IAtsObject) element;

            List<String> extraInfo = new ArrayList<>();
            String name = artifact.getName();
            extraInfo.add(name != null ? name : "");
            result = Collections.toString(" ", extraInfo);
         } else {
            result = element.toString();
         }
      }
      result = getAtsId(element, result);
      result = getActiveTag(element, result);
      return result;
   }

   private String getAtsId(Object element, String result) {
      if (showAtsId && element instanceof IAtsWorkItem) {
         IAtsWorkItem iAtsObject = (IAtsWorkItem) element;
         result = String.format("[%s] - %s", iAtsObject.getAtsId(), result);
      }
      return result;
   }

   private String getActiveTag(Object element, String result) {
      if (showActive && element instanceof IAtsConfigObject) {
         IAtsConfigObject iAtsObject = (IAtsConfigObject) element;
         result = String.format("%s (%s)", result, iAtsObject.isActive() ? "Active" : "InActive");
      }
      return result;
   }
}
