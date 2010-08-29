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
package org.eclipse.osee.framework.core.dsl.integration;

import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactData;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;

/**
 * @author Roberto E. Escobar
 */
public interface RestrictionHandler<T> {
   public T asCastedObject(ObjectRestriction objectRestriction);

   public void process(ObjectRestriction objectRestriction, ArtifactData artifactData, AccessDetailCollector collector) throws OseeCoreException;
}