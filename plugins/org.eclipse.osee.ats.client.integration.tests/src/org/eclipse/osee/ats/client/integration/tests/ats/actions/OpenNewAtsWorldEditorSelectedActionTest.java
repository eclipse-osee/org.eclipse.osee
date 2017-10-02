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
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import java.util.Arrays;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorSelectedAction;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorSelectedAction.IOpenNewAtsWorldEditorSelectedHandler;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsWorldEditorSelectedActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenNewAtsWorldEditorSelectedAction createAction() {
      return new OpenNewAtsWorldEditorSelectedAction(new IOpenNewAtsWorldEditorSelectedHandler() {

         @Override
         public List<Artifact> getSelectedArtifacts()  {
            return Arrays.asList((Artifact) AtsTestUtil.getTeamWf());
         }

         @Override
         public CustomizeData getCustomizeDataCopy() {
            return null;
         }
      });
   }
}
