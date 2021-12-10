package maximum_weighted_clique;
import java.io.*;
import java.util.*;

public class LDSQueueSortPriority {

    static String progname = "Ky2memorySorting";
    long deadline;   // ��������
    long cnt = 0; // ���}��
    int Vnbr, Enbr;          /* number of vertices/edges */
    boolean[][] adj, nadj;
    int[] wt;
    int[] current;	// current clique
    int currentSize;
    int currentWeight;		// weight of current clique
    int[] record = new int[0]; /* best clique so far */
    int recordWeight;   /* weight of best clique */
    int switch_number;
    Queue vsetqueue = new LinkedList();   //부분문제를 넣는 큐
    Queue currentqueue = new LinkedList();  //현재 형성중인 클리크에 포함되는 정점을 넣는 큐
    int[] vsettmp;  //큐에서 빼낸 부분 문제를 넣는 배열
    int[] currenttmp;  //큐에서 빼낸 현재 형성중인 클리크에 포함되는 정점집합을 넣는 배열
    int a =1;
    
    long startTime;
    static long clockPerSecond = 1000000000;

    int[] vset3; // 실험
    
    void printRecord() {
        double time = 1.0* (System.nanoTime() - startTime) / (clockPerSecond);
        System.out.printf("%.5f,%d,%d\n", time, recordWeight,
                          cnt);
        ;
    }

    public static void main(String[] args) throws IOException {
        long TIME_LIMIT = 600 * clockPerSecond;
        int switch_number = 70;
        Scanner scanner
            = new Scanner(new BufferedReader(new FileReader("C:\\Users\\김영재\\1000_0.8_1_10.txt")));
        new LDSQueueSortPriority(scanner, TIME_LIMIT, switch_number);
    }

    /**
     *Ostergard �ɂ���@�\�t�g wclique.c �Ȃǂŗp���Ă���`���̃t�@�C
     *����ǂݍ��ݍœK�����o�͂���D
     * @param sc �f�[�^��ǂݍ��ނ��߂̃X�L���i�D
     * @param limit �������ԁD
     * @param switchNum ��̏�E�v�Z�@��؂�ւ��钸�_�����w�肷��D
     */
    public LDSQueueSortPriority(Scanner sc, long limit, int switchNum) throws IOException {
        this.switch_number = switchNum;
        graph(sc);

        /* "start clock" */
        startTime = System.nanoTime();

        // �e���_�̎������v�Z����B
        int[] degree = new int[Vnbr];
        for(int i = 0; i < Vnbr; ++i) {
            // ���_ i �̎����v�Z
            for(int j = 0; j < Vnbr; ++j) {
                if(adj[i][j]) {
                    ++degree[i];
                }
            }
        }

        // �����̍~���ɒ��_�� vset �ɓ����D
        // ���� numberSort �ŕ��בւ���̂ňӖ����Ȃ����������C
        // ���_�̏d�݂������Ƃ��͎����̍~���̕����኱���ƍl���Ă���D
        int[] vset = new int[Vnbr+1];
        for(int i = 0; i < Vnbr; ++i) {
            degree[i] = Vnbr * degree[i] + i;
        }
        Arrays.sort(degree);
        for(int i = 0; i < Vnbr; ++i) {
            vset[Vnbr - 1 - i] = degree[i] % Vnbr;
        }

        vset[Vnbr] = -1;  //정점 집합이 비어있는지 구분하기 위한 -1 대입
        //System.out.println(vset[Vnbr]);
        // ��E�����邽�߂̔z��Dvset �� upper �����_�����P����
        // �̂́C�_�~�[���_�i�d�݂O�őS���_�ɗאځj�����邷���ԁD
        // numberSort �̓_�~�[���_���g�����ԕ��@��p���Ă���C
        // ���ʂ̏�����������������̉񐔂����Ȃ��čς�ł���D
        int[] upper = new int[Vnbr+1];
        numberSort(Vnbr, vset, upper);

        /* main routine */
        current = new int[Vnbr+1];
        currentSize = 0;
        currentWeight = recordWeight = 0;
        deadline = limit + startTime;
        expand(Vnbr, vset, upper,current, degree);
        //printRecord();
        //System.out.println('x');
        // 큐에서 부분문제를 빼내어 LDS의 D=1에서 탐색진행
        while(true){
        	// 큐가 비면 종료
        	if(vsetqueue.peek() == null){
        		break;
        		}
        	
        	vsettmp = (int [])vsetqueue.poll(); // 부분문제를 빼냄
        	currenttmp = (int [])currentqueue.poll(); // 현재 형성중인 클리크를 빼냄
        	//System.out.print("  vsettmp = [");
        	int vsetnum = 0; //부분문제의 정점수
        	for(int i = 0; i < vsettmp.length; i++){
        		if(vsettmp[i] != -1 && vsettmp[i] != Vnbr){
        			vsetnum++; //부분 문제의 배열이 비어있지 않고, 더미 정점도 아니라면 + 1
        		}
        	}
        	
        	if(vsettmp[0] != Vnbr && vsettmp[0] != -1) { // 부분문제가 비어있지 않으면, 
        		if(currenttmp.length == 0){ // 현재 형성중인 클리크가 없다면, 클리크 사이즈와 가중치를 초기화
        			currentSize = 0;
        			currentWeight = 0;
        		}
        		//numberSort(vsetnum, vsettmp, upper);
        		//System.out.println(vsetqueue.size());
        		expand(vsetnum,vsettmp,upper,currenttmp, degree); // 부분 문제와 현재 형성중인 클리크를 입력
        	}
        }
        printRecord();
    }

