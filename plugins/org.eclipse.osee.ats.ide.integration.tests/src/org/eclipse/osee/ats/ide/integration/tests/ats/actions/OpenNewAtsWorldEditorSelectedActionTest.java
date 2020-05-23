/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.actions;

import java.util.Arrays;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsWorldEditorSelectedAction;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsWorldEditorSelectedAction.IOpenNewAtsWorldEditorSelectedHandler;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsWorldEditorSelectedActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenNewAtsWorldEditorSelectedAction createAction() {
      return new OpenNewAtsWorldEditorSelectedAction(new IOpenNewAtsWorldEditorSelectedHandler() {

         @Override
         public List<Artifact> getSelectedArtifacts() {
            return Arrays.asList(AtsTestUtil.getTeamWf());
         }

         @Override
         public CustomizeData getCustomizeDataCopy() {
            return null;
         }
      });
   }
}
