package featureMining;

import gate.Gate;
import gate.util.GateException;

public class Main {

	public static void main(String[] args) {
		try {
			Gate.init();
		} catch (GateException e) {
			e.printStackTrace();
		}

	}

}