    /**
     * ���}
     * @param n �������̒��_��
     * @param vset �������̒��_�W��
     * @param upper ��E�̒l
     */
    public void expand(int n, int[] vset, int[] upper, int[] currentx, int[] degree) {

        /*
         * 1000���1��C���Ԑ؂ꂩ�ǂ������`�F�b�N����
         * �i����s���ƌv�Z���Ԃɉe������j�D
         */
        if(++cnt % 1000 == 0) {
            if(System.nanoTime() >= deadline) {
                System.out.printf("%s,time over\n", progname);
                printRecord();
                System.exit(0);
            }
        }
        //if( ((System.nanoTime() - startTime) / (clockPermilliSecond))/10 == a){
        //	a++;
       // 	printRecord();
        //}

        if(currentx.length != 0) { //���݌`�����̃N���[�N���Ȃ���Έȉ������s
        	if(currentx.length != (Vnbr + 1)) { //1��ڂ�expand�ȊO�ȉ������s
        		currentSize = 0;
        		currentWeight = 0;
        	for(int i = 0; i < currentx.length; i++){
        		current[currentSize++] = currentx[i];
        		currentWeight += wt[currentx[i]];
        	}
        	}
        }


        // vset[i+1],..., vset[n-1] �̂����C
        // vset[i] �ɗאڂ�����̂̂ݍl����D
        for(int i = 0; i < 1; ++i) {

            // ��E�e�X�g
            if(currentWeight + upper[i] <= recordWeight) {
                return;
            }

            //System.out.println("aaa");
            // v ��I������ꍇ���l����B
            int v = vset[i];
            current[currentSize++] = v;
            currentWeight += wt[v];

            // vset[i+1],..., vset[n-1] �̂����C
            // v �ɗאڂ��钸�_����Ȃ�n�� vset2 ��
            // ���B�v�f���͍��X n-i-1 �����A
            // �_�~�[�v�f�����邽�߂ɁA
            // n-i �������悤�ɂ���B
            int[] vset2 = new int[n-i];
            Arrays.fill(vset2,-1);
            int n2 = 0;
            boolean[] adjv = adj[v];
            for(int j = i + 1; j < n; ++j) {
                if(adjv[vset[j]]) {
                    vset2[n2++] = vset[j];
                  
                }
            }
            //System.out.println("vset2.length = " + vset2.length);
            //System.out.println("n2 = " + n2);

            //vset����I�΂Ȃ��������_(vset[0]�ȊO�̒��_)��vset3�Ƃ���
            /*
            int[] vset3 = new int[vset.length - 1];
            Arrays.fill(vset3,-1);
            for(int k = 0; k < vset3.length; k++){
            	vset3[k] = vset[k + 1];
            	if(vset3[k] == -1) {
            		break;
            	}
            	for(int p = k-1; p >= 0; p--) {
            		int temp = vset3[p];
            		if ((wt[temp] < wt[vset3[k]])&&(degree[temp] < degree[vset3[k]])) {
            			vset3[p] = vset3[k];
            			vset3[k] = temp;
            		}
            	}
            }*/
            //System.out.println(vset.length);
            vset3 = new int[vset.length-1];
            if (vset.length > 1) {
            	
            	PriorityQueue<Integer> vset4 = new PriorityQueue<>(vset.length-1, new Comparator<Integer>() {
            		
            		@Override
            		public int compare(Integer o1, Integer o2) {
            			if((wt[o1] < wt[o2])&&(degree[o1] < degree[o2])) {
            				return -1;
            			}
            			//else if((wt[o1] > wt[o2])&&(degree[o1] > degree[o2])){
            				//return 1;
            			//}
            			return 0;
            		}
            	});
            	for(int k = 0; k < vset.length-1; k++) {
            		if(vset[k+1] == -1) break;
            		if(vset4.contains(0) && vset[k+1]==0) continue;		// 10/23 추가 
            		vset4.offer(vset[k+1]);
            	}
            	int size = vset4.size();
            	//System.out.println(vset4);
            	for(int t = 0; t < size; t++) { // 방안1. size제한 -> 값 대폭 손해
            		vset3[t] = vset4.poll();
            	}
            }
            //System.out.println(vset.length-1);
            
            //현재 형성중인 클리크를 current2라 한다.
            int[] current2 = new int[currentSize-1];
            for(int k = 0; k < current2.length; k++){
            	current2[k] = current[k];
            }

            if(vset3.length != 0) { //부분 문제의 정점집합이 있는 경우 이하를 실행
	            vsetqueue.offer(vset3);
	            currentqueue.offer(current2);
            }
        
            if(n2 == 0) { // 분기에서의 '잎'의 처리
                // 갱신작업
                if(recordWeight < currentWeight) {
                    record = new int[currentSize];
                    System.arraycopy(current, 0, record, 0, currentSize);
                    recordWeight = currentWeight;
                    printRecord();
                }
            } else {

                // 재귀호출 
                int[] upper2 = new int[n2+1];
                //if(n2 <= switch_number) {
                 //   numberSort2(n2, vset2, upper2);
                //} else {
                numberSort(n2, vset2, upper2);
                //}
                expand(n2, vset2, upper2, current, degree);
            }
            --currentSize;
            currentWeight -= wt[v];
        }
    }

