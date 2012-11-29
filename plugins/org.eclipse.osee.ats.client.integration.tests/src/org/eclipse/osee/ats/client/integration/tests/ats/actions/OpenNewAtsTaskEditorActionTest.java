/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.client.integration.tests.ats.actions;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.jface.action.Action;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.actions.OpenNewAtsTaskEditorAction;
import org.eclipse.osee.ats.actions.OpenNewAtsTaskEditorAction.IOpenNewAtsTaskEditorHandler;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.task.ITaskEditorProvider;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsTaskEditorActionTest extends AbstractAtsActionRunTest {

   @Override
   public Action createAction() {
      return new OpenNewAtsTaskEditorAction(new IOpenNewAtsTaskEditorHandler() {

         @Override
         public ITaskEditorProvider getTaskEditorProviderCopy() {
            return new ITaskEditorProvider() {

               @Override
               public void setTableLoadOptions(TableLoadOption... tableLoadOptions) {
                  // do nothing
               }

               @Override
               public void setCustomizeData(CustomizeData customizeData) {
                  // do nothing
               }

               @Override
               public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException {
                  return Collections.singleton(AtsTestUtil.getOrCreateTaskOffTeamWf1());
               }

               @Override
               public String getTaskEditorLabel(SearchType searchType) {
                  return "Tasks";
               }

               @Override
               public Collection<TableLoadOption> getTableLoadOptions() {
                  return Collections.singleton(TableLoadOption.NoUI);
               }

               @Override
               public String getName() {
                  return "Tasks";
               }

               @Override
               public ITaskEditorProvider copyProvider() {
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
