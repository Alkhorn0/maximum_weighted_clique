package maximum_weighted_clique;
import java.io.IOException;
import java.util.*;
public class clique5 {
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
		new clique5(scanner, TIME_LIMIT, switch_number);
	}
	
	public clique5(Scanner sc, long limit, int switchNum) throws IOException{
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
		numberSort(Vnbr, vset, upper);
		
		/*main route*/
		current = new int[Vnbr+1];
		currentSize = 0;
		currentWeight = recordWeight = 0;
		deadline = limit + startTime;
		expand(Vnbr, vset, upper, degree);
		printRecord();
	}
	
	void expand(int n, int[] vset, int[] upper, int[] degree) {
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
				if((adjv[vset[j]])&(degree[v]*0.5 < degree[vset[j]])) {
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
					numberSort(n2, vset2, upper2);
					expand(n2, vset2, upper2, degree);
				}
			// ��͸� ������ ó��(������ v�� ����)
			currentSize--;
			currentWeight -= wt[v];
		}
	}
	
	
	// ������� ���̰� ������ �۰� �ǵ���  vset�� ����
	void numberSort(int n, int[] vset, int[] upper) {
		// upper�� �ʱ�ȭ
		for(int i = 0; i < n; i++) {
			upper[i] = wt[vset[i]];
		}
		for(int i = n - 1; i >= 0; i--) {
			int s = i;
			int sval = upper[s];
			for(int p = i - 1; p >= 0; p--) {
				int pval = upper[p];
				if(pval < sval) {
					s = p;
					sval = pval;
				}
			}
			// u = ������ ������ ��ȣ�� ���� ���� ������ ��ȣ (ex: {0: 1, 1: 10 ,2: 1} �϶� u = 0)
			int u = vset[s];
			vset[s] = vset[i];
			vset[i] = u;
			upper[s] = upper[i];
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
	
	/*
	// ������� ���̸� ����Ѵ�. 
	void numberSort2(int n, int[] vset, int[] upper) {
		// ���� ������ ����
		vset[n] = Vnbr;
		upper[n] = 0;
		
		int p = vset[n-1];	// dummy������ ������ ����ġ�� ���� ���� ������ ��ȣ = p
		int pval = wt[p];	// p�� ����ġ = pval
		
		for(int i = n-1; i >= 1; i--) {
			int q = vset[i-1];
			boolean[] nadjq = nadj[q];
			
			int j = i;
			while(nadjq[vset[j++]]);
			int qval = upper[j] + wt[q];
			
			// ���� ���� ���� vset[i] �� �ִ´�
			if(pval < qval) {
				// p�� ����
				for(j = i+1; pval < upper[j]; j++) {
					vset[j-1] = vset[j];
					upper[j-1] = upper[j];
				}
				vset[j-1] = p;
				upper[j-1] = pval;
				
				//pval�� ����
				pval = (!nadjq[p]) && (qval < pval + wt[q]) ? pval + wt[q] : qval;
				p = q;
			}
			else {
				// q�� ����
				for(j = i+1; qval < upper[j]; j++) {
					vset[j-1] = vset[j];
					upper[j-1] = upper[j];
				}
				vset[j-1] = q;
				upper[j-1] = qval;
				
				// pval�� ����
				if((!nadjq[p]) && pval < qval + wt[p]) {
					pval = qval + wt[p];
				}
			}
			vset[0] = p;
			upper[0] = pval;
		}
	}
	*/
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
