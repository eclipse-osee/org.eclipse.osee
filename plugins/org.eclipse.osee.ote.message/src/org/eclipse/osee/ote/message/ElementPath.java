/*
 * Created on Apr 16, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ken J. Aguilar
 *
 */
public class ElementPath implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4928814252711879251L;

	private final List<Object> elementPath;
	private String elementPathAsString = null;
	private final boolean isHeaderElement;


	public static ElementPath decode(String encodedPath) {
		List<Object> path = new LinkedList<Object>();
		String[] components = encodedPath.split("\\+");

		for (String component : components) {
			try {
				Integer i = Integer.parseInt(component);
				path.add(i);
			} catch (NumberFormatException e) {
				path.add(component);
			}
		}

		return new ElementPath(path);
	}

	public String getMessageClass() {
		return elementPath.get(0).toString();
	}

	public boolean isHeaderElement() {
		return isHeaderElement;
	}

	public String getElementName() {
		return elementPath.get(elementPath.size() -1).toString();
	}

	public ElementPath(List<Object> path) {
		elementPath = path;
		String string = (String)path.get(1);
		isHeaderElement = string.startsWith("HEADER(");

	}


	public List<Object> getList() {
		return elementPath;
	}


	@Override
	public String toString(){
		if(elementPathAsString == null){
			StringBuilder sb = new StringBuilder(64);
			if (isHeaderElement) {
				String string = (String) elementPath.get(1);
				String headerName = string.substring(string.indexOf('(') + 1, string.indexOf(')'));
				sb.append(headerName).append("-HEADER").append('.').append(elementPath.get(2).toString());
			} else {
				for(int i = 1; i < elementPath.size(); i++){
					Object obj = elementPath.get(i);
					if (obj instanceof Integer){
						sb.delete(sb.length() -1, sb.length());
						sb.append('[');
						sb.append(((Integer)obj).intValue());
						sb.append(']');
					} else{

						sb.append(obj.toString());
					}
					sb.append('.');
				}
				sb.deleteCharAt(sb.length() -1);
			}
			elementPathAsString = sb.toString();
		}
		return elementPathAsString;
	}

	public String encode() {

		StringBuilder buffer = new StringBuilder(256);
		for (Object component : elementPath) {
			buffer.append(component.toString());
			buffer.append('+');
		}
		buffer.deleteCharAt(buffer.length()-1);
		return buffer.toString();
	}

}
