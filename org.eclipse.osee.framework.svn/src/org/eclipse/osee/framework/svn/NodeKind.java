package org.eclipse.osee.framework.svn;
   public class NodeKind {
	    /** absent */
	    public static final int none = 0;

	    /** regular file */
	    public static final int file = 1;

	    /** directory */
	    public static final int dir = 2;

	    /** something's here, but we don't know what */
	    public static final int unknown = 3;

	    /**
	     * mapping for the constants to text
	     */
		public static final String[] NAMES = {
			"none",
			"file",
			"dir",
			"unknown",
		};
		
	}