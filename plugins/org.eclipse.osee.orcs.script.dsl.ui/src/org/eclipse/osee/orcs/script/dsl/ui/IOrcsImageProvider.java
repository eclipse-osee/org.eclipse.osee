/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.script.dsl.ui;

import org.eclipse.osee.framework.jdk.core.type.Identifiable;
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

   Image getArtifactTypeImage(Identifiable<Long> type);

   Image getAttributeTypeImage(Identifiable<Long> type);

   Image getRelationTypeImage(Identifiable<Long> type);

}