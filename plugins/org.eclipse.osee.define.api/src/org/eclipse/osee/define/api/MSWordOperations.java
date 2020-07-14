/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.define.api;

import java.util.Set;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Morgan E. Cook
 */
public interface MSWordOperations {

   public WordUpdateChange updateWordArtifacts(WordUpdateData data);

   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data);

   public String renderWordTemplateContentWithApplicability(BranchId branchId, ArtifactId viewId, String data);

   public Response msWordTemplatePublish(BranchId branch, ArtifactId template, ArtifactId headArtifact, String userEmail);

   public Response goalWorkflowPublish(ArtifactId template, ArtifactId goal);

   public String renderPlainText(BranchId branchId, String data);

}
