package maximum_weighted_clique;
import java.io.IOException;
import java.util.*;
public class clique7 {
    long deadline;				// 占쏙옙占싼시곤옙
	int cnt = 0;				// 占싻깍옙 占쏙옙
	int Vnbr, Enbr;				// 占쏙옙占쏙옙占쏙옙 占쏙옙, 占쏙옙占쏙옙 占쏙옙
	boolean[][] adj, nadj;		// 占쏙옙占쏙옙占쏙옙占�, 占쏙옙占쌓뤄옙占쏙옙
	int[] wt;					// 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙치 占쏙옙占쏙옙트
	int[] current;				// 占쏙옙占쏙옙占쏙옙 clique
	int currentSize;			// 占쏙옙占쏙옙占쏙옙 clique占쏙옙 占쏙옙占쌉듸옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙
	int currentWeight;			// 占쏙옙占쏙옙占쏙옙 clique占쏙옙 占쏙옙占쌉되댐옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙치占쏙옙 占쏙옙
	int[] record = new int[0];	// best clique
	int recordWeight;			// best clique占쏙옙 占쏙옙占쌉되댐옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙치占쏙옙 占쏙옙(占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙占� 占쏙옙占쏙옙)
	int switch_number;
	
	long startTime;
	static long clockPerSecond = 1000000000;
	
	void printRecord() {
		double time = 1.0 * (System.nanoTime() - startTime) / (clockPerSecond);
		System.out.printf("%.5f, %d, %d, %s\n", time, recordWeight, cnt, Arrays.toString(record));
	}
	
	public static void main(String[] args) throws IOException{
		long TIME_LIMIT = 3600 * clockPerSecond;
		int switch_number = 0;
		Scanner scanner = new Scanner(System.in);
		new clique7(scanner, TIME_LIMIT, switch_number);
	}
	
	public clique7(Scanner sc, long limit, int switchNum) throws IOException{
		this.switch_number = switchNum;
		graph(sc);		// 占쌓뤄옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙
		
		// start clock
		startTime = System.nanoTime();
		
		// 占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占�
		int[] degree = new int[Vnbr];
		for(int i = 0; i < Vnbr; i++) {
			// 占쏙옙占쏙옙 i占쏙옙 占쏙옙占쏙옙 占쏙옙占�
			for(int j = 0; j < Vnbr; j++) {
				if(adj[i][j]) {
					++degree[i];
				}
			}
		}
		
		// 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙
		int[] vset = new int[Vnbr+1];
		for(int i = 0; i < Vnbr; i++) {
			degree[i] = Vnbr*degree[i] + i;
		}
		Arrays.sort(degree);
		for(int i = 0; i < Vnbr; i++) {
			vset[Vnbr-1-i] = degree[i]%Vnbr;		// vset-> 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쌕몌옙 占쏙옙占쏙옙 占쏙옙호占쏙옙 크占썩가 큰占쏙옙 占쏙옙占쏙옙 占쏙옙
		}
		int[] upper = new int[Vnbr+1];
        int[] value = new int[Vnbr+1];
		numberSort(Vnbr, vset, upper, value, degree);
		
		/*main route*/
		current = new int[Vnbr+1];
		currentSize = 0;
		currentWeight = recordWeight = 0;
		deadline = limit + startTime;
		expand(Vnbr, vset, upper, value, degree);
		printRecord();
	}
	
