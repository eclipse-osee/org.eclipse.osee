/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collections;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;

public class XArtifactMultiChoiceSelect extends XSelectFromDialog<Artifact> {

   public static final String WIDGET_ID = XArtifactMultiChoiceSelect.class.getSimpleName();

   public XArtifactMultiChoiceSelect() {
      super("Select Artifact (s)");
      setSelectableItems(Collections.<Artifact> emptyList());
   }

   @Override
   public FilteredCheckboxTreeDialog<Artifact> createDialog() {
      FilteredCheckboxTreeDialog<Artifact> dialog = new FilteredCheckboxTreeDialog<Artifact>(getLabel(),
         "Select from the items below", new ArrayTreeContentProvider(), new LabelProvider(), new ArtifactNameSorter());
      return dialog;
   }

}