    /**
     * <p> �Œ��H�����ł��邾���������Ȃ�悤�� seq ����בւ���D</p>
     * <p> �ł����l�̍������_�� seq[0]�C���l�̒Ⴂ���_�� seq[n-1] ��
     * ����Cseq[n] �ɂ̓_�~�[���_������D</p>
     *

     �D�e���_���n�_�Ƃ���Œ��H����
     * upper �ɓ���D
     */
    public void numberSort(int n, int[] seq, int[] upper) {

        // upper[] ������������B
        for(int i = 0; i < n; ++i) {
            upper[i] = wt[seq[i]];
        }
        for(int i = n - 1; i >= 0; --i) {

            // seq[0]�`seq[i] �̒�����ŏ��̒l������
            // �v�f seq[s] �����o���D
            int s = i;
            int sval = upper[s];
            for(int p = i - 1; p >= 0; --p) {
                int pval = upper[p];
                if(pval < sval) {
                    s = p;
                    sval = pval;
                }
            }
            // seq[i] �� seq[s] ����������D
            // seq[i]�́C�O���珇�ɁC���m��Cseq[s]�C
            // �m��ς݁C�ƕ���
            int u  = seq[s];
            seq[s] = seq[i];
            seq[i] = u;
            upper[s] = upper[i];
            upper[i] = sval;

            // u (=seq[i]) �ɗאڂ���v�f�̒l���X�V����D
            for(int j = i - 1; j >= 0; --j) {
                int v = seq[j];
                if(adj[u][v]) {
                    upper[j] = sval + wt[v];
                }
            }
        }
    }

