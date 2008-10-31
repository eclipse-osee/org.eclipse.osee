package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.connection.OseeApplicationServerContext;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.WorkflowEditor;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class FixWordTemplateContent extends AbstractBlam {
   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet/debug/Blam"));
   
   private static final String GET_ATTRS = "select * from osee_attribute t1, osee_attribute_type t2, osee_artifact t3 where t3.art_id = t1.art_id and t1.attr_type_id = t2.attr_type_id and t2.name = 'Word Template Content'";
   private static final String GET_ATTRS_TEST = GET_ATTRS + " AND t1.art_id = 3894";
   private static final String GET_INFO = "select osee_value from osee_info where osee_key =?";
   private String applicationServerAddress;

   @Override
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
	   ArrayList<AttrData> attrDatas = loadAttrData();
	   monitor.beginTask("Fix lil Therons' big mistake" , attrDatas.size());
	   for(AttrData attrData : attrDatas){
		   monitor.subTask(attrData.getHrid());
		   String content = new String(getResource(attrData.getUri()));
		   
           if (DEBUG) {
               System.out.println(String.format("Before Fix: %s", content));
            }

            final Collection<Element> elements = new LinkedList<Element>();
            final Collection<Element> sectPr = new LinkedList<Element>();

            Document doc = Jaxp.readXmlDocument("<ForFix>" + content + "</ForFix>");
            Element rootElement = doc.getDocumentElement();

            NodeList nodeList = rootElement.getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); i++) {
               Element element = (Element) nodeList.item(i);
               if (element.getNodeName().endsWith("w:p")) {
                  elements.add(element);
               }
               if (element.getNodeName().endsWith("w:sectPr")) {
                  sectPr.add(element);
               }
            }
            boolean fixedAttribute = false;
            for (Element paragraph : elements) {
               boolean badParagraph = isBadParagraph(paragraph);
               if (badParagraph) {
                  paragraph.getParentNode().removeChild(paragraph);
               }
               fixedAttribute = fixedAttribute || badParagraph;
            }
            for (Element sect : sectPr) {
               sect.getParentNode().removeChild(sect);
               fixedAttribute = true;
            }

            if (fixedAttribute) {
            	//backup old files before changing them
            	try{
               HttpProcessor.put(getStorageURL(attrData.getGammaId(), attrData.getHrid(), "bck", false), Lib.stringToInputStream(content), "txt/xml", "UTF-8");
               String fixedContent =
                     Lib.inputStreamToString(new ByteArrayInputStream(
                           WordTemplateRenderer.getFormattedContent(rootElement)));
               
               HttpProcessor.put(getStorageURL(attrData.getGammaId(), attrData.getHrid(), attrData.getUri().contains(".xml")? "xml": null, true), new ByteArrayInputStream(WordTemplateRenderer.getFormattedContent(rootElement)), "txt/xml", "UTF-8");
  
               if (DEBUG) {
                  System.out.println(String.format(" After Fix : %s", fixedContent));
               }
            	}catch(Exception ex){
            		System.out.println(String.format("Skiping File %s because of exception %s",attrData.getHrid(), ex));
            	}
            } 
            monitor.worked(1);
	   }
   }                                                                                                                                                                                                                                                       
   
   private ArrayList<AttrData> loadAttrData() throws OseeDataStoreException {
		ArrayList<AttrData> attrData = new ArrayList<AttrData>();

		ConnectionHandlerStatement chStmt = null;
		try {
			chStmt = ConnectionHandler.runPreparedQuery(GET_ATTRS_TEST);
			while (chStmt.next()) {
				attrData.add(new AttrData(chStmt.getString("gamma_Id"), chStmt.getString("human_readable_id"), chStmt.getString("uri")));
			}
		} finally {
			ConnectionHandler.close(chStmt);
		}
		return attrData;
	}
   
   public static URL getStorageURL(String gammaId, String artifactHrid, String extension, boolean allowOverwrite) throws OseeDataStoreException, MalformedURLException {
	      Map<String, String> parameterMap = new HashMap<String, String>();
	      parameterMap.put("protocol", "attr");
	      parameterMap.put("seed", gammaId);
	      parameterMap.put("name", artifactHrid);
	      parameterMap.put("is.overwrite.allowed",String.valueOf(allowOverwrite));
	      parameterMap.put("compress.before.saving","true");
	      if (Strings.isValid(extension) != false) {
	         parameterMap.put("extension", extension);
	      }
	      String urlString =
	            HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeApplicationServerContext.RESOURCE_CONTEXT,
	                  parameterMap);
	      return new URL(urlString);
	   }
   
   public byte[] getResource(String resourcePath) throws OseeCoreException {
       byte[] toReturn = null;
       ByteArrayOutputStream sourceOutputStream = new ByteArrayOutputStream();
       try {
          AcquireResult result =
                HttpProcessor.acquire(getAcquireUrl(getApplicationServerAddress(), resourcePath), sourceOutputStream);
          if (result.getCode() == HttpURLConnection.HTTP_OK) {
             toReturn = sourceOutputStream.toByteArray();
          }
       } catch (Exception ex) {
          throw new OseeWrappedException(ex);
       } finally {
          try {
             sourceOutputStream.close();
          } catch (IOException ex) {
             throw new OseeWrappedException(ex);
          }
       }
       return toReturn;
    }
   
   private URL getAcquireUrl(String address, String uriPath) throws MalformedURLException, UnsupportedEncodingException {
       if (address.endsWith("/")) {
          address = address.substring(0, address.length() - 1);
       }
       return new URL(String.format("%s/%s?uri=%s&decompress.before.sending=true", address, OseeApplicationServerContext.RESOURCE_CONTEXT,
             URLEncoder.encode(uriPath, "UTF-8")));
    }
   
   private String getApplicationServerAddress() throws OseeDataStoreException {
       if (applicationServerAddress == null) {
          applicationServerAddress =
                ConnectionHandler.runPreparedQueryFetchString(ConnectionHandler.getPooledConnection(), null, GET_INFO, "osee.resource.server");
       }
       return applicationServerAddress;
    }

   //To handle the case of sub-sections
   private boolean isBadParagraph(Element paragraph) throws OseeCoreException {
      boolean badParagraph = false;
      String content = paragraph.getTextContent();
      if (content != null && content.contains("LISTNUM \"listreset\"")) {
         badParagraph = true;
      }

      return badParagraph;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   @Override
   public String getXWidgetsXml() {
      return "<xWidgets></xWidgets>";
   }

   @Override
   public void setWorkflowEditor(WorkflowEditor workflow) {
      // TODO Auto-generated method stub

   }

   @Override
   public Branch wrapOperationForBranch(BlamVariableMap variableMap) throws OseeArgumentException {
      return null;
   }
   
    class AttrData {
		private String gammaId;
		private String hrid;
		private String uri;

		public AttrData(String gammaId, String hrid, String uri) {
			super();
			this.gammaId = gammaId;
			this.hrid = hrid;
			this.uri = uri;
		}

		public String getGammaId() {
			return gammaId;
		}

		public String getHrid() {
			return hrid;
		}

		public String getUri() {
			return uri;
		}
	}
}
