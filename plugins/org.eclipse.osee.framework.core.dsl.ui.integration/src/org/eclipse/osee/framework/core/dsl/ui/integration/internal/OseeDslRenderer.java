/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AccessControlModel;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.OseeTypeDefinition;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeDslSegmentParser;
import org.eclipse.osee.framework.core.dsl.ui.integration.AbstractDslRenderer;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.FileToAttributeUpdateOperation;

/**
 * @author Roberto E. Escobar
 */
public final class OseeDslRenderer extends AbstractDslRenderer {
   private static final IArtifactType[] MATCHING_ARTIFACT_TYPES = {AccessControlModel, OseeTypeDefinition};

   private static final OseeDslSegmentParser parser = new OseeDslSegmentParser();

   public OseeDslRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
   }

   public OseeDslRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   @Override
   public String getName() {
      return "OseeDsl Editor";
   }

   @Override
   public DefaultArtifactRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new OseeDslRenderer(rendererOptions);
   }

   @Override
   public DefaultArtifactRenderer newInstance() {
      return new OseeDslRenderer();
   }

   @Override
   public String getAssociatedExtension(Artifact artifact) {
      return "osee";
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {
      Artifact artifact = artifacts.iterator().next();

      String data;
      if (artifact.isOfType(CoreArtifactTypes.OseeTypeDefinition)) {
         data = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.UriGeneralStringData, "");
      } else {
         StringBuilder builder = new StringBuilder();
         builder.append(parser.getStartTag(artifact, artifact.getBranchToken()));
         builder.append("\n");
         builder.append(artifact.getSoleAttributeValueAsString(CoreAttributeTypes.GeneralStringData, ""));
         builder.append("\n");
         builder.append(parser.getEndTag(artifact, artifact.getBranchToken()));
         data = builder.toString();
      }

      try {
         return new ByteArrayInputStream(data.getBytes("UTF-8"));
      } catch (UnsupportedEncodingException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      IOperation op;
      Artifact artifact = artifacts.iterator().next();
      if (artifact.isOfType(CoreArtifactTypes.OseeTypeDefinition)) {
         OseeTypeModifier modifier = new OseeTypeModifier();
         op = new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.UriGeneralStringData,
            modifier);
      } else {
         op = new OseeDslArtifactUpdateOperation(parser, file);
      }
      return op;
   }

   @Override
   public int minimumRanking() {
      return GENERAL_MATCH;
   }

   @Override
   protected IArtifactType[] getArtifactTypeMatches() {
      return MATCHING_ARTIFACT_TYPES;
   }
}
