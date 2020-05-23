/*********************************************************************
 * Copyright (c) 2015 Boeing
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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class FilteredTreeEntryDialog extends FilteredTreeDialog {

   private String entryValue = null;
   private final String entryName;
   private XText xText = null;
   private Collection<?> selectable;

   public FilteredTreeEntryDialog(String title, String message, String entryName, ITreeContentProvider contentProvider, ILabelProvider labelProvider) {
      super(title, message, contentProvider, labelProvider);
      this.entryName = entryName;
   }

   public FilteredTreeEntryDialog(String title, String message, String entryName, Collection<?> selectable) {
      this(title, message, entryName, new ArrayTreeContentProvider(), new StringLabelProvider());
      this.selectable = selectable;
   }

   @Override
   protected void createPreCustomArea(Composite parent) {
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
      super.createPreCustomArea(parent);
      if (selectable != null && !selectable.isEmpty()) {
         setInput(selectable);
      }
   }

   /**
    * @return the entryValue
    */
   public String getEntryValue() {
      return entryValue;
   }

   /**
    * @param entryValue the entryValue to set
    */
   public void setEntryValue(String entryValue) {
      this.entryValue = entryValue;
   }

   public ArtifactTypeToken getSelection() {
      return getSelectedFirst();
   }

}
