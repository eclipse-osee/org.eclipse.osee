/*******************************************************************************
* Copyright (c) 2023 Boeing.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Boeing - initial API and implementation
*******************************************************************************/
package prometheus;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

/**
 * @author Muhammad M. Alam
 */
public class PrometheusMetrics {

   private static final String QUERY_PARAM_SEPERATOR = "&";
   private static final String UTF_8 = "UTF-8";
   private static Properties prop = new Properties();

   private static class LocalByteArray extends ThreadLocal<ByteArrayOutputStream> {
      @Override
      protected ByteArrayOutputStream initialValue() {
         return new ByteArrayOutputStream(1 << 20);
      }
   }

   public static void main(String[] args) throws Exception {
      try {
         String working_directory = System.getProperty("user.dir");
         String conf_directory = working_directory + File.separator + "config.properties";
         InputStream input = new FileInputStream(conf_directory);
         prop.load(input);
         System.out.println("url_for_rest_call_1: " + prop.getProperty("url_for_rest_call_1"));
         System.out.println("url_for_rest_call_2: " + prop.getProperty("url_for_rest_call_2"));
         System.out.println("url_for_rest_call_3: " + prop.getProperty("url_for_rest_call_3"));
         System.out.println("Sleep Time: " + Long.parseLong(prop.getProperty("sleep_time")));
      } catch (IOException ex) {
         System.out.println(ex.getMessage());
      }
      CollectorRegistry registry = CollectorRegistry.defaultRegistry;
      startMetricsPopulatorThread(registry);
      startHttpServer(registry);
      System.out.println("Started");
   }

   private static void startMetricsPopulatorThread(CollectorRegistry registry) {
      Gauge gauge_response_1 = guage_1(registry);
      Gauge gauge_response_2 = guage_2(registry);
      Gauge gauge_response_3 = guage_3(registry);
      Thread bgThread = new Thread(() -> {
         while (true) {
            try {
               URL urlForGetRequest_1 = new URL(prop.getProperty("url_for_rest_call_1"));
               Date time_before_request = new Date();
               myGetRequest(urlForGetRequest_1);
               Date time_after_request = new Date();
               long response_time = (time_after_request.getTime() - time_before_request.getTime());
               gauge_response_1.set(response_time);
               URL urlForGetRequest_2 = new URL(prop.getProperty("url_for_rest_call_2"));
               time_before_request = new Date();
               myGetRequest(urlForGetRequest_2);
               time_after_request = new Date();
               response_time = (time_after_request.getTime() - time_before_request.getTime());
               gauge_response_2.set(response_time);
               URL urlForGetRequest_3 = new URL(prop.getProperty("url_for_rest_call_3"));
               time_before_request = new Date();
               myGetRequest(urlForGetRequest_3);
               time_after_request = new Date();
               response_time = (time_after_request.getTime() - time_before_request.getTime());
               gauge_response_3.set(response_time);
               TimeUnit.MINUTES.sleep(Long.parseLong(prop.getProperty("sleep_time")));
            } catch (IOException e) {
               e.printStackTrace();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      });
      bgThread.start();
   }

   private static Gauge guage_1(CollectorRegistry registry) {
      return Gauge.build().namespace("java").name("Rest_Response_Time_1").help("Rest_Response_Time Help").register(
         registry);
   }

   private static Gauge guage_2(CollectorRegistry registry) {
      return Gauge.build().namespace("java").name("Rest_Response_Time_2").help("Rest_Response_Time Help").register(
         registry);
   }

   private static Gauge guage_3(CollectorRegistry registry) {
      return Gauge.build().namespace("java").name("Rest_Response_Time_3").help("Rest_Response_Time Help").register(
         registry);
   }

   private static void startHttpServer(CollectorRegistry registry) throws IOException {
      HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
      HTTPMetricHandler mHandler = new HTTPMetricHandler(registry);
      addContext(server, mHandler);
      server.setExecutor(null); // creates a default executor
      server.start();
   }

   private static void addContext(HttpServer server, HTTPMetricHandler mHandler) {
      server.createContext("/", mHandler);
      server.createContext("/metrics", mHandler);
      server.createContext("/healthy", mHandler);
   }

   static class HTTPMetricHandler implements HttpHandler {
      private final static String HEALTHY_RESPONSE = "Exporter is Healthy.";
      private final CollectorRegistry registry;
      private final LocalByteArray response = new LocalByteArray();

      HTTPMetricHandler(CollectorRegistry registry) {
         this.registry = registry;
      }

      @Override
      public void handle(HttpExchange exchange) throws IOException {
         String query = exchange.getRequestURI().getRawQuery();
         String contextPath = exchange.getHttpContext().getPath();
         ByteArrayOutputStream outPutStream = outputStream();
         writeToStream(query, contextPath, outPutStream);
         writeHeaders(exchange);
         gzipStream(exchange, outPutStream);
         exchange.close();
      }

      private void gzipStream(HttpExchange exchange, ByteArrayOutputStream outPutStream) throws IOException {
         final GZIPOutputStream os = new GZIPOutputStream(exchange.getResponseBody());

         try {
            outPutStream.writeTo(os);
         } finally {
            os.close();
         }
      }

      private void writeHeaders(HttpExchange exchange) throws IOException {
         exchange.getResponseHeaders().set("Content-Type", TextFormat.CONTENT_TYPE_004);
         exchange.getResponseHeaders().set("Content-Encoding", "gzip");
         exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
      }

      private void writeToStream(String query, String contextPath, ByteArrayOutputStream outPutStream)
         throws IOException {
         OutputStreamWriter osw = new OutputStreamWriter(outPutStream, Charset.forName(UTF_8));
         if ("/-/healthy".equals(contextPath)) {
            osw.write(HEALTHY_RESPONSE);
         } else {
            TextFormat.write004(osw, registry.filteredMetricFamilySamples(parseQuery(query)));
         }
         osw.close();
      }

      private ByteArrayOutputStream outputStream() {
         ByteArrayOutputStream response = this.response.get();
         response.reset();
         return response;
      }
   }

   private static Set<String> parseQuery(String query) throws IOException {
      Set<String> names = new HashSet<String>();
      if (query != null) {
         String[] pairs = query.split(QUERY_PARAM_SEPERATOR);
         for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx != -1 && URLDecoder.decode(pair.substring(0, idx), UTF_8).equals("name[]")) {
               names.add(URLDecoder.decode(pair.substring(idx + 1), UTF_8));
            }
         }
      }
      return names;
   }

   public static void myGetRequest(URL urlForGetRequest) throws IOException {
      HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
      connection.setRequestMethod("GET");
      String basicAuth = "Basic xxxxx"; //change to userID
      connection.setRequestProperty("Authorization", basicAuth);
      int responseCode = connection.getResponseCode();

      if (responseCode != HttpURLConnection.HTTP_OK) {
         System.out.println("GET NOT WORKED");
      }

   }
}