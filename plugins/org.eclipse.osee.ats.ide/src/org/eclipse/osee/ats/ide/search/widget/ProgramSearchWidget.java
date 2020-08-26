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

package org.eclipse.osee.ats.ide.search.widget;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
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
            IAtsProgram program = AtsApiService.get().getProgramService().getProgramById(programId);
            combo.setSelected(Arrays.asList(program));
         }
      }
   }

   @Override
   public Collection<IAtsProgram> getInput() {
      return Collections.castAll(AtsApiService.get().getProgramService().getPrograms());
   }

}
