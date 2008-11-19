/*
 * Created on Nov 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
         OSEELog.logException(AtsPlugin.class, ex, true);
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
      worldEditor.getWorldEditorProvider().run(worldEditor, SearchType.ReSearch, false);
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
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

}
