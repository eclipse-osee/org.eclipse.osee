/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public interface IWorkPackageUtility extends IAtsColumn {

   IAtsWorkPackage getWorkPackage(IAtsObject atsObject);

   ArtifactId getWorkPackageId(Object object);

   String getColumnText(IAtsWorkPackage workPackage);

}