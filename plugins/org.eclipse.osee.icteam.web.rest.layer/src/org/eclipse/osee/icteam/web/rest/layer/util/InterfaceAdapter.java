/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.rest.layer.util;

import java.lang.reflect.Type;

import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * 
 * @author Ajay Chandrahasan 
 *
 * Interface to Serialize and Deserialize Transferable Artifacts
 */

public class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
	

	
    public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
    	return context.serialize(object);
    }

    public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(elem, TransferableArtifact.class);
    }

	
}