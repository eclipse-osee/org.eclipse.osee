/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactSelectWidgetWithSave;

/**
 * @author Donald G. Dunne
 */
public class XProgramSelectionWidget extends XArtifactSelectWidgetWithSave {

   public static final String WIDGET_ID = XProgramSelectionWidget.class.getSimpleName();
   private final List<ArtifactId> programArts = new ArrayList<>();

   public XProgramSelectionWidget() {
      super("Program");
      setupPrograms();
   }

   @Override
   public Artifact getStored() {
      ArtifactId artId = getArtifact().getSoleAttributeValue(AtsAttributeTypes.ProgramId, null);
      return ArtifactQuery.getArtifactFromId(artId, getArtifact().getBranch());
   }

   public void setupPrograms() {
      Collection<IAtsProgram> programs =
         AtsClientService.get().getProgramService().getPrograms(AtsArtifactTypes.Program);
      for (IAtsProgram program : programs) {
         if (program.isActive()) {
            programArts.add(AtsClientService.get().getQueryService().getArtifactById(program.getIdString()));
         }
      }
   }

   @Override
   public Collection<Artifact> getSelectableArtifacts() {
      Collection<ArtifactToken> programArts =
         AtsClientService.get().getQueryService().createQuery(AtsArtifactTypes.Program).andAttr(
            AtsAttributeTypes.Active, "true").getArtifacts();
      return Collections.castAll(programArts);
   }
}
