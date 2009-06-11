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
package org.eclipse.osee.ats.hyper;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public interface IHyperArtifact {

   public boolean isDeleted();

   public String getHyperName();

   public String getHyperType();

   public String getHyperState();

   public String getGuid();

   public String getHyperAssignee();

   public String getHyperTargetVersion();

   public Image getHyperAssigneeImage() throws Exception;

   public Artifact getHyperArtifact();

}
