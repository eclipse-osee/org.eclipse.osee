/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
