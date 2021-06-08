package saw.productline.package

import robot.api;

public class JavaTestWithTags {

	private String test;
	
	public JavaTestWithTags(String test){
		this.test = test;
	}
	
	public static void main(){
		String str;
		str = testMethodName();
		System.out.println(str);
	}
	
	private String testMethodName(){
		return test + robot.getName();
	}
	
}
