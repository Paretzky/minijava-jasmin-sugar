class Test {
	public static void main(String [] args) {
		System.out.println(new Test2().method());
	}
}
class Test2 {
	int testInt;
	int [] testIntArr;
	int [] intArr2;
	boolean bool;
	public int method(int bee) {
		testInt = intArr2[10][10-bee][0];
		intArr2[testInt+intArr2[0]*8] = 15;
		while(!(testInt < (15+testInt)+1)) {
			System.out.println(15);
		}
		return 15;
	}
	public boolean dowhile(int a, int [] b) {
		int [] c;
		do {
			System.out.println(c[b[a]]);
		} while(a < b.length+a);
		return c[b[a-1]]+5-(b[(c[b-c[0]])+1])+(new int[(1)+(b[a])]).length;
	}
	public int foreach(int [] ints) {
		int sum;
		for(int i in ints) {
			sum = i+sum;
		}
				if(5 && false) {
			a = b;
		} else {
			a = c;
		}
		return sum;
	}
}
