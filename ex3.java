
public class ex3{
	public static void main(String [] args){
		int n = 30;
		if(args.length > 0)
			n = Integer.parseInt(args[0]);
		int[] arr = new int[n > 0 ? n : 30];
		n = arr.length;
		int i, j, k;

		for(i = 0, j = 0, k = 0; k < n / 2; i = (i + 1) % n){
			if(arr[i] == 0){ j = (j + 1) % 9; }
			if(j == 0 && arr[i] == 0){ ++k; arr[i] = 1; }
		}
		for(i = 0; i < n; ++i){
			if(arr[i] == 0) System.out.print((i+1) + ", ");
		}
		System.out.println();
	}
}

		
