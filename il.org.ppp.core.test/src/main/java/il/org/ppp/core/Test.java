package il.org.ppp.core;

import lombok.Operation;

public class Test {
	@Operation
	interface root {
		double base();

		default double exp() {
			new Object();
			return 2;
		}

		default double value() {
			return Math.pow(base(), 1.0 / exp());
		}
	}

	public static void main(String[] args) {
		System.out.println(root.apply(4.0).value());
		System.out.println(root.apply(4.0, 3.0).value());
		System.out.println("square root of 5 = " + root(5));
		System.out.println("cube root of 5 = " + root(5, 3));
	}
}
