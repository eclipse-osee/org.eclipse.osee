/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.type.LinkType;

/**
 * This class is used as a Data Transfer Object for transferring options from the client to the server.
 *
 * @author Branden W. Phillips
 */
public class PublishingOptions {

   //boolean
   public boolean publishDiff = false;
   public boolean originPublishAsDiff = false;
   public boolean addMergeTag = false;
   public boolean includeUuids = false;
   public boolean updateParagraphNumbers = false;
   public boolean useParagraphNumbers = false;
   public boolean skipErrors = false;
   public boolean excludeFolders = false;
   public boolean useTemplateOnce = false;
   public boolean recurse = false;
   public boolean recurseOnLoad = false;
   public boolean useArtifactNames = false;
   public boolean allAttributes = false;
   public boolean maintainOrder = false;
   public boolean noDisplay = false;
   public boolean skipDialogs = false;
   public boolean firstTime = false;
   public boolean secondTime = false;
   public boolean templateOnly = false;
   public boolean inPublishMode = false;
   public boolean publishEmptyHeaders = false;

   //BranchId
   public BranchId branch = BranchId.SENTINEL;
   public BranchId compareBranch = BranchId.SENTINEL;
   public BranchId wasBranch = BranchId.SENTINEL;

   //ArtifactId
   public ArtifactId view = ArtifactId.SENTINEL;
   public ArtifactId templateArtifact = ArtifactId.SENTINEL;

   //Link Type
   public LinkType linkType = null;

   //ArtifactType
   public List<ArtifactTypeToken> excludeArtifactTypes = null;

   //String
   public String orcsQuery = null;
   public String overrideDataRights = null;
   public String attributeName = null;
   public String paragraphNumber = null;
   public String outlineType = null;
   public String resultPathReturn = null;
   public String openOption = null;
   public String executeVBScript = null;
   public String templateOption = null;
   public String previewRecurseValue = null;
   public String previewRecurseNoAttrValue = null;
   public String diffValue = null;
   public String diffNoAttrValue = null;
   public String threeWayMerge = null;
   public String id = null;
   public String name = null;
}
