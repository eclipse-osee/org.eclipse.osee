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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class AttributeTypeFilteredCheckTreeDialog extends FilteredCheckboxTreeDialog {

   private Collection<IAttributeType> selectableTypes;

   public AttributeTypeFilteredCheckTreeDialog(String title, String message) {
      super(title, message, new AttributeContentProvider(), new AttributeTypeLabelProvider(), new ArtifactNameSorter());
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setInput(
            selectableTypes == null ? AttributeTypeManager.getAllTypes() : selectableTypes);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return comp;
   }

   @Override
   protected Result isComplete() {
      return super.isComplete();
   }

   public Collection<IAttributeType> getSelectableTypes() {
      return selectableTypes;
   }

   public void setSelectableTypes(Collection<IAttributeType> selectableTypes) {
      this.selectableTypes = selectableTypes;
   }

}
