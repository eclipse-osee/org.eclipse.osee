/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Megumi Telles
 */
public class VersionTreeDialog extends FilteredCheckboxTreeDialog {

   protected Composite dialogComp;

   public VersionTreeDialog(Active active) {
      this(active, Collections.emptyList());
   }

   public VersionTreeDialog(Active active, Collection<IAtsVersion> versions) {
      super("Select Version", "Select Version", new ArrayTreeContentProvider(), new AtsObjectLabelProvider(),
         new AtsObjectNameSorter());
      setInput(versions);
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

}
