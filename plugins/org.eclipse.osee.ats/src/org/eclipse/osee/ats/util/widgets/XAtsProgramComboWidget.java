/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.core.client.config.IAtsProgramClient;
import org.eclipse.osee.ats.core.client.config.IAtsProgramManager;
import org.eclipse.osee.ats.core.client.config.ProgramManagers;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.StringNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XAtsProgramComboWidget extends XComboViewer {

   private final Collection<? extends IAtsProgramClient> atsPrograms;

   public XAtsProgramComboWidget(String displayLabel, Collection<? extends IAtsProgramClient> atsPrograms) {
      super(displayLabel, SWT.READ_ONLY);
      this.atsPrograms = atsPrograms;
      setLabelProvider(new AtsProgramLabelProvider());
      setContentProvider(new ArrayContentProvider());
      setSorter(new StringNameSorter());
   }

   public XAtsProgramComboWidget(IAtsProgramManager atsProgramManager) throws OseeCoreException {
      this(atsProgramManager.getName() + " Program", atsProgramManager.getPrograms());
   }

   public XAtsProgramComboWidget() throws OseeCoreException {
      this("ATS Program", getAllPrograms());
   }

   private static Collection<? extends IAtsProgramClient> getAllPrograms() throws OseeCoreException {
      List<IAtsProgramClient> programs = new ArrayList<IAtsProgramClient>();
      for (IAtsProgramManager manager : ProgramManagers.getAtsProgramManagers()) {
         programs.addAll(manager.getPrograms());
      }
      return programs;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      setSorter(new AtsProgramViewerSorter());

      reload(atsPrograms);
   }

   public void reload(Collection<? extends IAtsProgramClient> lbaPrograms) {
      Collection<Object> objs = new ArrayList<Object>();
      for (IAtsProgramClient proj : atsPrograms) {
         objs.add(proj);
      }
      setInput(objs);
   }
}
