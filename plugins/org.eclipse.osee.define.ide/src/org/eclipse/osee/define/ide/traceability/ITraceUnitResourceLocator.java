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

package org.eclipse.osee.define.ide.traceability;

import java.nio.CharBuffer;
import org.eclipse.osee.define.ide.utility.IResourceLocator;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Roberto E. Escobar
 */
public interface ITraceUnitResourceLocator extends IResourceLocator {

   public String UNIT_TYPE_UNKNOWN = "Unknown";

   public ArtifactTypeToken getTraceUnitType(String name, CharBuffer fileBuffer);

}
