/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.actions;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsWorldEditorAction;
import org.eclipse.osee.ats.ide.actions.OpenNewAtsWorldEditorAction.IOpenNewAtsWorldEditorHandler;
import org.eclipse.osee.ats.ide.world.IWorldEditorProvider;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
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
               public IAtsVersion getTargetedVersionArtifact() {
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

               @Override
               public Collection<Artifact> performSearch(SearchType searchType) {
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
