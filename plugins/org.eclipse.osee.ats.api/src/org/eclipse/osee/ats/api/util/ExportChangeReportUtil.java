/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.api.util;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Dominic A. Guss
 */
public class ExportChangeReportUtil {

   public static final ArtifactTypeToken[] ARTIFACT_ALLOW_TYPES = {
      CoreArtifactTypes.AbstractSoftwareRequirement,
      CoreArtifactTypes.InterfaceRequirementMsWord,
      CoreArtifactTypes.HeadingMsWord};
}
