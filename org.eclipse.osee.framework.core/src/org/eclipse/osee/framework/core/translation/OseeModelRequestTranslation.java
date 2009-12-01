/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.translation;

import java.net.URI;
import org.eclipse.osee.framework.core.data.OseeModelRequest;
import org.eclipse.osee.framework.core.data.OseeModelRequest.RequestType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class OseeModelRequestTranslation implements ITranslator<OseeModelRequest> {

   private enum Fields {
      OPERATION,
      PERSIST,
      MODEL;
   }

   @Override
   public OseeModelRequest convert(PropertyStore store) throws OseeCoreException {
      RequestType requestType = RequestType.valueOf(store.get(Fields.OPERATION.name()));
      boolean isPersistAllowed = store.getBoolean(Fields.PERSIST.name());
      String model = store.get(Fields.MODEL.name());
      return new OseeModelRequest(requestType, model, isPersistAllowed);
   }

   @Override
   public PropertyStore convert(OseeModelRequest object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Fields.OPERATION.name(), object.getRequestType().name());
      store.put(Fields.PERSIST.name(), object.isPersistAllowed());
      store.put(Fields.MODEL.name(), object.getModel());

      //      URI uri = object.getModel();
      //      if (uri != null) {
      //         store.put(Fields.RESOURCE_NAME.name(), getName(uri));
      //
      //         InputStream inputStream = null;
      //         try {
      //            inputStream = new BufferedInputStream(uri.toURL().openStream());
      //
      //         } catch (MalformedURLException ex) {
      //            throw new OseeWrappedException(ex);
      //         } catch (IOException ex) {
      //            throw new OseeWrappedException(ex);
      //         } finally {
      //            Lib.close(inputStream);
      //         }
      //      }
      return store;
   }

   private String getName(URI uri) {
      String value = uri.toASCIIString();
      int index = value.lastIndexOf("/");
      if (index > 0) {
         value = value.substring(index, value.length());
      }
      return value;
   }
}
