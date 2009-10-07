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
package org.eclipse.osee.ats.health;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsHealthCheck {

   /**
    * Check artifacts for problems. Log results in resultsMap indexed by test name as key (they will be organized by key
    * in report). Insert "Error: " at beginning of resultMap value if log item is an error. This will be highlighted.
    */
   public void check(Collection<Artifact> artifacts, HashCollection<String, String> resultsMap) throws OseeCoreException;

}
