public class ex5{
	public static void main(String [] args){
		int n = 15;
		if(args.length > 0)
			n = Integer.parseInt(args[0]);
		n = n >= 2 ? n : 2;
		int i, a, k;
		// n = k* a + k* (k-1) / 2
		for(a = 1; a <= n / 2; ++a){
			for(k = n / a; k > 1; --k){
			//	System.out.println(a + "," + k);
				if(k * a + k * (k - 1) / 2 == n){
					System.out.print(n + "=" + a);
					for(i = 1; i < k; ++i){
						System.out.print("+" + (a+i));
					}
					System.out.print("\n");
				}
			}
		}
	}
}
