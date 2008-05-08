/*
 * Created on Apr 8, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.servlet.data;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;

/**
 * This class is responsible for parsing servlet resquests into object[]
 * http://localhost:8089/resource?param1=blah&param2=blah
 * 
 * @author Roberto E. Escobar
 */
public class HttpRequestDecoder {

   private static final String URI = "uri";
   private static final String PROTOCOL = "protocol";
   private static final String SEED = "seed";
   private static final String NAME = "name";
   private static final String EXTENSION = "extension";

   // Whether data should be compressed
   private static final String COMPRESS_ON_ACQUIRE = "compress.before.sending";
   private static final String COMPRESS_ON_SAVE = "compress.before.saving";

   // Whether data should be decompressed
   private static final String DECOMPRESS_ON_ACQUIRE = "decompress.before.sending";

   // Whether data has already been compressed
   private static final String IS_COMPRESSED = "is.compressed";

   private static final String IS_OVERWRITE_ALLOWED = "is.overwrite.allowed";

   private HttpRequestDecoder() {
   }

   public static String[] fromPutRequest(HttpServletRequest request) {
      List<String> toReturn = new ArrayList<String>();
      toReturn.add(request.getParameter(PROTOCOL));
      toReturn.add(request.getParameter(SEED));
      StringBuilder builder = new StringBuilder();
      builder.append(request.getParameter(NAME));
      String extension = request.getParameter(EXTENSION);
      if (extension != null && extension.length() > 0) {
         builder.append(".");
         builder.append(extension);
      }
      toReturn.add(builder.toString());
      return toReturn.toArray(new String[toReturn.size()]);
   }

   public static String fromGetRequest(HttpServletRequest request) {
      return request.getParameter(URI);
   }

   public static Options getOptions(HttpServletRequest request) {
      Options options = new Options();
      options.put(StandardOptions.CompressOnSave.name(), request.getParameter(COMPRESS_ON_SAVE));
      options.put(StandardOptions.CompressOnAcquire.name(), request.getParameter(COMPRESS_ON_ACQUIRE));
      options.put(StandardOptions.DecompressOnAquire.name(), request.getParameter(DECOMPRESS_ON_ACQUIRE));
      options.put(StandardOptions.Overwrite.name(), request.getParameter(IS_OVERWRITE_ALLOWED));
      return options;
   }

   protected static boolean isDataCompressed(HttpServletRequest request) {
      return new Boolean(request.getParameter(IS_COMPRESSED));
   }

   public static String fromDeleteRequest(HttpServletRequest request) {
      return request.getParameter(URI);
   }

}
