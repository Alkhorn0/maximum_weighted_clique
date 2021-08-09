package maximum_weighted_clique;
import java.util.*;
public class clique {
	int Vnbr, Enbr;				// ������ ��, ���� ��
	boolean[][] adj, nadj;		// �������, ���׷���
	int[] wt;					// ������ ����ġ ����Ʈ
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
			wt[i] = weight;
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
