/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("serial")
public class BranchTokenSerializer extends StdScalarSerializer<@NonNull BranchToken> {

   public BranchTokenSerializer() {
      super(BranchToken.class);
   }

   @Override
   public void serialize(BranchToken branch, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonGenerationException {
      jgen.writeStartObject();
      jgen.writeStringField("id", branch.getIdString());
      jgen.writeStringField("name", branch.getName());
      jgen.writeStringField("viewId", branch.getViewId().getIdString());
      jgen.writeEndObject();
   }
}