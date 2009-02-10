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
package org.eclipse.osee.ats.world;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class WorldXWidgetActionPage extends AtsXWidgetActionFormPage {

   private final WorldEditor worldEditor;
   private WorldComposite worldComposite;

   /**
    * @return the worldComposite
    */
   public WorldComposite getWorldComposite() {
      return worldComposite;
   }

   /**
    * @param editor
    */
   public WorldXWidgetActionPage(WorldEditor worldEditor) {
      super(worldEditor, "org.eclipse.osee.ats.actionPage", "Actions");
      this.worldEditor = worldEditor;
   }

   @Override
   public Section createResultsSection(Composite body) {
      resultsSection = toolkit.createSection(body, Section.NO_TITLE);
      resultsSection.setText("Results");
      resultsSection.setLayoutData(new GridData(GridData.FILL_BOTH));

      resultsContainer = toolkit.createClientContainer(resultsSection, 1);
      worldComposite = new WorldComposite(worldEditor, resultsContainer, SWT.BORDER, toolBar);
      toolkit.adapt(worldComposite);
      return resultsSection;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormPage#createPartControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      super.createPartControl(parent);

      Result result = AtsPlugin.areOSEEServicesAvailable();
      if (result.isFalse()) {
         AWorkbench.popup("ERROR", "DB Connection Unavailable");
         return;
      }

      try {
         worldEditor.getWorldEditorProvider().run(worldEditor, SearchType.Search, false);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.AtsXWidgetActionFormPage#getDynamicWidgetLayoutListener()
    */
   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      if (worldEditor.getWorldEditorProvider() instanceof IWorldEditorParameterProvider) {
         return ((IWorldEditorParameterProvider) worldEditor.getWorldEditorProvider()).getDynamicWidgetLayoutListener();
      }
      return null;
   }

   public void reSearch() throws OseeCoreException {
      reSearch(false);
   }

   /*
    * Mainly for testing purposes
    */
   public void reSearch(boolean forcePend) throws OseeCoreException {
      worldEditor.getWorldEditorProvider().run(worldEditor, SearchType.ReSearch, forcePend);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.AtsXWidgetActionFormPage#getXWidgetsXml()
    */
   @Override
   public String getXWidgetsXml() throws OseeCoreException {
      if (worldEditor.getWorldEditorProvider() instanceof IWorldEditorParameterProvider) {
         return ((IWorldEditorParameterProvider) worldEditor.getWorldEditorProvider()).getParameterXWidgetXml();
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.AtsXWidgetActionFormPage#handleSearchButtonPressed()
    */
   @Override
   public void handleSearchButtonPressed() {
      try {
         reSearch();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
