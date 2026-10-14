/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXBranchSelWidget extends XXAbstractBaseXWidget<BranchToken> {

   public static final WidgetId ID = WidgetId.XXBranchSelWidget;

   boolean workingBranchesOnly = false;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXBranchSelWidget() {
      this(ID, "Branch");
   }

   public XXBranchSelWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      remove(XOption.VAL_SINGLE_SEL);
      set(XOption.VAL_SINGLE_TEXT, XOption.NO_INLINE_EDIT, XOption.FILL_HORZ);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createLabelWidget() {
      super.createLabelWidget();
      labelLinkWidget.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseDown(MouseEvent event) {
            if (event.button == 3) {
               setSelected(CoreBranches.COMMON);
               notifyXModifiedListeners();
            }
         }

      });
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   protected boolean handleSelection() {
      if (isWorkingBranchesOnly()) {
         BranchToken newBranch = BranchSelectionDialog.getWorkingBranchFromUser();
         if (newBranch != null) {
            setSelected(newBranch);
            handleSelectedPersist();
            return true;
         } else {
            handleClear();
         }
      } else {
         BranchToken newBranch = BranchSelectionDialog.getBranchFromUser();
         if (newBranch != null) {
            setSelected(newBranch);
            handleSelectedPersist();
            return true;
         } else {
            handleClear();
         }
      }
      return false;
   }

   @Override
   public String getDisplayValue() {
      if (getSelectedFirst().isInvalid()) {
         return " Not Set";
      }
      return " " + super.getDisplayValue();
   }

   @Override
   public BranchToken getSentinel() {
      return BranchToken.SENTINEL;
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof BranchToken;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public boolean isWorkingBranchesOnly() {
      return workingBranchesOnly;
   }

   public void setWorkingBranchesOnly(boolean workingBranchesOnly) {
      this.workingBranchesOnly = workingBranchesOnly;
   }

   public BranchToken getBranch() {
      return getSelectedFirst();
   }

   public void setBranch(BranchToken branch) {
      setSelected(branch);
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact()) {
         BranchId branchId = getArtifact().getSoleAttributeValue(getAttributeType(), BranchId.SENTINEL);
         if (branchId.isValid()) {
            BranchToken branch = BranchManager.getBranch(branchId);
            if (branch.isValid()) {
               setSelected(branch, false);
            }
         }
      }
   }

}
