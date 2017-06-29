/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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

   public void setupPrograms() {
      Collection<IAtsProgram> programs =
         AtsClientService.get().getProgramService().getPrograms(AtsArtifactTypes.Program);
      for (IAtsProgram program : programs) {
         if (program.isActive()) {
            programArts.add(AtsClientService.get().getArtifactById(program.getIdString()));
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
