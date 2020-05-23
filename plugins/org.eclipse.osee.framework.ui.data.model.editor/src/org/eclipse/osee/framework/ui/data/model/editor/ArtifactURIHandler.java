/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.data.model.editor;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GraphitiDiagram;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Level;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.StringOutputStream;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactURIHandler extends URIHandlerImpl {
   private final String txComment;

   public ArtifactURIHandler(String txComment) {
      this.txComment = txComment;
   }

   @Override
   public boolean canHandle(URI uri) {
      return "osee".equals(uri.scheme());
   }

   @Override
   public InputStream createInputStream(URI uri, Map<?, ?> options) {
      return createStream(uri, this::createInputStream);
   }

   private InputStream createInputStream(Artifact artifact, Long attributeId) {
      try {
         Object value = artifact.getAttributeById(attributeId, false).getValue();
         return Lib.stringToInputStream(value.toString());
      } catch (UnsupportedEncodingException ex) {
         OseeLog.log(ArtifactURIHandler.class, Level.SEVERE, ex);
         throw OseeCoreException.wrap(ex);
      }
   }

   private <T> T createStream(URI uri, BiFunction<Artifact, Long, T> function) {
      String[] segments = uri.segments();
      BranchId branch = BranchId.valueOf(segments[0]);
      Artifact artifact = ArtifactQuery.getArtifactFromId(Long.parseLong(segments[2]), branch);
      Long attributeId = Long.parseLong(segments[4]);
      return function.apply(artifact, attributeId);
   }

   @Override
   public boolean exists(URI uri, Map<?, ?> options) {
      return true;
   }

   @Override
   public OutputStream createOutputStream(URI uri, Map<?, ?> options) {
      return createStream(uri, this::createOutputStream);
   }

   private OutputStream createOutputStream(Artifact artifact, Long attributeId) {
      return new StringOutputStream(value -> {
         artifact.setSoleAttributeFromString(GraphitiDiagram, value);
         artifact.persist(txComment);
      });
   }

   @Override
   public Map<String, ?> getAttributes(URI uri, Map<?, ?> options) {
      Map<String, Object> result = new HashMap<>();
      Set<String> requestedAttributes = getRequestedAttributes(options);
      if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_READ_ONLY)) {
         result.put(URIConverter.ATTRIBUTE_READ_ONLY, false);
      }

      return result;
   }
}