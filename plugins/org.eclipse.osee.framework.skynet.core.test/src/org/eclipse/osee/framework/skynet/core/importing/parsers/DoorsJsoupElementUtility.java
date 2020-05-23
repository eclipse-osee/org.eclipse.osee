/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.framework.skynet.core.importing.parsers;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public final class DoorsJsoupElementUtility {
   Elements elements;

   public DoorsJsoupElementUtility() {
      //@formatter:off
      String s =
         " <table border=\"\">\n" +
         " <tbody>\n" +
         " <tr bgcolor=\"#FFC0CB\">\n" +
         " <th width=\"72\" align=\"Right\">ID</th>\n" +
         " <th width=\"560\" align=\"Left\">Requirements</th>\n" +
         " <th width=\"71\" align=\"Left\">Object Number</th>\n" +
         " <th width=\"42\" align=\"Left\">Req?</th>\n" +
         " <th width=\"113\" align=\"Left\">Data Type</th>\n" +
         " <th width=\"51\" align=\"Left\">Parent ID</th>\n" +
         " <th width=\"108\" align=\"Left\">Effectivity</th>\n" +
         " <th width=\"98\" align=\"Left\">Paragraph Heading</th>\n" +
         " <th width=\"160\" align=\"Left\">Document Applicability</th>\n" +
         " <th width=\"214\" align=\"Left\">Verification Criteria (V-PerfSpec_Verification)</th>\n" +
         " <th width=\"71\" align=\"Left\">Change Status</th>\n" +
         " <th width=\"116\" align=\"Left\">Proposed Object Heading</th>\n" +
         " <th width=\"127\" align=\"Left\">Proposed Object Text</th>\n" +
         " <th width=\"88\" align=\"Left\">OSEE GUID</th>\n" +
         " <th width=\"160\" align=\"Left\">Subsystem</th>\n" +
         " <th nowrap=\"\"> Links </th>\n" +
         " </tr>\n" +
         " <tr>\n" +
         " <td>SysSpec-3</td>\n" +
         " <td><a name=\"X3\"> </a><b>1 SCOPE</b><br /></td>\n" +
         " <td>1</td>\n" +
         " <td>False</td>\n" +
         " <td>Heading</td>\n" +
         " <td><br /></td>\n" +
         " <td>Baseline</td>\n" +
         " <td>SCOPE</td>\n" +
         " <td></td>\n" +
         " <td><br /></td>\n" +
         " <td><br /></td>\n" +
         " <td><br /></td>\n" +
         " <td><br /></td>\n" +
         " <td>AvLzOiTXgEKsl4lQbLQA</td>\n" +
         " <td><br /></td>\n" +
         " <td nowrap=\"\" align=\"left\"><small>...</small></td>\n" +
         " </tr>\n" +
         " <tr>\n" +
         " <td>SysSpec-4</td>\n" +
         " <td><a name=\"X358\"> \n"+
         " </a><table border=\"1\" cellpadding=\"0\" cellspacing=\"0\">\n"+
         " <tbody><tr>\n"+
         " <td colspan=\"192\" valign=\"top\"><a name=\"X360\"> \n"+
         " </a>example<br>\n"+
         " </td>\n"+
         " <td colspan=\"384\" valign=\"top\"><a name=\"X361\"> \n"+
         " </a>some data<br>\n"+
         " </td>\n"+
         " </tr>\n"+
         " <tr>\n"+
         " <td colspan=\"192\" valign=\"top\"><a name=\"X363\"> \n"+
         " </a>further<br>\n"+
         " </td>\n"+
         " <td colspan=\"384\" valign=\"top\"><a name=\"X364\"> \n"+
         " </a>other data<br>\n"+
         " </td>\n"+
         " </tr>\n"+
         " <tr>\n"+
         " <td colspan=\"192\" valign=\"top\"><a name=\"X369\"> \n"+
         " </a>another<br>\n"+
         " </td>\n"+
         " <td colspan=\"384\" valign=\"top\"><a name=\"X370\"> \n"+
         " </a>MzLzOzTXgEKsz42QbLzB<br>\n"+
         " </td>\n"+
         " </tr>\n"+
         " </tbody></table>\n"+
         " </td>\n" +
         " <td>1.1</td>\n" +
         " <td>False</td>\n" +
         " <td>Heading</td>\n" +
         " <td><br /></td>\n" +
         " <td>MzLzOzTXgEKsz43QbLzB</td>\n" +
         " <td>ZZParagraph</td>\n" +
         " <td>ZZApplicability</td>\n" +
         " <td>Verification Method: Laboratory</td>\n" +
         " <td>MzLzOzTXgEKsz44QbLzB</td>\n" +
         " <td>MzLzOzTXgEKsz45QbLzB</td>\n" +
         " <td>MzLzOzTXgEKsz46QbLzB</td>\n" +
         " <td>MzLzOzTXgEKsz47QbLzB</td>\n" +
         " <td><br /></td>\n" +
         " <td nowrap=\"\" align=\"left\"><small>...</small></td>\n" +
         " </tr>\n" +
         " </tbody>\n" +
         " </table>\n" +
         "";
      //@formatter:on
      // this selection doesn't distinguish the table rows inside of table rows
      // these child rows appear at the end of the list

      elements = Jsoup.parse(s).select("tr");
   }

   public Elements getJsoupElements() {
      return elements;
   }
}
