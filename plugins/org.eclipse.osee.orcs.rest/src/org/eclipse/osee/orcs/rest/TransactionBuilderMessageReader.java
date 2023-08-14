/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.orcs.rest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.transaction.TransactionBuilderDataFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * Deserializes JSON into TransactionBuilder for REST calls. This provider is programmatically registered via
 * OrcsApplication.start()
 *
 * @author Ryan D. Brooks
 */
public class TransactionBuilderMessageReader implements MessageBodyReader<TransactionBuilder> {
   private final OrcsApi orcsApi;

   public TransactionBuilderMessageReader(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == TransactionBuilder.class;
   }

   @Override
   public TransactionBuilder readFrom(Class<TransactionBuilder> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
      throws IOException, WebApplicationException {
      TransactionBuilderDataFactory txBdf = new TransactionBuilderDataFactory(orcsApi);
      TransactionBuilder tx = txBdf.loadFromJson(Lib.inputStreamToString(entityStream));
      return tx;
   }

}