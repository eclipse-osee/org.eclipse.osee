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
package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.Collections;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.MinMaxOSEECheckedFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromDialog;
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
   public MinMaxOSEECheckedFilteredTreeDialog createDialog() {
      SimpleCheckFilteredTreeDialog dialog =
         new SimpleCheckFilteredTreeDialog(getLabel(), "Select from the versions below",
            new ArrayTreeContentProvider(), getLabelProvider(), new AtsObjectNameSorter(), 1, 1000);
      return dialog;
   }

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
