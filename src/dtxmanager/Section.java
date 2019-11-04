package dtxmanager;

import java.io.*;

public class Section implements Cloneable, Serializable {

    /**
     * Datova sekce
     */
    static final int COMMON_SECTION = 0;
    /**
     * INIT SEKCE
     */
    static final int INIT_SECTION = 1;
    /**
     * RUN SEKCE
     */
    static final int RUN_SECTION = 2;
    /**
     * RUN/INIT SEKCE
     */
    static final int RUNINIT_SECTION = 3;
    /**
     * MOVEBLOCK sekce
     */
    static final int MOVEBLOCK_SECTION = 4;

    /**
     * Retezcove oznaceni typu sekci
     */
    static final String[] typeStrings = {"DATA", "INIT", "RUN", "RUNINIT", "MVB"};

    /**
     * Pocatecni adresa
     */
    int start;
    /**
     * Koncova adresa
     */
    int stop;
    /**
     * Typ sekce
     */
    int type;
    /**
     * Adresa odskoku pro inicializacni a spousteci sekce
     */
    int jump;
    /**
     * Adresa odskoku 2, jen pro RUN/INIT sekce
     */
    int jump2;

    int mvbStart;
    int mvbEnd;
    int mvbTarget;

    /**
     * Data sekce
     */
    int[] data;

    /**
     * Komentar
     */
    String comment;

    /**
     * Nova instance
     *
     * @param s Pocatecni adresa sekce
     * @param st Koncova adresa sekce
     * @param t Typ sekce
     */
    Section(int s, int st, int t) {
        this(s, st, t, 0, 0);
    }

    /**
     * Nova instance
     *
     * @param s Pocatecni adresa sekce
     * @param st Koncova adresa sekce
     * @param t Typ sekce
     * @param j Skokova adresa
     */
    Section(int s, int st, int t, int j) {
        this(s, st, t, j, 0);
    }

    Section(int s, int st, int t, int j, int j2) {
        start = s;
        stop = st;
        type = t;
        jump = j;
        jump2 = j2;
        data = new int[st - s + 1];
        this.comment = "";
    }

    /**
     * retezcova reprezentace sekce
     *
     * @return retezcova reprezentace
     */
    public String toString() {
        String s1 = Integer.toString(start);
        String s2 = Integer.toString(stop);
        String s4 = typeStrings[type];
        String s3 = "";
        switch (this.type) {
            case (Section.INIT_SECTION): {
                s3 = Integer.toString(jump);
                break;
            }
            case (Section.RUN_SECTION): {
                s3 = Integer.toString(jump);
                break;
            }
            case (Section.RUNINIT_SECTION): {
                s3 = Integer.toString(jump);
                s3 += ",";
                s3 += Integer.toString(jump2);
            }

        }

        return s4 + ": " + s1 + "-" + s2 + " " + s3 + " (" + comment + ")";
    }

    /**
     * Klonovani sekce. Hluboka kopie
     */
    public Object clone() {
        Section r = new Section(this.start, this.stop, this.type, this.jump, this.jump2);
        for (int i = 0; i < data.length; i++) {
            r.data[i] = data[i];
        }
        r.comment = new String(comment);
        return r;
    }

    public int getDataLength() {
        return data.length;
    }

    public void setMoveParameters(int s, int e, int t) {
        mvbStart = s;
        mvbEnd = e;
        mvbTarget = t;
    }

    public String getDataAsCArray() {
        StringBuffer sb = new StringBuffer();
        String lnsp = System.getProperty("line.separator");
        sb.append("{");
        for (int i = 0; i < this.data.length; i++) {
            sb.append("0x");
            if (data[i] < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(data[i]));
            if (i != data.length - 1) {
                sb.append(',');
            }
            if (i != 0 && i % 32 == 0) {
                sb.append(lnsp);
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
