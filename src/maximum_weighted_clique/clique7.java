package maximum_weighted_clique;
import java.io.IOException;
import java.util.*;
public class clique7 {
    long deadline;				// ���ѽð�
	int cnt = 0;				// �б� ��
	int Vnbr, Enbr;				// ������ ��, ���� ��
	boolean[][] adj, nadj;		// �������, ���׷���
	int[] wt;					// ������ ����ġ ����Ʈ
	int[] current;				// ������ clique
	int currentSize;			// ������ clique�� ���Ե� ������ ����
	int currentWeight;			// ������ clique�� ���ԵǴ� ������ ����ġ�� ��
	int[] record = new int[0];	// best clique
	int recordWeight;			// best clique�� ���ԵǴ� ������ ����ġ�� ��(���� ������� ����)
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
		graph(sc);		// �׷��� ���� ����
		
		// start clock
		startTime = System.nanoTime();
		
		// �� ������ ������ ���
		int[] degree = new int[Vnbr];
		for(int i = 0; i < Vnbr; i++) {
			// ���� i�� ���� ���
			for(int j = 0; j < Vnbr; j++) {
				if(adj[i][j]) {
					++degree[i];
				}
			}
		}
		
		// ������ ������ ������������ ����
		int[] vset = new int[Vnbr+1];
		for(int i = 0; i < Vnbr; i++) {
			degree[i] = Vnbr*degree[i] + i;
		}
		Arrays.sort(degree);
		for(int i = 0; i < Vnbr; i++) {
			vset[Vnbr-1-i] = degree[i]%Vnbr;		// vset-> ������ �������� �������� ������ ���ٸ� ���� ��ȣ�� ũ�Ⱑ ū�� ���� ��
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
			// ��� Ż�� ����(��� �׽�Ʈ) v=vset[i] �� �������� �ʴ� ���
			if(currentWeight + upper[i] <= recordWeight) {
				return;
			}
			// v�� �����ϴ� ���
			int v = vset[i];
			current[currentSize++] = v;
			currentWeight += wt[v];
			
			// v�� �����ϴ� ������ ����Ʈ vset2, �ߺ� ���� �� ���̿�� ������ ���� ũ��� Vnbr(n)-i
			int[] vset2 = new int[n-i];
			int n2 = 0;
			boolean[] adjv = adj[v];
			for(int j = i+1; j < n; j++) {
				if((adjv[vset[j]])) {
					vset2[n2++] = vset[j];
				}
			}
			// v�� �����ϴ� ������ ���� ��� (���� �ؾߵ� �κ�)
			if(n2 == 0) {
				// �������� ����
				if(recordWeight < currentWeight) {
					record = new int[currentSize];
					System.arraycopy(current, 0, record, 0, currentSize);
					recordWeight = currentWeight;
					printRecord();
				}
			}
			// v�� �����ϴ� ������ �ִٸ� ��͸� ���� �� ���� Ŭ��ũ�� ����
			else {
					int[] upper2 = new int[n2+1];
                    int[] value2 = new int[n2+1];
					numberSort(n2, vset2, upper2, value2, degree);
					expand(n2, vset2, upper2, value2, degree);
				}
			// ��͸� ������ ó��(������ v�� ����)
			currentSize--;
			currentWeight -= wt[v];
		}
	}
	
	
	// ������� ���̰� ������ �۰� �ǵ���  vset�� ����
	void numberSort(int n, int[] vset, int[] upper, int[] value, int[] degree) {
		// upper�� �ʱ�ȭ
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
			// u = ������ ������ ��ȣ�� ���� ���� ������ ��ȣ (ex: {0: 1, 1: 10 ,2: 1} �϶� u = 0)
			int u = vset[t];
            int sval = upper[t];
			vset[t] = vset[i];
			vset[i] = u;
            value[t] = value[i];
            value[i] = tval;
			upper[t] = upper[i];
			upper[i] = sval;
			
			// u�� �����ϴ� ����� ���� ���� (u�� ����ġ + v�� ����ġ)
			for(int j = i-1; j >= 0; j--) {
				int v = vset[j];
				if (adj[u][v]) {
					upper[j] = sval + wt[v];
				}
			}
		}
	}

	// �׷��� input
	void graph(Scanner fp) {
		Vnbr = fp.nextInt();				// ������ �Է�
		Enbr = fp.nextInt();				// ���� �� �Է�
		adj = new boolean[Vnbr+1][];
		nadj = new boolean[Vnbr+1][];
		wt = new int[Vnbr+1];
		for(int i = 0; i <= Vnbr; i++) {
			adj[i] = new boolean[Vnbr+1];
			nadj[i] = new boolean[Vnbr+1];
		}
		// empty graph ����
		for(int i = 0; i < Vnbr; i++) {
			Arrays.fill(adj[i], false);
			Arrays.fill(nadj[i], true);
		}
		for(int i = 0; i < Vnbr; i++) {
			int weight = fp.nextInt();		// ����ġ �Է�
			int deg = fp.nextInt();			// ���� �Է�
			wt[i] = weight;					// ���� i�� ����ġ
			// ���� i�� ���� ����
			for(int j = 0; j < deg; j++) {
				int entry = fp.nextInt();
				adj[i][entry] = true;
				nadj[i][entry] = false;
			}
			adj[i][Vnbr] = adj[Vnbr][i] = true;			// dummy���� �Է�
			nadj[i][Vnbr] = nadj[Vnbr][i] = false;		// dummy���� �Է�
		}
		wt[Vnbr] = 0;		// dummy ������ ����ġ = 0
	}

}
