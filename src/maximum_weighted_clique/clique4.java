package maximum_weighted_clique;
import java.io.IOException;
import java.util.*;
public class clique4 {
	long deadline;				// 제한시간
	int cnt = 0;				// 분기 수
	int Vnbr, Enbr;				// 정점의 수, 변의 수
	boolean[][] adj, nadj;		// 인접행렬, 보그래프
	int[] wt;					// 정점의 가중치 리스트
	int[] current;				// 현재의 clique
	int currentSize;			// 현재의 clique에 포함된 정점의 개수
	int currentWeight;			// 현재의 clique에 포함되는 정점의 가중치의 합
	int[] record = new int[0];	// best clique
	int recordWeight;			// best clique에 포함되는 정점의 가중치의 합(최종 최장로의 길이)
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
		new clique4(scanner, TIME_LIMIT, switch_number);
	}
	
	public clique4(Scanner sc, long limit, int switchNum) throws IOException{
		this.switch_number = switchNum;
		graph(sc);		// 그래프 정보 구축
		
		// start clock
		startTime = System.nanoTime();
		
		// 각 정점의 차수를 계산
		int[] degree = new int[Vnbr];
		for(int i = 0; i < Vnbr; i++) {
			// 정점 i의 차수 계산
			for(int j = 0; j < Vnbr; j++) {
				if(adj[i][j]) {
					++degree[i];
				}
			}
		}
		
		// 정점을 차수의 내림차순으로 정렬
		int[] vset = new int[Vnbr+1];
		for(int i = 0; i < Vnbr; i++) {
			degree[i] = Vnbr*degree[i] + i;
		}
		Arrays.sort(degree);
		for(int i = 0; i < Vnbr; i++) {
			vset[Vnbr-1-i] = degree[i]%Vnbr;		// vset-> 차수를 기준으로 내림차순 차수가 같다면 정점 번호의 크기가 큰게 먼저 옴
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
			// 재귀 탈출 조건(상계 테스트) v=vset[i] 를 선택하지 않는 경우
			if(currentWeight + upper[i] <= recordWeight) {
				return;
			}
			// v를 선택하는 경우
			int v = vset[i];
			current[currentSize++] = v;
			currentWeight += wt[v];
			
			// v에 인접하는 정점의 리스트 vset2, 중복 방지 및 더미요소 삽입을 위해 크기는 Vnbr(n)-i
			int[] vset2 = new int[n-i];
			int n2 = 0;
			boolean[] adjv = adj[v];
			for(int j = i+1; j < n; j++) {
				if((adjv[vset[j]])&(wt[v]*0.5 < wt[vset[j]])) {
					vset2[n2++] = vset[j];
				}
			}
			// v에 인접하는 정점이 없는 경우 (변경 해야될 부분)
			if(n2 == 0) {
				// 최적해의 갱신
				if(recordWeight < currentWeight) {
					record = new int[currentSize];
					System.arraycopy(current, 0, record, 0, currentSize);
					recordWeight = currentWeight;
					printRecord();
				}
			}
			// v에 인접하는 정점이 있다면 재귀를 통해 더 작은 클리크로 분할
			else {
					int[] upper2 = new int[n2+1];
					numberSort(n2, vset2, upper2);
					expand(n2, vset2, upper2, degree);
				}
			// 재귀를 감안한 처리(선택한 v를 제거)
			currentSize--;
			currentWeight -= wt[v];
		}
	}
	
	
	// 최장로의 길이가 가능한 작게 되도록  vset을 정렬
	void numberSort(int n, int[] vset, int[] upper) {
		// upper의 초기화
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
			// u = 차수와 정점의 번호가 가장 작은 정점의 번호 (ex: {0: 1, 1: 10 ,2: 1} 일때 u = 0)
			int u = vset[s];
			vset[s] = vset[i];
			vset[i] = u;
			upper[s] = upper[i];
			upper[i] = sval;
			
			// u에 인접하는 요소의 값을 갱신 (u의 가중치 + v의 가중치)
			for(int j = i-1; j >= 0; j--) {
				int v = vset[j];
				if (adj[u][v]) {
					upper[j] = sval + wt[v];
				}
			}
		}
	}
	
	/*
	// 최장로의 길이를 계산한다. 
	void numberSort2(int n, int[] vset, int[] upper) {
		// 더미 정점을 삽입
		vset[n] = Vnbr;
		upper[n] = 0;
		
		int p = vset[n-1];	// dummy정점을 제외한 가중치가 가장 작은 정점의 번호 = p
		int pval = wt[p];	// p의 가중치 = pval
		
		for(int i = n-1; i >= 1; i--) {
			int q = vset[i-1];
			boolean[] nadjq = nadj[q];
			
			int j = i;
			while(nadjq[vset[j++]]);
			int qval = upper[j] + wt[q];
			
			// 값이 작은 쪽을 vset[i] 에 넣는다
			if(pval < qval) {
				// p를 삽입
				for(j = i+1; pval < upper[j]; j++) {
					vset[j-1] = vset[j];
					upper[j-1] = upper[j];
				}
				vset[j-1] = p;
				upper[j-1] = pval;
				
				//pval을 갱신
				pval = (!nadjq[p]) && (qval < pval + wt[q]) ? pval + wt[q] : qval;
				p = q;
			}
			else {
				// q를 삽입
				for(j = i+1; qval < upper[j]; j++) {
					vset[j-1] = vset[j];
					upper[j-1] = upper[j];
				}
				vset[j-1] = q;
				upper[j-1] = qval;
				
				// pval을 갱신
				if((!nadjq[p]) && pval < qval + wt[p]) {
					pval = qval + wt[p];
				}
			}
			vset[0] = p;
			upper[0] = pval;
		}
	}
	*/
	// 그래프 input
	void graph(Scanner fp) {
		Vnbr = fp.nextInt();				// 정점수 입력
		Enbr = fp.nextInt();				// 변의 수 입력
		adj = new boolean[Vnbr+1][];
		nadj = new boolean[Vnbr+1][];
		wt = new int[Vnbr+1];
		for(int i = 0; i <= Vnbr; i++) {
			adj[i] = new boolean[Vnbr+1];
			nadj[i] = new boolean[Vnbr+1];
		}
		// empty graph 생성
		for(int i = 0; i < Vnbr; i++) {
			Arrays.fill(adj[i], false);
			Arrays.fill(nadj[i], true);
		}
		for(int i = 0; i < Vnbr; i++) {
			int weight = fp.nextInt();		// 가중치 입력
			int deg = fp.nextInt();			// 차수 입력
			wt[i] = weight;					// 정점 i의 가중치
			// 정점 i의 연결 정보
			for(int j = 0; j < deg; j++) {
				int entry = fp.nextInt();
				adj[i][entry] = true;
				nadj[i][entry] = false;
			}
			adj[i][Vnbr] = adj[Vnbr][i] = true;			// dummy정점 입력
			nadj[i][Vnbr] = nadj[Vnbr][i] = false;		// dummy정점 입력
		}
		wt[Vnbr] = 0;		// dummy 정점의 가중치 = 0
	}

}
