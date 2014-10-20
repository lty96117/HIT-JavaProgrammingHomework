public class ex4{
	public static void main(String [] args){
		int n = 101;
		if(args.length > 0)
			n = Integer.parseInt(args[0]);
		int[] arr = new int[n > 0 ? n : 101];
		n = arr.length;
		int i, j, k = n;
		while(k > 1){
			for(i = 0, j = 0; i < n; ++i){
				if(arr[i] == 0){
					++j;
					if(j % 2 == 1){ --k; arr[i] = 1; }
				}
			}
		}
		
		for(i = 0; i < n; ++i){
			if(arr[i] == 0) System.out.println((i+1)); 
		}
	}
}
			
