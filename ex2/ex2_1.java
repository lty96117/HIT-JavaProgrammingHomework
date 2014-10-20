class ex2_1{
	public static void main(String [] args){
		float mon = Float.parseFloat(args[0]);
		long bot = (long)(mon * 100 / 48);
		long juc = 0;
		juc = bot + bot / 2;
		System.out.println(juc);
	}
}
