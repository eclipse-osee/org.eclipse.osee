/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import org.eclipse.osee.framework.core.data.MaterialColors;
import org.eclipse.osee.framework.core.data.MaterialIcon;
import org.eclipse.osee.framework.core.data.MaterialShades;
import org.eclipse.osee.framework.core.data.OperationTypeToken;

/**
 * This type defines the types of operations that can occur on an artifact, attribute or relation.
 *
 * @author Jaden W. Puckett
 */
public interface CoreOperationTypes {

   //@formatter:off

   /*
    * Base
    */
   OperationTypeToken CreateChildArtifact = osee.add(6996644113326307731L,"Create Child Artifact", "Create child artifact for selected artifact",
      new MaterialIcon("add", MaterialColors.GREEN, MaterialShades.S500));
   OperationTypeToken DeleteArtifact = osee.add(9075821926072512558L, "Delete Artifact", "Delete selected artifact",
      new MaterialIcon("delete", MaterialColors.RED, MaterialShades.S500));

   /*
    * Publishing
    */
   OperationTypeToken PublishMarkdownAsHtmlWithSpecifiedTemplate =
      osee.add(8972650019222132280L, "Publish Markdown as HTML", "Publish Markdown as HTML with specified template",
         new MaterialIcon("description", MaterialColors.BLUE, MaterialShades.S700));

   //@formatter:on
}
