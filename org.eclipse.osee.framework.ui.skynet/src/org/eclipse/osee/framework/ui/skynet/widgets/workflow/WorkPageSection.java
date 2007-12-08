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
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class WorkPageSection extends SectionPart {

   public WorkPageSection(Composite parent, XFormToolkit toolkit, int style, WorkPage page) {
      super(parent, toolkit, style);
      createPage(parent, page, toolkit);
   }

   private Section createPage(Composite comp, WorkPage page, XFormToolkit toolkit) {
      Section section = toolkit.createSection(comp, Section.TWISTIE | Section.TITLE_BAR);
      section.setText(page.getName());
      section.setExpanded(true);
      section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite mainComp = toolkit.createClientContainer(section, 1);
      mainComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      createWorkArea(mainComp, page, toolkit);
      return section;
   }

   private void createWorkArea(Composite comp, WorkPage page, XFormToolkit toolkit) {
      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      page.createBody(toolkit, workComp, null, xModListener, true);
   }

   final XModifiedListener xModListener = new XModifiedListener() {
      public void widgetModified(XWidget widget) {
         System.out.println("Widget changed");
      }
   };

   @Override
   public void refresh() {
      super.refresh();
   }
}