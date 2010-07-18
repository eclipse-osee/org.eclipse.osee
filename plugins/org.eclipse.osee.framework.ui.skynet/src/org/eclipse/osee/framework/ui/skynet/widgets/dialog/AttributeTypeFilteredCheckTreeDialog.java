/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEECheckedFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class AttributeTypeFilteredCheckTreeDialog extends OSEECheckedFilteredTreeDialog {

   private static PatternFilter patternFilter = new PatternFilter();
   private Collection<AttributeType> selectableTypes;

   public AttributeTypeFilteredCheckTreeDialog(String title, String message) {
      super(title, message, patternFilter, new AttributeContentProvider(), new AttributeTypeLabelProvider(),
            new ArtifactNameSorter());
   }

   public Collection<AttributeType> getChecked() {
      if (super.getTreeViewer() == null) return Collections.emptyList();
      Set<AttributeType> checked = new HashSet<AttributeType>();
      for (Object obj : super.getTreeViewer().getChecked()) {
         checked.add((AttributeType) obj);
      }
      return checked;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setInput(
               selectableTypes == null ? AttributeTypeManager.getAllTypes() : selectableTypes);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return comp;
   }

   @Override
   protected Result isComplete() {
      return super.isComplete();
   }

   public Collection<AttributeType> getSelectableTypes() {
      return selectableTypes;
   }

   public void setSelectableTypes(Collection<AttributeType> selectableTypes) {
      this.selectableTypes = selectableTypes;
   }

}
