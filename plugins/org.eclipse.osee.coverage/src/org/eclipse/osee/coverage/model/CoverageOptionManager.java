/*
 * Created on Dec 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Donald G. Dunne
 */
public class CoverageOptionManager {
   private static String MGR_TAG = "options";
   private static String ITEM_TAG = "option";

   public List<CoverageOption> options;
   public static CoverageOption Deactivated_Code = new CoverageOption("Deactivated_Code");
   public static CoverageOption Dead_Code = new CoverageOption("Dead_Code");
   public static CoverageOption Exception_Handling = new CoverageOption("Exception_Handling");
   public static CoverageOption Test_Unit = new CoverageOption("Test_Unit");
   public static CoverageOption Not_Covered = new CoverageOption("Not_Covered");

   public CoverageOptionManager(List<CoverageOption> options) {
      this.options = options;
   }

   public CoverageOptionManager() {
      this.options = new ArrayList<CoverageOption>();
   }

   public CoverageOptionManager(String xml) {
      fromXml(xml);
   }

   public Collection<CoverageOption> getEnabled() {
      List<CoverageOption> enabled = new ArrayList<CoverageOption>();
      for (CoverageOption option : options) {
         if (option.isEnabled()) {
            enabled.add(option);
         }
      }
      return enabled;
   }

   public void add(CoverageOption coverageOption) throws OseeArgumentException {
      if (get(coverageOption.getName()) != null) throw new OseeArgumentException(String.format(
            "Option with name [%s] already exists", coverageOption.getName()));
      options.add(coverageOption);
   }

   public CoverageOption get(String name) {
      for (CoverageOption option : options) {
         if (option.getName().equals(name)) {
            return option;
         }
      }
      return null;
   }

   public void fromXml(String xml) {
      if (options == null) {
         options = new ArrayList<CoverageOption>();
      } else {
         options.clear();
      }
      if (Strings.isValid(xml)) {
         NodeList nodes;
         try {
            nodes = Jaxp.readXmlDocument(xml).getElementsByTagName(ITEM_TAG);
            for (int i = 0; i < nodes.getLength(); i++) {
               Element element = (Element) nodes.item(i);
               options.add(new CoverageOption(element.getAttribute("name"), element.getAttribute("desc"),
                     element.getAttribute("enabled").equals("true")));
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
         }
      }
   }

   public String toXml() {
      try {
         Document doc = Jaxp.newDocument();
         Element rootElement = doc.createElement(MGR_TAG);
         doc.appendChild(rootElement);
         for (CoverageOption item : options) {
            Element element = doc.createElement(ITEM_TAG);
            element.setAttribute("name", item.getName());
            element.setAttribute("enabled", String.valueOf(item.isEnabled()));
            element.setAttribute("desc", item.getDescription());
            rootElement.appendChild(element);
         }
         return Jaxp.getDocumentXml(doc);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't create coverage options document", ex);
      }
      return null;
   }

   public List<CoverageOption> get() {
      return options;
   }
}
