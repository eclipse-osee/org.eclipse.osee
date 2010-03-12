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
package org.eclipse.osee.framework.skynet.core.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public interface IOseeValidator {

   public static final int SHORT = 10;
   public static final int MEDIUM = 50;
   public static final int LONG = 100;

   public int getQualityOfService();

   public boolean isApplicable(Artifact artifact, IAttributeType attributeType) throws OseeCoreException;

   public IStatus validate(Artifact artifact, AttributeType attributeType, Object proposedObject) throws OseeCoreException;
}
