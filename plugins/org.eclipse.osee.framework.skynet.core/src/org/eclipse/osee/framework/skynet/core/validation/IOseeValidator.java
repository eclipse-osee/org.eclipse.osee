/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public interface IOseeValidator {

   public static final int SHORT = 10;
   public static final int MEDIUM = 50;
   public static final int LONG = 100;

   public int getQualityOfService();

   public boolean isApplicable(Artifact artifact, AttributeTypeToken attributeType);

   public IStatus validate(Artifact artifact, AttributeTypeToken attributeType, Object proposedObject);
}
