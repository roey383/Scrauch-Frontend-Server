package generalTests;

import java.util.Arrays;

public class TestClassLoader {
	
	public String name;
	
	public TestClassLoader(String name) {
		super();
		this.name = name;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TestClassLoader test = new TestClassLoader("roey");
		
		
//		System.out.println(Arrays.asList(test.getClass().getClasses()));
//		System.out.println(test.getClass().getConstructor(parameterTypes));
//		System.out.println(Arrays.asList(test.getClass().getDeclaredFields()).get(0));
		System.out.println(test.getClass().getClassLoader().getClass());

		
	}

}
