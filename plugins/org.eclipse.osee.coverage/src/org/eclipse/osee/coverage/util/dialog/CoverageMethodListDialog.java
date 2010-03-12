/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.util.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class CoverageMethodListDialog extends CheckedTreeSelectionDialog {

   public CoverageMethodListDialog(Collection<CoverageOption> values) {
      this(values, new ArrayList<CoverageOption>());
   }

   public CoverageMethodListDialog(Collection<CoverageOption> values, Collection<CoverageOption> selected) {
      super(Display.getCurrent().getActiveShell(), labelProvider, new ArrayTreeContentProvider());
      setTitle("Select Coverage Method(s)");
      setMessage("Select Coverage Method(s)");
      setComparator(new ArtifactNameSorter());
      try {
         setInput(values.toArray(new CoverageOption[values.size()]));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      setInitialSelections(selected.toArray(new CoverageOption[selected.size()]));
   }

   public Set<CoverageOption> getSelected() {
      Set<CoverageOption> selected = new HashSet<CoverageOption>();
      for (Object obj : getResult())
         selected.add((CoverageOption) obj);
      return selected;
   }

   static ILabelProvider labelProvider = new ILabelProvider() {

      public Image getImage(Object element) {
         return null;
      }

      public String getText(Object element) {
         if (element instanceof CoverageOption) {
            return ((CoverageOption) element).getNameDesc();
         }
         return "Unknown";
      }

      public void addListener(ILabelProviderListener listener) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object element, String property) {
         return false;
      }

      public void removeListener(ILabelProviderListener listener) {
      }

   };

}
