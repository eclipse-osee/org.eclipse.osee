package saw.productline.package

import robot.api;

public class JavaTestWithoutTags {

	private String test;
	
	public JavaTestWithoutTags(String test){
		this.test = test;
	}
	
	public static void main(){
		String str = testMethodName();
		System.out.println(str);
	}
	
	/* This method takes our test string and adds robot name to it */
	private String testMethodName(){
		return test + robot.getName();
	}
}
