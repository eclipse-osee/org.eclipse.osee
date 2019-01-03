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
package org.eclipse.osee.ats.ide.search.widget;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;

/**
 * @author Donald G. Dunne
 */
public class ProgramSearchWidget extends AbstractXComboViewerSearchWidget<IAtsProgram> {

   public static final String PROGRAM = "Program";

   public ProgramSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(PROGRAM, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         ArtifactId programId = ArtifactId.valueOf(data.getProgramId());
         XComboViewer combo = getWidget();
         if (programId.isValid()) {
            IAtsProgram program = AtsClientService.get().getProgramService().getProgramById(programId);
            combo.setSelected(Arrays.asList(program));
         }
      }
   }

   @Override
   public Collection<IAtsProgram> getInput() {
      return Collections.castAll(AtsClientService.get().getProgramService().getPrograms());
   }

}
