package maximum_weighted_clique;
import java.util.*;
public class clique {
	int Vnbr, Enbr;				// 정점의 수, 변의 수
	boolean[][] adj, nadj;		// 인접행렬, 보그래프
	int[] wt;					// 정점의 가중치 리스트
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
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
			wt[i] = weight;
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
