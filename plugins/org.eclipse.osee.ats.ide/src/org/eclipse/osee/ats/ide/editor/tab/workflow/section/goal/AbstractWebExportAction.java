/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.ide.editor.tab.workflow.section.goal;

import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class AbstractWebExportAction extends AbstractAtsAction {

   protected final GoalArtifact goalArt;
   protected final WorkflowEditor editor;

   public AbstractWebExportAction(String string, GoalArtifact goalArt, WorkflowEditor editor, OseeImage image) {
      super(string, ImageManager.getImageDescriptor(image));
      this.goalArt = goalArt;
      this.editor = editor;
   }

   protected String validateAndGetCustomizeDataGuid() {
      String custGuid = AtsApiService.get().getAttributeResolver().getSoleAttributeValue((ArtifactId) goalArt,
         AtsAttributeTypes.WorldResultsCustId, "");
      if (Strings.isInValid(custGuid)) {
         AWorkbench.popup("Error", "No customization id (WorldResultsCustId) configured for Goal %s",
            goalArt.getName());
         return null;
      }

      CustomizeData customization = AtsApiService.get().getStoreService().getCustomizationByGuid(custGuid);
      if (customization == null) {
         AWorkbench.popup("Error", "No customization found with id [%s]\n\nNote: Customization must be saved.",
            custGuid);
         return null;
      }
      return custGuid;
   }

}
