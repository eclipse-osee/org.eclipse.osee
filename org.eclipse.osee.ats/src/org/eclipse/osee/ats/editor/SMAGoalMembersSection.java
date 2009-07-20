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
package org.eclipse.osee.ats.editor;

import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.IWorldEditor;
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.WorldComposite;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class SMAGoalMembersSection extends SectionPart implements IWorldEditor {

   private final SMAEditor editor;
   private WorldComposite worldComposite;

   public SMAGoalMembersSection(SMAEditor editor, Composite parent, XFormToolkit toolkit, int style) {
      super(parent, toolkit, style | Section.TITLE_BAR);
      this.editor = editor;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
    */
   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();

      Section section = getSection();
      section.setText("Members");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      final Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
      sectionBody.setLayout(ALayout.getZeroMarginLayout());
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      ToolBar toolBar = AtsUtil.createCommonToolBar(sectionBody, (XFormToolkit) toolkit);
      worldComposite = new WorldComposite(this, sectionBody, SWT.BORDER, toolBar);
      try {
         worldComposite.load("Members", editor.getSmaMgr().getSma().getRelatedArtifacts(AtsRelation.Goal_Member),
               (CustomizeData) null, TableLoadOption.None);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);

   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#refresh()
    */
   @Override
   public void refresh() {
      super.refresh();
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            if (worldComposite != null && !worldComposite.isDisposed()) {
               worldComposite.getXViewer().refresh();
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
    */
   @Override
   public void dispose() {
      if (worldComposite != null && !worldComposite.isDisposed()) {
         worldComposite.dispose();
      }
      super.dispose();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditor#createToolBarPulldown(org.eclipse.swt.widgets.Menu)
    */
   @Override
   public void createToolBarPulldown(Menu menu) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditor#getCurrentTitleLabel()
    */
   @Override
   public String getCurrentTitleLabel() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditor#getIActionable()
    */
   @Override
   public IActionable getIActionable() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditor#getWorldEditorProvider()
    */
   @Override
   public IWorldEditorProvider getWorldEditorProvider() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditor#reSearch()
    */
   @Override
   public void reSearch() throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditor#reflow()
    */
   @Override
   public void reflow() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditor#setTableTitle(java.lang.String, boolean)
    */
   @Override
   public void setTableTitle(String title, boolean warning) {
   }

}
