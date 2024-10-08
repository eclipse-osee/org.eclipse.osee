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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Used by XWidgets that perform data binding using OSEE attributes
 *
 * @author Roberto E. Escobar
 */
public interface AttributeWidget extends ArtifactStoredWidget {

   /**
    * Set attributeType used as storage for this widget
    */
   void setAttributeType(Artifact artifact, AttributeTypeToken attributeTypeToken);

   /**
    * Get attributeType used for data binding for this widget
    */
   AttributeTypeToken getAttributeType();
}