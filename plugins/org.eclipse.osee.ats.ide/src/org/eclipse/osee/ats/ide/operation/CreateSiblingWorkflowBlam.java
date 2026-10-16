/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.operation;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.util.IsEnabled;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class CreateSiblingWorkflowBlam extends ModifyActionableItemsBlam implements IsEnabled {

   private static final String BLAM_NAME = "Create Sibling Workflows";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public CreateSiblingWorkflowBlam() {
      this(BLAM_NAME, AtsImage.CHANGE_REQUEST);
   }

   public CreateSiblingWorkflowBlam(String name, OseeImage image) {
      super(name, image);
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   @Override
   protected void createTreeViewers(Composite parent) {
      super.createTreeViewers(parent);
      newTree.setEnabledChecker(this);
   }

   @Override
   protected int getLayoutColumns() {
      return 1;
   }

   @Override
   protected boolean displayWfTree() {
      return false;
   }

   @Override
   protected boolean displayDuplicateButton() {
      return false;
   }

   @Override
   protected boolean displayOtherTree() {
      return false;
   }

   @Override
   public String getRunText() {
      return BLAM_NAME;
   }

   @Override
   public String getOutputMessage() {
      return "Not yet run.";
   }

   @Override
   public String getTabTitle() {
      return BLAM_NAME;
   }

   @Override
   public String getTitle() {
      return BLAM_NAME;
   }

   @Override
   public String getDropLabelStr() {
      return "Team Workflow";
   }

   @Override
   public boolean isEnabled(Object obj) {
      if (obj instanceof IAtsActionableItem) {
         return ((IAtsActionableItem) obj).isActive() && ((IAtsActionableItem) obj).isActionable();
      }
      return false;
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public String getDescriptionUsage() {
      return "Create sibling workflows in this Action.  Select AIs and select Create button.";
   }

}
