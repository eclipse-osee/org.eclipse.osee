/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AccessData;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.lifecycle.LifecycleHandler;

public interface IAccessProvider extends LifecycleHandler {

   void computeAccess(IBasicArtifact<?> userArtifact, Collection<?> objToCheck, AccessData accessData) throws OseeCoreException;

}
