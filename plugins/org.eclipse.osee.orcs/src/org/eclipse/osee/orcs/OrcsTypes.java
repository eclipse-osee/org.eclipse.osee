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
package org.eclipse.osee.orcs;

import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTypes {

   void importOseeTypes(boolean isInitializing, OseeImportModelRequest request, OseeImportModelResponse response) throws OseeCoreException;

   void exportOseeTypes(OutputStream outputStream) throws OseeCoreException;

   Callable<?> purgeArtifactType(Collection<? extends IArtifactType> artifactTypes);

   Callable<?> purgeAttributeType(Collection<? extends IAttributeType> attributeTypes);

   Callable<?> purgeRelationType(Collection<? extends IRelationType> relationTypes);

}
