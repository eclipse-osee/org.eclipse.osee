/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.action;

import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public interface IWorkItemPage {

   String getHtml(ArtifactReadable action, String title, ActionLoadLevel actionLoadLevel, IResourceRegistry registry) throws Exception;

   String getHtmlWithStates(ArtifactReadable action, String title, ActionLoadLevel actionLoadLevel, IResourceRegistry registry) throws Exception;

   ArtifactId createAction(String title, String description, String actionableItemName, String changeType, String priority, String asUserId) throws Exception;

}