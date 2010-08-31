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

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IWorkDefinitionProvider {

   /**
    * Return WorkItemDefinitions to be contributed programatically to the WorkDefinitionFactory. This includes Page and
    * Workflow Definitions. This should only be used for development as all WorkItemDefinitions should be imported into
    * the DB using ImportWorkItemDefinitions.
    */
   public Collection<WorkItemDefinition> getProgramaticWorkItemDefinitions() throws OseeCoreException;

   /**
    * Return WorkFlowDefinition to use for the given state machine artifact.
    */
   public WorkFlowDefinition getWorkFlowDefinition(Artifact artifact) throws OseeCoreException;

}
