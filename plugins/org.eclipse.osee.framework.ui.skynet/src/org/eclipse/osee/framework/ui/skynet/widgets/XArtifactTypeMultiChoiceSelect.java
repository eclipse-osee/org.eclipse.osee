/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;

/**
 * Multi selection of artifact types with checkbox dialog and filtering
 *
 * @author Donald G. Dunne
 */
public class XArtifactTypeMultiChoiceSelect extends XSelectFromDialog<ArtifactTypeToken> {

   public static final String WIDGET_ID = XArtifactTypeMultiChoiceSelect.class.getSimpleName();

   public XArtifactTypeMultiChoiceSelect() {
      super("Select Artifact Type(s)");
      try {
         setSelectableItems(ArtifactTypeManager.getAllTypes());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public FilteredCheckboxTreeDialog<ArtifactTypeToken> createDialog() {
      FilteredCheckboxTreeDialog<ArtifactTypeToken> dialog =
         new FilteredCheckboxTreeDialog<ArtifactTypeToken>(getLabel(), "Select from the items below",
            new ArrayTreeContentProvider(), new LabelProvider(), new ArtifactNameSorter());
      dialog.setInput(ArtifactTypeManager.getAllTypes());
      return dialog;
   }
}