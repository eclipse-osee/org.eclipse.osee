/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class FilteredTreeArtifactTypeEntryDialog extends FilteredTreeArtifactTypeDialog {

   private String entryValue = null;
   private final String entryName;
   private XText xText = null;

   public FilteredTreeArtifactTypeEntryDialog(String title, String message, String entryName) {
      super(title, message);
      this.entryName = entryName;
   }

   public FilteredTreeArtifactTypeEntryDialog(String title, String message, String entryName, Collection<? extends ArtifactTypeToken> selectable) {
      super(title, message, selectable, new ArtifactTypeLabelProvider());
      this.entryName = entryName;
   }

   @Override
   protected void createPreCustomArea(Composite parent) {
      super.createPreCustomArea(parent);
      xText = new XText(entryName);
      if (entryValue != null) {
         xText.setText(entryValue);
      }
      xText.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            entryValue = xText.get();
            updateStatusLabel();
         }
      });
      xText.createWidgets(parent, 2);
   }

   public String getEntryValue() {
      return entryValue;
   }

   public void setEntryValue(String entryValue) {
      this.entryValue = entryValue;
   }

   @Override
   protected Result isComplete() {
      if (!Strings.isValid(entryValue)) {
         return new Result("Must enter Artifact name.");
      }
      return super.isComplete();
   }

   public ArtifactTypeToken getSelection() {
      return getSelectedFirst();
   }

}