    /**
     * �Œ��H�����v�Z����Dseq �ɂ����ėאڂ���Q���_
     * ���������������ǂ��ꍇ�͌�������D�e���_���n�_
     * �Ƃ���Œ��H���� upper �ɓ���DnumberSort ��荂���D
     */
    public void numberSort2(int n, int[] seq, int[] upper) {

        // �_�~�[���_��}������B
        seq[n] = Vnbr;
        upper[n] = 0;

        // ��ڂ̌��ł��� p ���Z�b�g����B
        int p = seq[n-1];
        int pval = wt[p];

        for(int i = n - 1; i >= 1; --i) {
            // ��ڂ̌��ł��� q ���Z�b�g����B
            // q �ɗאڂ��钸�_��T���A��E���v�Z����B
            int q = seq[i-1];
            boolean[] nadjq = nadj[q];

            int j = i;
            while(nadjq[seq[++j]]);
            int qval = upper[j] + wt[q];

            // �l�̏��������� seq[i] �ɓ����B
            if(pval < qval) {

                // p ��}������B
                for(j = i + 1; pval < upper[j]; ++j) {
                    seq[j-1] = seq[j];
                    upper[j-1] = upper[j];
                }
                seq[j-1] = p;
                upper[j-1] = pval;

                // pval ���X�V����B
                pval = (!nadjq[p]) && qval < pval + wt[q] ? pval + wt[q] : qval;
                p = q;

            } else {
                // q ��}������B
                for(j = i + 1; qval < upper[j]; ++j) {
                    seq[j-1] = seq[j];
                    upper[j-1] = upper[j];
                }
                seq[j-1] = q;
                upper[j-1] = qval;

                // pval ���X�V����B
                if((!nadjq[p]) && pval < qval + wt[p]) {
                    pval = qval + wt[p];
                }
            }
        }
        seq[0] = p;
        upper[0] = pval;
    }

    /**
     * <p> �X�L���i����i�󔒋�؂�́j���L�̏����̃e�L�X�g�t�@�C����ǂݍ��݁C�אڍs��Ƃ��̕�O���t�C���_�̏d�݂𓾂�D</p>
     * <p> �P�s�ڂɂ͒��_���C�Ӑ� </p>
     * <p> �Q�s�ڂɂ͒��_�ԍ��O�̏d�݁C�����C�אڒ��_�i�̗񋓁j </p>
     * <p> �R�s�ڂɂ͒��_�ԍ��P�̏d�݁C�����C�אڒ��_�i�̗񋓁j </p>
     * <p> �ȉ����l�D</p>
     * <p> n+1�s�ڂɂ͒��_�ԍ�n-1�̏d�݁C�����C�אڒ��_�i�̗񋓁j </p>
     * <p> �O���t�ɂ̓_�~�[���_�������D�_�~�[���_�� v_0,v_1,...,v_{n-1}�Ɨאڂ���D�d�݂͂O�ł���D</p>
     */

    public void graph(Scanner fp) {
        Vnbr = fp.nextInt();
        Enbr = fp.nextInt();
        adj = new boolean[Vnbr+1][];
        nadj = new boolean[Vnbr+1][];
        wt = new int[Vnbr+1];
        for(int i = 0; i <= Vnbr; ++i) {
            adj[i] = new boolean[Vnbr+1];
            nadj[i] = new boolean[Vnbr+1];
        }
        // create empty graph
        for(int i=0;i<Vnbr;i++) {
            Arrays.fill(adj[i], false);
            Arrays.fill(nadj[i], true);
        }
        for(int i=0;i<Vnbr;i++) {
            int weight = fp.nextInt();
            int deg = fp.nextInt();
            wt[i] = weight;
            for(int j=0;j<deg;j++) {
                int entry = fp.nextInt();
                adj[i][entry] = true;
                nadj[i][entry] = false;
            }
            adj[i][Vnbr] = adj[Vnbr][i] = true;
            nadj[i][Vnbr] = nadj[Vnbr][i] = false;
        }
        wt[Vnbr] = 0; // dummy vertex
    }
}
