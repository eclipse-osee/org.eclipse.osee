/*
 * Created on Feb 25, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.logging;

import java.util.logging.Level;

/**
 * @author osee
 *
 */
public class OteLevel extends Level {

	private static final long serialVersionUID = -1545385440588581634L;
	
	public static final Level TEST_EVENT = new OteLevel("TEST_EVENT",SEVERE.intValue());
	public static final Level TEST_SEVERE = new OteLevel("TEST_SEVERE",SEVERE.intValue()+100);
	public static final Level ENV_SEVERE = new OteLevel("ENV_SEVERE",SEVERE.intValue()+200);
	
	/**
	 * @param name
	 * @param value
	 */
	protected OteLevel(String name, int value) {
		super(name, value);
	}

}
