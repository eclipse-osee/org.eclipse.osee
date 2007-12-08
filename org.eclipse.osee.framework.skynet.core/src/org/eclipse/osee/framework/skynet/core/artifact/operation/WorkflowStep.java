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
package org.eclipse.osee.framework.skynet.core.artifact.operation;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Ryan D. Brooks
 */
public interface WorkflowStep {

   /**
    * &lt;step&gt; ::= &lt;artifact operation invocation&gt; | &lt;workflow invocation&gt; | &lt;exit&gt; | &lt;flow
    * control construct&gt;
    * 
    * @param artifacts
    * @param monitor TODO
    * @return list of artifacts that are the result of this step (if null) engine with replace with the empty list
    * @throws Exception TODO
    */
   public abstract List<Artifact> perform(List<Artifact> artifacts, IProgressMonitor monitor) throws IllegalArgumentException, Exception;

}
