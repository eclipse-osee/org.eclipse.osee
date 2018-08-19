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
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeData<T> extends OrcsData<AttributeTypeToken>, AttributeId {

   ArtifactId getArtifactId();

   void setArtifactId(ArtifactId artifactId);

   DataProxy<T> getDataProxy();

   void setDataProxy(DataProxy<T> proxy);

}