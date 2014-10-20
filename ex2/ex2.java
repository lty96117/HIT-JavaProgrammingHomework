class ex2{
	public static void main(String [] args){
		float mon = Float.parseFloat(args[0]);
		long bot = (long)(mon * 100 / 48);
		long juc = bot;
		while(bot >= 20){ bot -= 13; juc += 7; }
		while(bot >= 3){ bot -= 2; juc++; }

		System.out.println(juc);
	}
}
