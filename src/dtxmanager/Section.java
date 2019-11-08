package dtxmanager;

import java.io.*;

public class Section implements Cloneable, Serializable {

    /**
     * Data section
     */
    static final int COMMON_SECTION = 0;
    /**
     * INIT section
     */
    static final int INIT_SECTION = 1;
    /**
     * RUN section
     */
    static final int RUN_SECTION = 2;
    /**
     * RUN+INIT section
     */
    static final int RUNINIT_SECTION = 3;
    /**
     * MOVEBLOCK special section
     */
    static final int MOVEBLOCK_SECTION = 4;

    /**
     * Strings for section types
     */
    static final String[] SECTION_TYPE_STRINGS = {"DATA", "INIT", "RUN", "RUNINIT", "MVB"};

    /**
     * Start address
     */
    int start;
    /**
     * End address
     */
    int stop;
    /**
     * Section type
     */
    int type;
    /**
     * Jump address for RUN and INIT
     */
    int jump;
    /**
     * Second jump address for RUN+INIT
     */
    int jump2;

    int mvbStart;
    int mvbEnd;
    int mvbTarget;

    /**
     * Data of the section
     */
    int[] data;

    /**
     * Comment
     */
    String comment;

    /**
     * Create new section
     *
     * @param s First address
     * @param st Last address
     * @param t Section type
     */
    Section(int s, int st, int t) {
        this(s, st, t, 0, 0);
    }

    /**
     * Nova instance
     *
     * @param s First address
     * @param st Last address
     * @param t Section type
     * @param j Jump address
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
     * Get string representation of a Section
     *
     * @return String representation
     */
    public String toString() {
        String s1 = Integer.toString(start);
        String s2 = Integer.toString(stop);
        String s4 = SECTION_TYPE_STRINGS[type];
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
     * Create deep copy
     */
    public Object clone() throws CloneNotSupportedException {
        Section r = new Section(this.start, this.stop, this.type, this.jump, this.jump2);
        System.arraycopy(data, 0, r.data, 0, data.length);
        r.comment = comment;
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
