package org.eclipse.osee.ote.rest.internal;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HTMLBuilder {
   
   private static String hostname = "";
   private static final String serverTitle = "OTE Server"; 
   
   private StringBuilder sb;
   
   static {
      try {
         InetAddress localHost = InetAddress.getLocalHost();
         hostname = localHost.getHostName();
      } catch (UnknownHostException e) {
         e.printStackTrace();
      }
   }
   
   public HTMLBuilder() {
      sb = new StringBuilder();
   }
   
   public void open(String title){
      sb.append("<!DOCTYPE HTML>\n");
      sb.append("<html>\n");
      sb.append("<head>\n");
      sb.append("<title>"+title+"</title>\n");
      sb.append("</head>\n");
      sb.append("<body>\n");
   }
   
   public void commonHeader(File directory){
      h2(serverTitle + " on " + hostname);
      h3("Local Folder:");
      p(directory.getAbsolutePath());
   }
   
   private void h3(String string) {
      sb.append("<h3>");
      sb.append(string);
      sb.append("</h3>");
   }

   public void addLink(String baseUrl, String path, String title){
      sb.append("<a href=\"");
      sb.append(baseUrl);
      if(!baseUrl.endsWith("/")){
         sb.append("/");
      }
      sb.append(path);
      sb.append("\">");
      sb.append(title);
      sb.append("</a>");
   }
   
   public void close(){
      sb.append("</body>\n");
      sb.append("</html>\n");
   }
   
   public String get(){
      return sb.toString();
   }

   public void br() {
      sb.append("<br>");
   }

   public void pre(String statusFileContents) {
      sb.append("<pre>");
      sb.append(statusFileContents);
      sb.append("</pre>");
   }

   public void p(String content) {
      sb.append("<p>");
      sb.append(content);
      sb.append("</p>");
   }

   public void h2(String title) {
      sb.append("<h2>");
      sb.append(title);
      sb.append("</h2>");
   }

   public void ulStart() {
      sb.append("<ul>");
   }

   public void ulStop() {
      sb.append("</ul>");
   }

   public void li(String name) {
      sb.append("<li>");
      sb.append(name);
      sb.append("</li>");
   }

   public void tableStart() {
      sb.append("<table border=\"1\">");
   }

   public void trStart() {
      sb.append("<tr>");
   }

   public void td(String string) {
      sb.append("<td>");
      sb.append(string);
      sb.append("</td>");
   }

   public void trEnd() {
      sb.append("</tr>");
   }

   public void tableEnd() {
      sb.append("</table>");
   }
   
}
