/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.util.Set;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Morgan E. Cook
 */
public interface MSWordOperations {

   public WordUpdateChange updateWordArtifacts(WordUpdateData data);

   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data);

   public StreamingOutput publishWithNestedTemplates(BranchId branch, ArtifactId masterTemplate, ArtifactId slaveTemplate, ArtifactId headArtifact);

}
