/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.script.dsl.ui;

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public interface IOrcsImageProvider {

   Image getBranchImage();

   Image getTxImage();

   Image getArtifactImage();

   Image getAttributeImage();

   Image getRelationImage();

   Image getArtifactTypeImage(Id type);

   Image getAttributeTypeImage(Id type);

   Image getRelationTypeImage(Id type);

}