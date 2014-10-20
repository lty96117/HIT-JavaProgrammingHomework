public class ex6{
	public static void main(String [] args){
		int n = 15;
		if(args.length > 0)
			n = Integer.parseInt(args[0]);
		int[] arr = new int[(int)(n > 3 ? (n < 50 ? n : 15) : 15)];
		n = arr.length;
		int i, j, k;
		for(i = 0, j = 0, k = n; k > 2; i = (i + 1) % n){
			if(arr[i] == 0) j = (j + 1) % 2;
			if(j == 1 && arr[i] == 0){ --k; arr[i] = 1; }
		}
		for(i = 0; i < n; ++i){
			if(arr[i] == 0) System.out.println((i+1));
		}
	}
}
