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

import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorOperationProvider extends WorldEditorProvider implements IWorldEditorConsumer {

   private final AbstractOperation operation;

   public WorldEditorOperationProvider(AbstractOperation operation) {
      super(null, new TableLoadOption[] {TableLoadOption.None});
      this.operation = operation;
   }

   @Override
   public IWorldEditorProvider copyProvider() {
      return new WorldEditorOperationProvider(operation);
   }

   @Override
   public void run(WorldEditor worldEditor, SearchType searchtype, boolean forcePend) throws OseeCoreException {
      // WorldEditor is provided to the operation independently
      // Don't need search type cause operation should already handle if it wants to search or re-search
      if (forcePend) {
         Operations.executeAndPend(operation, true);
      } else {
         Operations.executeAsJob(operation, true);
      }
   }

   @Override
   public String getName() throws OseeCoreException {
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

}