	void expand(int n, int[] vset, int[] upper, int[] value, int[] degree) {
		if(++cnt % 1000 == 0) {
			if(System.nanoTime() >= deadline) {
				System.out.println("time over");
				System.exit(0);
			}
		}
		
		for(int i = 0; i < n; i++) {
			// 占쏙옙占� 탈占쏙옙 占쏙옙占쏙옙(占쏙옙占� 占쌓쏙옙트) v=vset[i] 占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占십댐옙 占쏙옙占�
			if(currentWeight + upper[i] <= recordWeight) {
				return;
			}
			// v占쏙옙 占쏙옙占쏙옙占싹댐옙 占쏙옙占�
			int v = vset[i];
			current[currentSize++] = v;
			currentWeight += wt[v];
			
			// v占쏙옙 占쏙옙占쏙옙占싹댐옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙트 vset2, 占쌩븝옙 占쏙옙占쏙옙 占쏙옙 占쏙옙占싱울옙占� 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 크占쏙옙占� Vnbr(n)-i
			int[] vset2 = new int[n-i];
			int n2 = 0;
			boolean[] adjv = adj[v];
			for(int j = i+1; j < n; j++) {
				if((adjv[vset[j]])) {
					vset2[n2++] = vset[j];
				}
			}
			// v占쏙옙 占쏙옙占쏙옙占싹댐옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占� (占쏙옙占쏙옙 占쌔야듸옙 占싸븝옙)
			if(n2 == 0) {
				// 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙
				if(recordWeight < currentWeight) {
					record = new int[currentSize];
					System.arraycopy(current, 0, record, 0, currentSize);
					recordWeight = currentWeight;
					printRecord();
				}
			}
			// v占쏙옙 占쏙옙占쏙옙占싹댐옙 占쏙옙占쏙옙占쏙옙 占쌍다몌옙 占쏙옙拷占� 占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙 클占쏙옙크占쏙옙 占쏙옙占쏙옙
			else {
					int[] upper2 = new int[n2+1];
                    int[] value2 = new int[n2+1];
					numberSort(n2, vset2, upper2, value2, degree);
					expand(n2, vset2, upper2, value2, degree);
				}
			// 占쏙옙拷占� 占쏙옙占쏙옙占쏙옙 처占쏙옙(占쏙옙占쏙옙占쏙옙 v占쏙옙 占쏙옙占쏙옙)
			currentSize--;
			currentWeight -= wt[v];
		}
	}
	
	
	// 占쏙옙占쏙옙占쏙옙占� 占쏙옙占싱곤옙 占쏙옙占쏙옙占쏙옙 占쌜곤옙 占실듸옙占쏙옙  vset占쏙옙 占쏙옙占쏙옙
	void numberSort(int n, int[] vset, int[] upper, int[] value, int[] degree) {
		// upper占쏙옙 占십깍옙화
		for(int i = 0; i < n; i++) {
			upper[i] = wt[vset[i]];
            value[i] = wt[vset[i]]*degree[vset[i]];
		}
		for(int i = n - 1; i >= 0; i--) {
            int t = i;
            int tval = value[t];
			for(int p = i - 1; p >= 0; p--) { 
				int pval = value[p];
				if(pval < tval) {
					t = p;
					tval = pval;
				}
			}
			// u = 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙호占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙호 (ex: {0: 1, 1: 10 ,2: 1} 占싹띰옙 u = 0)
			int u = vset[t];
            int sval = upper[t];
			vset[t] = vset[i];
			vset[i] = u;
            value[t] = value[i];
            value[i] = tval;
			upper[t] = upper[i];
			upper[i] = sval;
			
			// u占쏙옙 占쏙옙占쏙옙占싹댐옙 占쏙옙占쏙옙占� 占쏙옙占쏙옙 占쏙옙占쏙옙 (u占쏙옙 占쏙옙占쏙옙치 + v占쏙옙 占쏙옙占쏙옙치)
			for(int j = i-1; j >= 0; j--) {
				int v = vset[j];
				if (adj[u][v]) {
					upper[j] = sval + wt[v];
				}
			}
		}
	}

	// 占쌓뤄옙占쏙옙 input
	void graph(Scanner fp) {
		Vnbr = fp.nextInt();				// 占쏙옙占쏙옙占쏙옙 占쌉뤄옙
		Enbr = fp.nextInt();				// 占쏙옙占쏙옙 占쏙옙 占쌉뤄옙
		adj = new boolean[Vnbr+1][];
		nadj = new boolean[Vnbr+1][];
		wt = new int[Vnbr+1];
		for(int i = 0; i <= Vnbr; i++) {
			adj[i] = new boolean[Vnbr+1];
			nadj[i] = new boolean[Vnbr+1];
		}
		// empty graph 占쏙옙占쏙옙
		for(int i = 0; i < Vnbr; i++) {
			Arrays.fill(adj[i], false);
			Arrays.fill(nadj[i], true);
		}
		for(int i = 0; i < Vnbr; i++) {
			int weight = fp.nextInt();		// 占쏙옙占쏙옙치 占쌉뤄옙
			int deg = fp.nextInt();			// 占쏙옙占쏙옙 占쌉뤄옙
			wt[i] = weight;					// 占쏙옙占쏙옙 i占쏙옙 占쏙옙占쏙옙치
			// 占쏙옙占쏙옙 i占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙
			for(int j = 0; j < deg; j++) {
				int entry = fp.nextInt();
				adj[i][entry] = true;
				nadj[i][entry] = false;
			}
			adj[i][Vnbr] = adj[Vnbr][i] = true;			// dummy占쏙옙占쏙옙 占쌉뤄옙
			nadj[i][Vnbr] = nadj[Vnbr][i] = false;		// dummy占쏙옙占쏙옙 占쌉뤄옙
		}
		wt[Vnbr] = 0;		// dummy 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙치 = 0
	}

}
