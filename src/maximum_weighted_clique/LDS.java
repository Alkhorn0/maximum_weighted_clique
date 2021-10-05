package maximum_weighted_clique;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class LDS {

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
    int a = 1;

    long startTime;
    static long clockPerSecond = 1000000000;

    void printRecord() {
        double time = 1.0* (System.nanoTime() - startTime) / (clockPerSecond);
        System.out.printf("%.5f, %d, %d, %s\n", time, recordWeight,cnt, Arrays.toString(record));
    }

    public static void main(String[] args) throws IOException {
        long TIME_LIMIT = 3600 * clockPerSecond;
        int switch_number = 0;

        Scanner scanner
        	= new Scanner(new BufferedReader(new FileReader("C:\\Users\\김영재\\1000_0.5_1_10.txt")));
        new LDS(scanner, TIME_LIMIT, switch_number);
    }

    /**
     *Ostergard �ɂ���@�\�t�g wclique.c �Ȃǂŗp���Ă���`���̃t�@�C
     *����ǂݍ��ݍœK�����o�͂���D
     * @param sc �f�[�^��ǂݍ��ނ��߂̃X�L���i�D
     * @param limit �������ԁD
     * @param switchNum ��̏�E�v�Z�@��؂�ւ��钸�_�����w�肷��D
     */
    public LDS(Scanner sc, long limit, int switchNum) throws IOException {
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

	// expand�ɂO����Vnbr��LDS������(5/16�p�])
	for(int i = 0; i <= Vnbr; i++) {
	    expand(Vnbr, vset, upper, i);
	}

	printRecord();
	System.out.println("------end--------");
    }

    /**
     * ���}
     * @param n �������̒��_��
     * @param vset �������̒��_�W��
     * @param upper ��E�̒l
     */

    //expand�Ɉ���d��ǉ�(5/16�p�])
    public void expand(int n, int[] vset, int[] upper, int d) {

        /*
         * 1000���1��C���Ԑ؂ꂩ�ǂ������`�F�b�N����
         * �i����s���ƌv�Z���Ԃɉe������j�D
         */
        if(++cnt % 1000 == 0) {
            if(System.nanoTime() >= deadline) {
                System.out.printf("time over\n");
                System.exit(0);
            }
        }

        //if( ((System.nanoTime() - startTime) / (clockPermilliSecond))/10 == a){
        //	a++;
        //	printRecord();
        //}

        // vset[i+1],..., vset[n-1] �̂����C
        // vset[i] �ɗאڂ�����̂̂ݍl����D
	// d�񂾂��E�Ɉړ�����(5/16�p�])
        for(int i = 0; i < d; ++i) {

            // ��E�e�X�g
            if(currentWeight + upper[i] <= recordWeight) {
                return;
            }

            // v ��I������ꍇ���l����B
            int v = vset[i];
            current[currentSize++] = v;
            currentWeight += wt[v];

            //System.out.println("aaa");

            // vset[i+1],..., vset[n-1] �̂����C
            // v �ɗאڂ��钸�_����Ȃ�n�� vset2 ��
            // ���B�v�f���͍��X n-i-1 �����A
            // �_�~�[�v�f�����邽�߂ɁA
            // n-i �������悤�ɂ���B
            int[] vset2 = new int[n-i];
            int n2 = 0;
            boolean[] adjv = adj[v];
            for(int j = i + 1; j < n; ++j) {
                if(adjv[vset[j]]) {
                    vset2[n2++] = vset[j];
                }
            }


            if(n2 == 0) { // ���}�ɂ�����u�t�v�̏���

                // �œK���̍X�V
                if(recordWeight < currentWeight) {
                    record = new int[currentSize];
                    System.arraycopy(current, 0, record, 0, currentSize);
                    recordWeight = currentWeight;
                    printRecord();
                }
            } else {

                // �ċA�Ăяo���D��E�v�Z�̕��@�͒��_���ɂ���Č��߂�D
                int[] upper2 = new int[n2+1];
                //if(n2 <= switch_number) {
                //    numberSort2(n2, vset2, upper2);
                //} else {
                numberSort(n2, vset2, upper2);
                //}
                if(i > 0) {
                	--d;
                }
                expand(n2, vset2, upper2, d);
                if(i > 0) {
                	++d;
            	}
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
