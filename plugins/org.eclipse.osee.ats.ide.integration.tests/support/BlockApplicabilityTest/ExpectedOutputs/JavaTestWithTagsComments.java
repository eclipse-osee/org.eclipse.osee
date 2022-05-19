package saw.productline.package

import robot.api;

public class JavaTestWithTags {

	private String test;
	
	public JavaTestWithTags(String test){
		this.test = test;
	}
	
	public static void main(){
		String str;
		/* Configuration[Product A] */
		str = testMethodName();
		/* Configuration Else */
		/* str = "Name: Light Robot"; */
		/* End Configuration */
		/* Configuration[Product C] */
		/* str = testMethodType(); */
		/* End Configuration */
		System.out.println(str);
	}
	
	/* Configuration[Product A] */
	private String testMethodName(){
		return test + robot.getName();
	}
	/* End Configuration */
	
	/* Configuration[Product C] */
	/* private String testMethodType(){ */
		/* return test + robot.getType() */
	/* } */
	/* End Configuration */
}
