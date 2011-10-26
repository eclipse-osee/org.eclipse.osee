/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorAction.IOpenNewAtsWorldEditorHandler;
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

public class OpenNewAtsWorldEditorActionTest extends AbstractAtsActionRunTest {

   @Override
   public OpenNewAtsWorldEditorAction createAction() {
      return new OpenNewAtsWorldEditorAction(new IOpenNewAtsWorldEditorHandler() {

         @Override
         public IWorldEditorProvider getWorldEditorProviderCopy() {
            return new IWorldEditorProvider() {

               @Override
               public void setTableLoadOptions(TableLoadOption... tableLoadOptions) {
                  // do nothing
               }

               @Override
               public void setCustomizeData(CustomizeData customizeData) {
                  // do nothing
               }

               @Override
               public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend) {
                  // do nothing
               }

               @Override
               public Artifact getTargetedVersionArtifact() {
                  return null;
               }

               @Override
               public String getSelectedName(SearchType searchType) {
                  return "Open";
               }

               @Override
               public String getName() {
                  return "Open";
               }

               @Override
               public IWorldEditorProvider copyProvider() {
                  return null;
               }
            };
         }

         @Override
         public CustomizeData getCustomizeDataCopy() {
            return null;
         }
      });
   }

}
