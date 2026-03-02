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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewerWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XAtsProgramComboWidget extends XComboViewerWidget {

   public static WidgetId ID = WidgetIdAts.XAtsProgramComboWidget;
   protected Collection<IAtsProgram> atsPrograms = new ArrayList<>();

   public XAtsProgramComboWidget(String displayLabel) {
      this(ID, displayLabel);
   }

   public XAtsProgramComboWidget(WidgetId widgetId, String displayLabel) {
      super(widgetId, displayLabel, SWT.READ_ONLY);
      setLabelProvider(new AtsProgramLabelProvider());
      setContentProvider(new ArrayContentProvider());
      setComparator(new StringNameComparator());
   }

   public XAtsProgramComboWidget() {
      this(ID, "ATS Program");
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      setComparator(new AtsProgramViewerSorter());
      reload();
   }

   public void reload() {
      Collection<Object> objs = new ArrayList<>();
      for (IAtsProgram proj : getPrograms()) {
         objs.add(proj);
      }
      setInput(objs);
   }

   protected Collection<IAtsProgram> getPrograms() {
      if (atsPrograms == null) {
         atsPrograms = AtsApiService.get().getProgramService().getPrograms();
      }
      return atsPrograms;
   }
}
