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
package simulateRestCalls;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * @author Muhammad M. Alam
 */
public class RestCalls {

   public static void main(String[] args) throws Exception {
      System.out.println("REST Calls Started");
      try {
         Date d1 = new Date();
         System.out.println("Start Time = " + d1);
         myGetRequest();
         Date d2 = new Date();
         System.out.println("End Time = " + d2);
         long milli_seconds = (d2.getTime() - d1.getTime());
         System.out.println("Total time to complete the REST calls: " + milli_seconds);
      } catch (IOException e) {
         e.printStackTrace();
      }
      System.out.println("REST Calls Completed");
   }

   public static void myGetRequest() throws IOException {
      BufferedReader buffer = null;
      int no_of_rest_calls = 0;
      try {
         String filePath = "C:\\REST_Calls.txt";
         System.out.println(filePath);
         buffer = new BufferedReader(new FileReader(filePath));
         String requestStr;
         while ((requestStr = buffer.readLine()) != null) {
            URL urlForGetRequest = new URL(requestStr);
            String readLine = null;
            HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
            connection.setRequestMethod("GET");
            String basicAuth = "Basic xxxxxx"; //change it to userId
            connection.setRequestProperty("Authorization", basicAuth);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
               no_of_rest_calls++;
               System.out.println("Request Number = " + no_of_rest_calls);
            } else {
               System.out.println("GET Request failed for: " + requestStr);
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         buffer.close();
      }
      System.out.println("Total number of Rest calls = " + no_of_rest_calls);
   }
}