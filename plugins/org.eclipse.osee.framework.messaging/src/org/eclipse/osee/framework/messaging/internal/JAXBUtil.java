/*
 * Created on Jan 22, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXB;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.osee.framework.messaging.OseeMessagingListener;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class JAXBUtil {

	public static void marshal(Object obj, OutputStream stream){
		JAXB.marshal(obj, stream);
	}
	
	public static String marshal(Object obj) throws UnsupportedEncodingException{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		marshal(obj, os);
		return new String(os.toByteArray(), "UTF-8");
	}
	
	public static Object unmarshal(String str, Class<?> clazz) throws UnsupportedEncodingException{
		ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
		return JAXB.unmarshal(new StreamSource(is), clazz);
	}
	
	public static Object unmarshal(Object body, OseeMessagingListener listener) throws UnsupportedEncodingException{
		Class<?> pojoType = listener.getClazz();
		Object messageBody;
		if (pojoType == null) {
			messageBody = body;
		} else {
			messageBody = JAXBUtil.unmarshal(body.toString(), pojoType);
		}
		return messageBody;
	}
	
}
