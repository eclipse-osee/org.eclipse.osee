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

package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.BlamWorkflow;
import org.eclipse.osee.framework.ui.skynet.blam.WorkflowEditor;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemBlam extends XNavigateItem {
   private final BlamOperation blamOperation;

   /**
    * @param parent
    * @param name
    */
   public XNavigateItemBlam(XNavigateItem parent, BlamOperation blamOperation) {
      super(parent, blamOperation.getClass().getSimpleName());
      this.blamOperation = blamOperation;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws SQLException, OseeCoreException {
      BlamWorkflow workflow;
      try {
         workflow =
               (BlamWorkflow) ArtifactQuery.getArtifactFromTypeAndName(BlamWorkflow.ARTIFACT_NAME, getName(),
                     BranchPersistenceManager.getCommonBranch());
      } catch (Exception ex) {
         workflow = BlamWorkflow.createBlamWorkflow(blamOperation);
         workflow.setDescriptiveName(getName());
         workflow.persistAttributesAndRelations();
      }
      workflow.setSoleOperation(blamOperation);

      WorkflowEditor.editArtifact(workflow);
   }
}