/*
 * Created on Jan 12, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.health.change;

/**
 * @author Jeff C. Phillips
 */
public class ChangeComparerTest {

   public static void main(String[] args) {
      String content = "<artId>12535</artId>";
      System.out.println(Integer.parseInt(content.substring(content.indexOf("<artId>") + 7, content.indexOf("</artId>"))));
   }

}
