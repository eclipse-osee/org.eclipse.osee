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
package org.eclipse.osee.ats.ide.world;

import java.util.Collection;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorOperationProvider extends WorldEditorProvider implements IWorldEditorConsumer {

   private final WorldEditorOperation operation;

   public WorldEditorOperationProvider(WorldEditorOperation operation) {
      super(null, new TableLoadOption[] {TableLoadOption.None});
      this.operation = operation;
   }

   @Override
   public IWorldEditorProvider copyProvider() {
      return new WorldEditorOperationProvider(operation);
   }

   @Override
   public String getName() {
      return operation.getName();
   }

   @Override
   public WorldEditor getWorldEditor() {
      if (operation instanceof IWorldEditorConsumer) {
         return ((IWorldEditorConsumer) operation).getWorldEditor();
      }
      return null;
   }

   @Override
   public void setWorldEditor(WorldEditor worldEditor) {
      if (operation instanceof IWorldEditorConsumer) {
         ((IWorldEditorConsumer) operation).setWorldEditor(worldEditor);
      }
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      return operation.performSearch();
   }

}
