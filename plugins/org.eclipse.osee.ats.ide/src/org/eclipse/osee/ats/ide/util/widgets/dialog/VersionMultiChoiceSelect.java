/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collections;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Angel Avila
 */
public class VersionMultiChoiceSelect extends XSelectFromDialog<IAtsVersion> {

   public static final String WIDGET_ID = VersionMultiChoiceSelect.class.getSimpleName();
   private LabelProvider labelProvider = null;

   public VersionMultiChoiceSelect() {
      super("Select Version(s)");
      setSelectableItems(Collections.<IAtsVersion> emptyList());
   }

   @Override
   public void createControls(Composite parent, int horizontalSpan, boolean fillText) {
      super.createControls(parent, horizontalSpan, fillText);
      GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
      layoutData.heightHint = 80;
      getStyledText().setLayoutData(layoutData);

   }

   @Override
   public FilteredCheckboxTreeDialog<IAtsVersion> createDialog() {
      FilteredCheckboxTreeDialog<IAtsVersion> dialog =
         new FilteredCheckboxTreeDialog<IAtsVersion>(getLabel(), "Select from the versions below",
            new ArrayTreeContentProvider(), new LabelProvider(), new AtsObjectNameSorter());
      dialog.setInput(AttributeTypeManager.getEnumerationValues(AtsAttributeTypes.ClosureState));
      dialog.setShowSelectButtons(true);
      return dialog;
   }

   @Override
   public LabelProvider getLabelProvider() {
      if (labelProvider == null) {
         labelProvider = new LabelProvider();
      }
      return labelProvider;
   }

   public void setLabelProvider(LabelProvider labelProvider) {
      this.labelProvider = labelProvider;
   }

}
