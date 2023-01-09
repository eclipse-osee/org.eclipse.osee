/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.render.MSWordTemplateClientRenderer;
import org.eclipse.osee.framework.ui.skynet.render.artifactElement.WordExtractorData;
import org.eclipse.osee.framework.ui.skynet.render.artifactElement.WordImageArtifactElementExtractor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Test case for {@link WordImageArtifactElementExtractor}.
 *
 * @author Jeff C. Phillips
 */
public class WordArtifactElementExtractorTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final String EQUATION_TEST = "support/equation.xml";
   private static final String PERFORMANCE_TEST = "support/performance.xml";
   private static final String WORDML_START =
      "<w:wordDocument xmlns:w=\"http://schemas.microsoft.com/office/word/2003/wordml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w10=\"urn:schemas-microsoft-com:office:word\" xmlns:sl=\"http://schemas.microsoft.com/schemaLibrary/2003/core\" xmlns:aml=\"http://schemas.microsoft.com/aml/2001/core\" xmlns:wx=\"http://schemas.microsoft.com/office/word/2003/auxHint\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:dt=\"uuid:C2F41010-65B3-11d1-A29F-00AA00C14882\" xmlns:wsp=\"http://schemas.microsoft.com/office/word/2003/wordml/sp2\"  xmlns:ns0=\"http:/ \" xmlns:ns1=\"http://www.w3.org/2001/XMLSchema\" w:macrosPresent=\"no\" w:embeddedObjPresent=\"no\" w:ocxPresent=\"no\" xml:space=\"preserve\">";
   private static final String WORDML_END = "</w:wordDocument>";
   private static final String NO_CHANGE = "<w:body></w:body>";
   private static final String START_2007 = "<w:body><w:p>";
   private static final String SIMPLE_2007_CHANGE = "</w:p><w:p><w:r><w:t>Middle change</w:t></w:r></w:p><w:p>";
   private static final String START_2007_CHANGE = "<w:r><w:t>I am change</w:t></w:r></w:p><w:p>";
   private static final String END_2007_CHANGE = "</w:p><w:p><w:r><w:t>End change</w:t></w:r>";
   private static final String END_2007 =
      "</w:p><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"720\" w:footer=\"720\" w:gutter=\"0\"/><w:cols w:space=\"720\"/><w:docGrid w:line-pitch=\"360\"/><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"/><w:p wsp:rsidR=\"00B17B94\" wsp:rsidRDefault=\"00B17B94\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00A05BEC\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/><w:docGrid w:line-pitch=\"360\"/></w:body>";
   private static final String START_2003 = "<w:body><wx:sect><w:p>";
   private static final String END_2003 =
      "</w:p><w:p wsp:rsidR=\"00235CEA\" wsp:rsidRDefault=\"00235CEA\"/><w:p wsp:rsidR=\"00235CEA\" wsp:rsidRDefault=\"00235CEA\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00E00A43\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p><w:sectPr wsp:rsidR=\"00235CEA\" wsp:rsidSect=\"007F3E1C\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1440\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/></w:sectPr></wx:sect></w:body>";
   private static final String MULULTI_2003_EDIT_MULTI_SECTION =
      "<w:body><wx:sect><wx:sub-section><wx:sub-section><wx:sub-section><w:p wsp:rsidR=\"008730C8\" wsp:rsidRDefault=\"006264BE\"><w:pPr><w:pStyle w:val=\"Heading3\"/><w:listPr><wx:t wx:val=\"3.2.1  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Communication Subsystem Crew Interface</w:t></w:r></w:p><w:p wsp:rsidR=\"008730C8\" wsp:rsidRDefault=\"008730C8\"><w:r><w:pict><v:shapetype id=\"_x0000_t75\" coordsize=\"21600,21600\" o:spt=\"75\" o:preferrelative=\"t\" path=\"m@4@5l@4@11@9@11@9@5xe\" filled=\"f\" stroked=\"f\"><v:stroke joinstyle=\"miter\"/><v:formulas><v:f eqn=\"if lineDrawn pixelLineWidth 0\"/><v:f eqn=\"sum @0 1 0\"/><v:f eqn=\"sum 0 0 @1\"/><v:f eqn=\"prod @2 1 2\"/><v:f eqn=\"prod @3 21600 pixelWidth\"/><v:f eqn=\"prod @3 21600 pixelHeight\"/><v:f eqn=\"sum @0 0 1\"/><v:f eqn=\"prod @6 1 2\"/><v:f eqn=\"prod @7 21600 pixelWidth\"/><v:f eqn=\"sum @8 21600 0\"/><v:f eqn=\"prod @7 21600 pixelHeight\"/><v:f eqn=\"sum @10 21600 0\"/></v:formulas><v:path o:extrusionok=\"f\" gradientshapeok=\"t\" o:connecttype=\"rect\"/><o:lock v:ext=\"edit\" aspectratio=\"t\"/></v:shapetype><w:binData w:name=\"wordml://02000001.jpg\">/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUAEcDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDuI/HV3L4l1XTJvEnhmwtrKK1aC5uYSRd+bEHZkzcKNoPTBbhhz3Ol478fWvhjR9TFhcW0us2awt9nmRmQeY4AViMDcU3sF3bsKWxgGix0bxLpfi3Xtahs9JnTVltSYnv5IzC0UW1hkQncMk4PHAHHOBleLPh/rWrz+Kv7Nu9PWHXls2b7TvDRtAcbPlBGCAG3c9Nu3ncPfhHBSxEPaWUEovdav3OZOyv/ADPXe2ljJ81nY0ta8fjw7N4nluxDdw6V9mWC2tYpRKryoSBM5GxVJxhlzgcHLEA9pBMtxBHMgcJIodRIhRgCM8qwBB9iARXAa98P9Q1l/G4W7tok11bI2pO4lWgAyHGOASAMjOAc44wen/4S/wAPwfub/XtGtryP5Z4P7QjPlyDhlycHg5HIB9hXHiKNGdOH1dXlpe3+GHS383N+vQpN31ObtPGOsap4k1fSrW40azvrG+8iHS7+OVJbqEYPmLKG/iUOwxG2BtzkHJ2bfxxp0/iHW9KeG5hGkrF5k0kD4kdyRtUbckk7Ao6yFjtBAycbxb4O1jxRJIj2+jRTx3Mb2OswySxXdrErBsbAp3sMvj94qkkHCkVY1Hwjrp1Xxbd6RqcNpJrltbiG4ywkt5Yl2bcAH5WXPzghlJ4U4zXTKGCqRV2otpddneN3dJ3VuZ6rmVnvoT7yNh/G3h+LTdQv5r14YdOZEu1mtpY5YS+Nm6NlD4bcMHGDz6GoLTxvY33i+Hw9BZ6gsslm90ZZ7OWELhgoG10Bwfm+Y4XIABJOBx0vww1N9D8WWNpBpOnJqy2KWlvDcSSRwiAjducxgktjOcEkk59a7WXw/dj4jW/iSGSF7ZtMbT54nYq6fvPMV1wCGyeCDtx1yelZ1KGAgpcsm3Z21W/LFrZd3Jb20GnNnR0UUV45oFFFFABRRRQAUUUUAFFFFABRRRQB/9k=</w:binData><v:shape id=\"_x0000_i1025\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\"><v:imagedata src=\"wordml://02000001.jpg\" o:title=\"AAABDQ12FQYBv4zVUFX22w\"/></v:shape></w:pict></w:r></w:p><w:p wsp:rsidR=\"0067637D\" wsp:rsidRDefault=\"006264BE\"><w:r><w:t>One</w:t></w:r></w:p><w:p wsp:rsidR=\"008730C8\" wsp:rsidRDefault=\"008730C8\"><w:r><w:pict><w:binData w:name=\"wordml://02000002.jpg\">/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUAEcDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD1jUPE2oR+NF8NabpdtcTf2f8Ab2mubxoVC+YY9uFjck5wf886s2t6fYIE1XUNPsrpIFnmie6UCNSQu7LbSV3naGIGTjoeK5HxB4emufiUmsXXhn+29KGji1CYt32zecWztldei55H9761V17wjfal4hvL210VI7U+EpbCzjYxAwXLFgsYAbCkIxXI+XBIzivYWGwk1BOSj7t3qt+2r0+5fMzvJXO11/XrTQNMubmaSFrmO2muILV5gj3HlIXYLnk8DkgHGc1Pouo/2xoOnan5Xk/bLaO48vdu2b1DYzgZxnrivMdQ8E681ra7dJhvDJ4QXSHiedB5FyhVwWzweR8pXPzKMlR8w9H8MWc+neE9GsbqPy7m2sYIZUyDtdY1BGRweQelY4rD4elh04TUpX79PS44tt6mUnifVb3xVrWiaZpFlL/ZXkeZNc37Rb/NTeMKsT9MEdfSp/GHi+08J6Je3mIbq8tokm+w/aAkjRtKse/oSFy3XGMjFc3J4cdPH/iXU9T8Hf21Z332X7HJttZNuyLa/EsilcnHbnb9KyvG/gnXtSuvGH2HSYb4av8AYJrSUzovktCNjgbuQ+CfQbWb5s/KeylhcFOvTUpJRtBvXdvk5k3zabyb0VrNIlykkzstT8c2OhTa62rrDBaaZ5IieO6SWW5eRC2zyh8yNxxu6jLcAEjpoJ4rmCOeCVJYZVDxyRsGV1IyCCOCCO9eZeJfBeuas/xCW2tkA1ZbBrFnlUCYwgF165U5GBuwMkc45r02CRpYI5HheF3UM0UhBZCR907SRkdOCR7muHF0sPClCVJ+87X1/uQe3q5J+a6bFRbvqSUUUV55YUUUUAFZV54Y8P6jdPdX2h6Zc3MmN809pG7tgYGSRk8AD8KKKuFScHeDt6Ba5owQRW0EcEESRQxKEjjjUKqKBgAAcAAdqkooqG76sAooooAKKKKAP//Z</w:binData><v:shape id=\"_x0000_i1026\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\"><v:imagedata src=\"wordml://02000002.jpg\" o:title=\"AAABDQ12FQYBv4zVUFX22w\"/></v:shape></w:pict></w:r></w:p><w:p wsp:rsidR=\"008730C8\" wsp:rsidRDefault=\"008730C8\"/><w:p wsp:rsidR=\"008730C8\" wsp:rsidRDefault=\"008730C8\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"006264BE\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p></wx:sub-section><wx:sub-section><w:p wsp:rsidR=\"008730C8\" wsp:rsidRDefault=\"006264BE\"><w:pPr><w:pStyle w:val=\"Heading3\"/><w:listPr><wx:t wx:val=\"3.2.2  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Navigation Subsystem Crew Interface</w:t></w:r></w:p><w:p wsp:rsidR=\"008730C8\" wsp:rsidRDefault=\"008730C8\"><w:r><w:pict><v:shape id=\"_x0000_i1027\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\"><v:imagedata src=\"wordml://02000001.jpg\" o:title=\"AAABD2KacGEBi7yYThPBnA\"/></v:shape></w:pict></w:r></w:p><w:p wsp:rsidR=\"00C52CB7\" wsp:rsidRDefault=\"006264BE\"><w:r><w:t>Two</w:t></w:r></w:p><w:p wsp:rsidR=\"008730C8\" wsp:rsidRDefault=\"008730C8\"><w:r><w:pict><v:shape id=\"_x0000_i1028\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\"><v:imagedata src=\"wordml://02000002.jpg\" o:title=\"AAABD2KacGEBi7yYThPBnA\"/></v:shape></w:pict></w:r></w:p><w:p wsp:rsidR=\"008730C8\" wsp:rsidRDefault=\"008730C8\"/><w:p wsp:rsidR=\"008730C8\" wsp:rsidRDefault=\"008730C8\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"006264BE\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p><w:sectPr wsp:rsidR=\"008730C8\" wsp:rsidSect=\"00FC06D7\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1080\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/></w:sectPr></wx:sub-section></wx:sub-section></wx:sub-section></wx:sect></w:body>";
   private static final String MULIT_2007_EDIT =
      "<w:body><wx:sub-section><wx:sub-section><wx:sub-section><w:p wsp:rsidR=\"00964C23\" wsp:rsidRDefault=\"00DB6B9D\"><w:pPr><w:pStyle w:val=\"Heading3\"/><w:listPr><wx:t wx:val=\"3.2.3  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Aircraft Systems Management Subsystem Crew Interface</w:t></w:r></w:p><w:p wsp:rsidR=\"00964C23\" wsp:rsidRDefault=\"00964C23\"><w:r><w:pict><v:shapetype id=\"_x0000_t75\" coordsize=\"21600,21600\" o:spt=\"75\" o:preferrelative=\"t\" path=\"m@4@5l@4@11@9@11@9@5xe\" filled=\"f\" stroked=\"f\"><v:stroke joinstyle=\"miter\"/><v:formulas><v:f eqn=\"if lineDrawn pixelLineWidth 0\"/><v:f eqn=\"sum @0 1 0\"/><v:f eqn=\"sum 0 0 @1\"/><v:f eqn=\"prod @2 1 2\"/><v:f eqn=\"prod @3 21600 pixelWidth\"/><v:f eqn=\"prod @3 21600 pixelHeight\"/><v:f eqn=\"sum @0 0 1\"/><v:f eqn=\"prod @6 1 2\"/><v:f eqn=\"prod @7 21600 pixelWidth\"/><v:f eqn=\"sum @8 21600 0\"/><v:f eqn=\"prod @7 21600 pixelHeight\"/><v:f eqn=\"sum @10 21600 0\"/></v:formulas><v:path o:extrusionok=\"f\" gradientshapeok=\"t\" o:connecttype=\"rect\"/><o:lock v:ext=\"edit\" aspectratio=\"t\"/></v:shapetype><w:binData w:name=\"wordml://02000001.jpg\" xml:space=\"preserve\">/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUAEcDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDuI/HV3L4l1XTJvEnhmwtrKK1aC5uYSRd+bEHZkzcKNoPTBbhhz3Ol478fWvhjR9TFhcW0us2awt9nmRmQeY4AViMDcU3sF3bsKWxgGix0bxLpfi3Xtahs9JnTVltSYnv5IzC0UW1hkQncMk4PHAHHOBleLPh/rWrz+Kv7Nu9PWHXls2b7TvDRtAcbPlBGCAG3c9Nu3ncPfhHBSxEPaWUEovdav3OZOyv/ADPXe2ljJ81nY0ta8fjw7N4nluxDdw6V9mWC2tYpRKryoSBM5GxVJxhlzgcHLEA9pBMtxBHMgcJIodRIhRgCM8qwBB9iARXAa98P9Q1l/G4W7tok11bI2pO4lWgAyHGOASAMjOAc44wen/4S/wAPwfub/XtGtryP5Z4P7QjPlyDhlycHg5HIB9hXHiKNGdOH1dXlpe3+GHS383N+vQpN31ObtPGOsap4k1fSrW40azvrG+8iHS7+OVJbqEYPmLKG/iUOwxG2BtzkHJ2bfxxp0/iHW9KeG5hGkrF5k0kD4kdyRtUbckk7Ao6yFjtBAycbxb4O1jxRJIj2+jRTx3Mb2OswySxXdrErBsbAp3sMvj94qkkHCkVY1Hwjrp1Xxbd6RqcNpJrltbiG4ywkt5Yl2bcAH5WXPzghlJ4U4zXTKGCqRV2otpddneN3dJ3VuZ6rmVnvoT7yNh/G3h+LTdQv5r14YdOZEu1mtpY5YS+Nm6NlD4bcMHGDz6GoLTxvY33i+Hw9BZ6gsslm90ZZ7OWELhgoG10Bwfm+Y4XIABJOBx0vww1N9D8WWNpBpOnJqy2KWlvDcSSRwiAjducxgktjOcEkk59a7WXw/dj4jW/iSGSF7ZtMbT54nYq6fvPMV1wCGyeCDtx1yelZ1KGAgpcsm3Z21W/LFrZd3Jb20GnNnR0UUV45oFFFFABRRRQAUUUUAFFFFABRRRQB/9k=</w:binData><v:shape id=\"_x0000_i1025\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\"><v:imagedata src=\"wordml://02000001.jpg\" o:title=\"AAABDIyk7NQAjIItIqz3cQ\"/></v:shape></w:pict></w:r></w:p><w:p wsp:rsidR=\"00193380\" wsp:rsidRDefault=\"00236054\" wsp:rsidP=\"007117E2\"><w:pPr><w:pStyle w:val=\"paranormal\"/></w:pPr><w:r><w:t>One</w:t></w:r></w:p><w:p wsp:rsidR=\"00964C23\" wsp:rsidRDefault=\"00964C23\"><w:r><w:pict><w:binData w:name=\"wordml://02000002.jpg\" xml:space=\"preserve\">/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAAUAEcDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD1jUPE2oR+NF8NabpdtcTf2f8Ab2mubxoVC+YY9uFjck5wf886s2t6fYIE1XUNPsrpIFnmie6UCNSQu7LbSV3naGIGTjoeK5HxB4emufiUmsXXhn+29KGji1CYt32zecWztldei55H9761V17wjfal4hvL210VI7U+EpbCzjYxAwXLFgsYAbCkIxXI+XBIzivYWGwk1BOSj7t3qt+2r0+5fMzvJXO11/XrTQNMubmaSFrmO2muILV5gj3HlIXYLnk8DkgHGc1Pouo/2xoOnan5Xk/bLaO48vdu2b1DYzgZxnrivMdQ8E681ra7dJhvDJ4QXSHiedB5FyhVwWzweR8pXPzKMlR8w9H8MWc+neE9GsbqPy7m2sYIZUyDtdY1BGRweQelY4rD4elh04TUpX79PS44tt6mUnifVb3xVrWiaZpFlL/ZXkeZNc37Rb/NTeMKsT9MEdfSp/GHi+08J6Je3mIbq8tokm+w/aAkjRtKse/oSFy3XGMjFc3J4cdPH/iXU9T8Hf21Z332X7HJttZNuyLa/EsilcnHbnb9KyvG/gnXtSuvGH2HSYb4av8AYJrSUzovktCNjgbuQ+CfQbWb5s/KeylhcFOvTUpJRtBvXdvk5k3zabyb0VrNIlykkzstT8c2OhTa62rrDBaaZ5IieO6SWW5eRC2zyh8yNxxu6jLcAEjpoJ4rmCOeCVJYZVDxyRsGV1IyCCOCCO9eZeJfBeuas/xCW2tkA1ZbBrFnlUCYwgF165U5GBuwMkc45r02CRpYI5HheF3UM0UhBZCR907SRkdOCR7muHF0sPClCVJ+87X1/uQe3q5J+a6bFRbvqSUUUV55YUUUUAFZV54Y8P6jdPdX2h6Zc3MmN809pG7tgYGSRk8AD8KKKuFScHeDt6Ba5owQRW0EcEESRQxKEjjjUKqKBgAAcAAdqkooqG76sAooooAKKKKAP//Z</w:binData><v:shape id=\"_x0000_i1026\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\"><v:imagedata src=\"wordml://02000002.jpg\" o:title=\"AAABDIyk7NQAjIItIqz3cQ\"/></v:shape></w:pict></w:r></w:p><w:p wsp:rsidR=\"00964C23\" wsp:rsidRDefault=\"00964C23\"/><w:p wsp:rsidR=\"00964C23\" wsp:rsidRDefault=\"00964C23\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00DB6B9D\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p></wx:sub-section><wx:sub-section><w:p wsp:rsidR=\"00964C23\" wsp:rsidRDefault=\"00DB6B9D\"><w:pPr><w:pStyle w:val=\"Heading3\"/><w:listPr><wx:t wx:val=\"3.2.4  \"/><wx:font wx:val=\"Times New Roman\"/></w:listPr></w:pPr><w:r><w:t>Controls and Display Subsystem Crew Interface</w:t></w:r></w:p><w:p wsp:rsidR=\"00964C23\" wsp:rsidRDefault=\"00964C23\"><w:r><w:pict><v:shape id=\"_x0000_i1027\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\"><v:imagedata src=\"wordml://02000001.jpg\" o:title=\"AAABDIpXNGsAlFP5ZetuAg\"/></v:shape></w:pict></w:r></w:p><w:p wsp:rsidR=\"00494E1B\" wsp:rsidRDefault=\"00236054\" wsp:rsidP=\"00494E1B\"><w:pPr><w:pStyle w:val=\"paranormal\"/></w:pPr><w:r><w:t>Two</w:t></w:r></w:p><w:p wsp:rsidR=\"00964C23\" wsp:rsidRDefault=\"00964C23\"><w:r><w:pict><v:shape id=\"_x0000_i1028\" type=\"#_x0000_t75\" style=\"width:53.25pt;height:15pt\"><v:imagedata src=\"wordml://02000002.jpg\" o:title=\"AAABDIpXNGsAlFP5ZetuAg\"/></v:shape></w:pict></w:r></w:p><w:p wsp:rsidR=\"00964C23\" wsp:rsidRDefault=\"00964C23\"/><w:p wsp:rsidR=\"00964C23\" wsp:rsidRDefault=\"00964C23\"><w:pPr><w:rPr><w:vanish/></w:rPr></w:pPr><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r wsp:rsidR=\"00DB6B9D\"><w:rPr><w:vanish/></w:rPr><w:instrText>LISTNUM\"listreset\"\\l1\\s0</w:instrText></w:r><w:r><w:rPr><w:vanish/></w:rPr><w:fldChar w:fldCharType=\"end\"/><wx:t wx:val=\" .\"/></w:r></w:p></wx:sub-section></wx:sub-section></wx:sub-section><w:sectPr wsp:rsidR=\"00964C23\" wsp:rsidSect=\"00FC06D7\"><w:pgSz w:w=\"12240\" w:h=\"15840\" w:code=\"1\"/><w:pgMar w:top=\"1440\" w:right=\"1080\" w:bottom=\"1440\" w:left=\"1440\" w:header=\"432\" w:footer=\"432\" w:gutter=\"0\"/><w:pgNumType w:start=\"1\"/><w:cols w:space=\"475\"/><w:noEndnote/></w:sectPr></w:body>";

   @Test(expected = OseeCoreException.class)
   public void testEmptyChange() throws Exception {
      WordImageArtifactElementExtractor artifactElementExtractor =
         new WordImageArtifactElementExtractor(getDocumentWrapTags(NO_CHANGE));
      artifactElementExtractor.extractElements();
   }

   @Test
   public void testOLEObject() throws Exception {
      String content = Lib.fileToString(getClass(), EQUATION_TEST);
      WordImageArtifactElementExtractor artifactElementExtractor =
         new WordImageArtifactElementExtractor(getDocument(content));
      artifactElementExtractor.extractElements();
      Assert.assertTrue(artifactElementExtractor.getOleDataElement() != null);
   }

   @Test
   public void test2007SimpleChange() throws Exception {
      WordImageArtifactElementExtractor extractor = new WordImageArtifactElementExtractor(
         getDocumentWrapTags(createTestString(START_2007, END_2007, SIMPLE_2007_CHANGE)));

      Collection<WordExtractorData> artElements = extractor.extractElements();
      Assert.assertTrue(artElements.size() == 1);

      String actual = WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
         MSWordTemplateClientRenderer.getFormattedContent(artElements.iterator().next().getParentEelement()))));
      Assert.assertEquals("Middle change", actual);
   }

   @Test
   public void test2007StartChange() throws Exception {
      WordImageArtifactElementExtractor extractor = new WordImageArtifactElementExtractor(
         getDocumentWrapTags(createTestString(START_2007, END_2007, START_2007_CHANGE)));

      Collection<WordExtractorData> artElements = extractor.extractElements();
      Assert.assertTrue(artElements.size() == 1);
      String value = WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
         MSWordTemplateClientRenderer.getFormattedContent(artElements.iterator().next().getParentEelement()))));
      Assert.assertTrue("I am change".equals(value));
   }

   @Test
   public void test2007EndChange() throws Exception {
      WordImageArtifactElementExtractor extractor = new WordImageArtifactElementExtractor(
         getDocumentWrapTags(createTestString(START_2007, END_2007, END_2007_CHANGE)));

      Collection<WordExtractorData> artElements = extractor.extractElements();
      Assert.assertTrue(artElements.size() == 1);
      Assert.assertTrue("End change".equals(WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
         MSWordTemplateClientRenderer.getFormattedContent(artElements.iterator().next().getParentEelement()))))));
   }

   @Test
   public void testInBetweenChange2007Multi() throws Exception {
      WordImageArtifactElementExtractor extractor =
         new WordImageArtifactElementExtractor(getDocumentWrapTags(MULIT_2007_EDIT));

      List<WordExtractorData> artElements = extractor.extractElements();
      Assert.assertTrue("expected 2 got " + artElements.size(), artElements.size() == 2);
      List<String> testText = Arrays.asList("One", "Two");
      multiArtifactTest(artElements, testText);
   }

   @Test
   public void test2003Change() throws Exception {
      WordImageArtifactElementExtractor extractor = new WordImageArtifactElementExtractor(getDocumentWrapTags(
         createTestString(START_2003, END_2003, "</w:p><w:p><w:r><w:t>This is a test-x</w:t></w:r></w:p><w:p>")));

      Collection<WordExtractorData> artElements = extractor.extractElements();
      Assert.assertTrue(extractor.extractElements().size() == 1);
      Assert.assertTrue("This is a test-x".equals(WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
         MSWordTemplateClientRenderer.getFormattedContent(artElements.iterator().next().getParentEelement()))))));
   }

   @Test
   public void test2003StartChange() throws Exception {
      WordImageArtifactElementExtractor extractor = new WordImageArtifactElementExtractor(getDocumentWrapTags(
         createTestString(START_2003, END_2003, "<w:r><w:t>this This is a test that</w:t></w:r></w:p><w:p>")));

      Collection<WordExtractorData> artElements = extractor.extractElements();
      Assert.assertTrue(extractor.extractElements().size() == 1);
      String artContent = WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
         MSWordTemplateClientRenderer.getFormattedContent(artElements.iterator().next().getParentEelement()))));
      Assert.assertTrue("Got*" + artContent, "this This is a test that".equals(artContent));
   }

   @Test
   public void testInBetweenChange2003Multi() throws Exception {
      WordImageArtifactElementExtractor extractor =
         new WordImageArtifactElementExtractor(getDocumentWrapTags(MULULTI_2003_EDIT_MULTI_SECTION));

      List<WordExtractorData> artElements = extractor.extractElements();
      Assert.assertTrue("expected 2 got " + artElements.size(), artElements.size() == 2);
      List<String> testText = Arrays.asList("One", "Two");
      multiArtifactTest(artElements, testText);
   }

   /**
    * Test should fail if running time exceeds 10 seconds
    */
   @Test(timeout = 10000)
   public void testPerformance() throws Exception {
      String content = Lib.fileToString(getClass(), PERFORMANCE_TEST);
      WordImageArtifactElementExtractor artifactElementExtractor =
         new WordImageArtifactElementExtractor(getDocument(content));
      artifactElementExtractor.extractElements();
   }

   private String createTestString(String start, String end, String... datas) {
      StringBuilder stringBuilder = new StringBuilder(start);
      for (String data : datas) {
         String guid = GUID.create();
         stringBuilder.append(WordCoreUtil.getStartEditImage(guid));
         stringBuilder.append(data);
         stringBuilder.append(WordCoreUtil.getEndEditImage(guid));
      }
      stringBuilder.append(end);
      return stringBuilder.toString();
   }

   private void multiArtifactTest(List<WordExtractorData> actuals, List<String> expected) throws IOException, XMLStreamException {
      for (int i = 0; i < actuals.size(); i++) {
         String artContent = WordUtil.textOnly(Lib.inputStreamToString(new ByteArrayInputStream(
            MSWordTemplateClientRenderer.getFormattedContent(actuals.get(i).getParentEelement()))));
         Assert.assertTrue("expected:*" + expected.get(i) + "* got:*" + artContent + "*",
            expected.get(i).equals(artContent));
      }
   }

   private Document getDocumentWrapTags(String xmlString) throws ParserConfigurationException, SAXException, IOException {
      return getDocument(WORDML_START + xmlString + WORDML_END);
   }

   private Document getDocument(String xmlString) throws ParserConfigurationException, SAXException, IOException {
      return Jaxp.readXmlDocumentNamespaceAware(xmlString);
   }
}
