/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.ide.config.program.IAtsProgramManager;
import org.eclipse.osee.ats.ide.config.program.ProgramManagers;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XAtsProgramComboWidget extends XComboViewer {

   protected Collection<IAtsProgram> atsPrograms = new ArrayList<>();

   public XAtsProgramComboWidget(String displayLabel, Collection<IAtsProgram> atsPrograms) {
      super(displayLabel, SWT.READ_ONLY);
      this.atsPrograms = atsPrograms;
      setLabelProvider(new AtsProgramLabelProvider());
      setContentProvider(new ArrayContentProvider());
      setComparator(new StringNameComparator());
   }

   public XAtsProgramComboWidget(Collection<IAtsProgram> atsPrograms) {
      this("ATS Program", atsPrograms);
   }

   public XAtsProgramComboWidget() {
      this("ATS Program", getAllPrograms());
   }

   private static Collection<IAtsProgram> getAllPrograms() {
      List<IAtsProgram> programs = new ArrayList<>();
      for (IAtsProgramManager manager : ProgramManagers.getAtsProgramManagers()) {
         programs.addAll(manager.getPrograms());
      }
      return programs;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      setComparator(new AtsProgramViewerSorter());
      reload();
   }

   public void reload() {
      Collection<Object> objs = new ArrayList<>();
      for (IAtsProgram proj : atsPrograms) {
         objs.add(proj);
      }
      setInput(objs);
   }
}
