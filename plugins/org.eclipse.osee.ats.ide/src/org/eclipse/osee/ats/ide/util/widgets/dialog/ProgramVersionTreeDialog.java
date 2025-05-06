/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class ProgramVersionTreeDialog extends FilteredCheckboxTreeDialog<ProgramVersion> {

   protected Composite dialogComp;

   public ProgramVersionTreeDialog(String title, String message, Collection<ProgramVersion> programVersions) {
      super(title, message, new ArrayTreeContentProvider(), new AtsObjectLabelProvider(), new ProgramVersionSorter());
      setInput(programVersions);

   }

   public ProgramVersionTreeDialog(Collection<ProgramVersion> programVersions) {
      this("Select Program/Version", "Select Program/Version", programVersions);
   }

   /**
    * @return selected team defs and children if recurseChildren was checked
    */
   public Collection<Version> getResultVersions() {
      Set<Version> versions = new HashSet<>(10);
      for (Object obj : getResult()) {
         versions.add((Version) obj);
      }
      return versions;
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control control = super.createDialogArea(container);
      dialogComp = new Composite(control.getParent(), SWT.NONE);
      dialogComp.setLayout(new GridLayout(2, false));
      dialogComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      return container;
   }

   public static class ProgramVersionSorter extends ViewerComparator {

      @Override
      public int compare(Viewer viewer, Object o1, Object o2) {
         if (o1 instanceof ProgramVersion && o2 instanceof ProgramVersion) {
            return getComparator().compare(((ProgramVersion) o1).getProgVerArt().getName(),
               ((ProgramVersion) o2).getProgVerArt().getName());
         }
         return super.compare(viewer, o1, o2);
      }

   }
}
