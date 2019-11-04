package dtxmanager;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

class AtariExecutableException extends Exception {

    /**
     * Zprava
     */
    private String msg = "";

    public AtariExecutableException(String msg) {
        this.msg = msg;
    }

    /**
     * Pozadavek na vraceni zpravy
     *
     * @return Zprava
     */
    public String getMessage() {
        return msg;
    }

    public String toString() {
        return "AtariExecutableException";
    }
}

/**
 * Vicesekcni format Atari executable.<BR>
 * Stary format z pocatku osmdesatych let pouzivany systemem ATARI DOS. Tato
 * trida umi soubory tohoto formatu nacitat, analyzovat a konvertovat vicesekcni
 * soubory na jednosekcni, pokud se sekce neprekryvaji.<BR>
 *
 * Po prevodu na jednu sekci je doplnen kod pro provedeni odskoku
 */
public class AtariExecutable extends AbstractTableModel {

    /**
     * Jmeno souboru na disku
     */
    private String filename;
    /**
     * Delka pridavneho kodu
     */
    private int extraLen;

    /**
     * Seznam vsech sekci
     */
    private ArrayList allSections;

    /**
     * Nova instance
     */
    public AtariExecutable(String _filename) {
        filename = _filename;
        allSections = new ArrayList();
        this.fireTableDataChanged();
        extraLen = 0;
    }

    /**
     * Delka pridavneho kodu
     *
     * @return Delka specialniho kodu v bajtech
     */
    int getExtraCodeLength() {
        int l = allSections.size();
        int cnt = 0;
        Section s;
        for (int i = 0; i < l; i++) {
            s = (Section) allSections.get(i);
            if (s.type == Section.RUN_SECTION || s.type == Section.INIT_SECTION) {
                cnt += 3;
                continue;
            }
            if (s.type == Section.RUNINIT_SECTION) {
                cnt += 6;
            }

        }
        return cnt;
    }

    /**
     * Vrati seznam sekci jako pole retezcu
     *
     * @return Pole retezcu
     */
    public String[] listing() {
        /*Pole retezcu*/
        String[] s = new String[allSections.size()];
        for (int i = 0; i < allSections.size(); i++) {
            s[i] = ((Section) (allSections.get(i))).toString();
        }
        return s;
    }

    /**
     * Analyza souboru. Zjistuje se rozlozeni a typ sekci.
     *
     * @throws IOException,AtariExecutableException
     */
    public void analyze(boolean full) throws IOException, AtariExecutableException {

        Vector v = new Vector();

        RandomAccessFile raf;
        raf = new RandomAccessFile(filename, "r");

        /*Nacteni vsech dat ze souboru*/
        byte[] filebData = new byte[(int) raf.length()];
        raf.readFully(filebData);
        raf.close();

        /*Konverze z pole bajtu na pole intu. Skoda ze Java nema neznamenkove
         *typy,ale neda se nic delat*/
        int[] fileData = new int[filebData.length];
        byte b;
        for (int i = 0; i < fileData.length; i++) {
            /*Orezani znamenka*/
            b = filebData[i];
            fileData[i] = (b < 0) ? b + 256 : b;
        }

        int pos = 0;
        int size = filebData.length;
        int b1, b2, b3, b4, b5;
        int w1, w2, w3, w4, w5, w6;

        /*Zahajeni analyzy*/
 /*Prvni dva bajty musi byt 255*/
        if (full == true) {

            if (fileData[0] != 255 || fileData[1] != 255) {
                throw new AtariExecutableException("No 255 255 header");

            }

            pos = 2;
        }
        else {
            pos = 0;
        }

        /*Sekce musi mit alespon 3 byty*/
        while (size - pos > 3) {

            /*Je zde 255 255 ?*/
            b1 = fileData[pos];
            b2 = fileData[pos + 1];

            /*Jestlize ano, tak se inkrementuje pozice, jinak nic*/
            if (b1 == 255 && b2 == 255) {
                pos += 2;
            }

            /*pos ukazuje na prvni bajt sekce - zde je jeji velikost*/
            b1 = fileData[pos];
            pos++;
            b2 = fileData[pos];
            pos++;
            w1 = b2 * 256 + b1;   //w1 obsahuje  pocatecni adresu sekce
            b1 = fileData[pos];
            pos++;
            b2 = fileData[pos];
            pos++;
            w2 = b2 * 256 + b1;   //w2 obsahuje koncovou adresu sekce

            w3 = w2 - w1 + 1;     //w3 obsahuje velikost sekce

            /*Je to init ?*/
            if (w3 == 2 && w1 == 738 && w2 == 739) {
                /*Nacti dva bajty, ukazuji kam se ma skocit na init*/
                b1 = fileData[pos];
                pos++;
                b2 = fileData[pos];
                pos++;
                v.add(new Integer(pos - 1));
                w4 = b2 * 256 + b1;
                Section ns = new Section(w1, w2, Section.INIT_SECTION, w4);
                this.allSections.add(ns);
                continue;
            }

            /*Je to run ?*/
            if (w3 == 2 && w1 == 736 && w2 == 737) {
                /*Nacti dva bajty, ukazuji kam se ma skocit na init*/
                b1 = fileData[pos];
                pos++;
                b2 = fileData[pos];
                pos++;
                w4 = b2 * 256 + b1;
                Section ns = new Section(w1, w2, Section.RUN_SECTION, w4);
                this.allSections.add(ns);
                continue;
            }

            /*Je to oboji sekce- jak init, tak run*/
            if (w3 == 4 && w1 == 736 && w2 == 739) {
                /*Nacti dva bajty, ukazuji kam se ma skocit na init*/
                b1 = fileData[pos];
                pos++;
                b2 = fileData[pos];
                pos++;
                w4 = b2 * 256 + b1;
                int bkp = w4;
                b1 = fileData[pos];
                pos++;
                b2 = fileData[pos];
                pos++;
                v.add(new Integer(pos - 1));
                w4 = b2 * 256 + b1;
                Section ens = new Section(w1, w2, Section.RUNINIT_SECTION, bkp, w4);
                this.allSections.add(ens);
                continue;

            }

            /*Je to obycejna sekce, udela se zaznam do pole a pokracuje se*/
            Section s;
            s = new Section(w1, w2, Section.COMMON_SECTION);
            this.allSections.add(s);
            for (int i = 0; i < w3; i++) {
                s.data[i] = fileData[pos + i];
            }
            pos += w3;

        }/*Konec while*/

        this.fireTableDataChanged();
    }

    /**
     * Vylistuje sekce souboru na standardni vystup
     */
    void listToConsole() {
        for (int i = 0; i < allSections.size(); i++) {
            System.out.println(allSections.get(i).toString());
        }
    }

    /**
     * Exportuje sekci do externiho souboru s hlavickou nebo bez
     */
    void exportSection(int index, String fname, boolean header) throws Exception {

        Section sect = (Section) allSections.get(index);

        /*Soubor jen pro cteni*/
        RandomAccessFile raf = new RandomAccessFile(fname, "rw");

        if (header == true) {
            raf.writeByte(255);
            raf.writeByte(255);
            raf.writeByte(sect.start % 256);
            raf.writeByte(sect.start / 256);
            raf.writeByte(sect.stop % 256);
            raf.writeByte(sect.stop / 256);
        }

        for (int i = 0; i < sect.data.length; i++) {
            raf.writeByte(sect.data[i]);
        }
        raf.close();

    }

    /**
     * posun dane sekce blize k pocatku
     */
    int moveUp(int index, int step) {

        if (index > 0) {
            if (index - step < 0) {
                step = index;
            }
            Section mvs = (Section) allSections.get(index);
            allSections.remove(index);
            allSections.add(index - step, mvs);

            this.fireTableDataChanged();
            return index - step;
        }
        else {
            return 0;
        }
    }

    /**
     * posun dane sekce blize ke konci
     */
    int moveDown(int index, int step) {
        if (index < allSections.size() - 1) {
            if (index + step > allSections.size() - 1) {
                step = allSections.size() - 1 - index;
            }
            Section mvs = (Section) allSections.get(index);
            allSections.add(index + step + 1, mvs);
            allSections.remove(index);
            this.fireTableDataChanged();
            return index + step;
        }
        else {
            return index;
        }
    }

    /*Smazani sekce*/
    void deleteSection(int index) {
        if (index >= 0 & index < allSections.size()) {
            allSections.remove(index);
        }
        this.fireTableDataChanged();
    }

    /*Pridani nove sekce*/
    void addSection(Section s) {
        this.allSections.add(s);
        this.fireTableDataChanged();
    }

    void addSection(Section s, int idx) {
        if (idx > allSections.size()) {
            idx = allSections.size();
        }
        if (idx < 0) {
            idx = 0;
        }
        this.allSections.add(idx, s);
        this.fireTableDataChanged();
    }

    /*Ulozeni do souboru*/
    void saveFile(String fname, int[] scindices, boolean hdr) throws Exception {

        /*Otevrit soubor*/
        RandomAccessFile raf = new RandomAccessFile(fname, "rw");
        raf.setLength(0);
        /*Zapsat 255 255*/
        if (hdr == true) {
            raf.writeByte(255);
            raf.writeByte(255);
        }
        /*Vzit vsechny sekce a pozapisovat*/
        for (int i = 0; i < scindices.length; i++) {
            Section s = (Section) allSections.get(scindices[i]);
            switch (s.type) {
                case (Section.MOVEBLOCK_SECTION):
                case (Section.COMMON_SECTION): {
                    /*adr*/
                    if (hdr == true) {
                        raf.writeByte(s.start % 256);
                        raf.writeByte(s.start / 256);
                        raf.writeByte(s.stop % 256);
                        raf.writeByte(s.stop / 256);
                    }
                    /*data*/
                    for (int k = 0; k < s.data.length; k++) {
                        raf.writeByte(s.data[k]);
                    }
                    break;
                }
                case (Section.INIT_SECTION): {
                    /*adr*/
                    if (hdr == true) {
                        raf.writeByte(738 % 256);
                        raf.writeByte(738 / 256);
                        raf.writeByte(739 % 256);
                        raf.writeByte(739 / 256);
                    }
                    /*jmp data*/
                    raf.writeByte(s.jump % 256);
                    raf.writeByte(s.jump / 256);
                    break;
                }
                case (Section.RUN_SECTION): {
                    /*adr*/
                    if (hdr == true) {
                        raf.writeByte(736 % 256);
                        raf.writeByte(736 / 256);
                        raf.writeByte(737 % 256);
                        raf.writeByte(737 / 256);
                    }
                    /*jmp data*/
                    raf.writeByte(s.jump % 256);
                    raf.writeByte(s.jump / 256);
                    break;
                }
                case (Section.RUNINIT_SECTION): {
                    /*adr*/
                    if (hdr == true) {
                        raf.writeByte(736 % 256);
                        raf.writeByte(736 / 256);
                        raf.writeByte(739 % 256);
                        raf.writeByte(739 / 256);
                    }
                    /*jmp data*/
                    raf.writeByte(s.jump % 256);
                    raf.writeByte(s.jump / 256);
                    raf.writeByte(s.jump2 % 256);
                    raf.writeByte(s.jump2 / 256);
                    break;
                }
            }

        }
        raf.close();

    }

    public Section getSection(int idx) {
        return (Section) allSections.get(idx);
    }

    public int getSectionCount() {
        return allSections.size();
    }

    public Section createCodeFrom(int idx) {
        Section rs = new Section(40000, 40000, Section.COMMON_SECTION, 0, 0);
        Section ss = getSection(idx);

        Vector dta = new Vector();
        dta.add(new Integer(72));
        /*A do zasobniku*/

 /*Hledame hodnoty 0-255*/
        for (int v = 0; v < 256; v++) {
            boolean first = true;
            for (int a = ss.start; a <= ss.stop; a++) {
                if (ss.data[a - ss.start] == v) {
                    if (first == true) {
                        dta.add(new Integer(169));
                        /*LDA #*/
                        dta.add(new Integer(v));
                        first = false;
                    }
                    dta.add(new Integer(141));
                    /*STA*/
                    dta.add(new Integer(a % 256));
                    dta.add(new Integer(a / 256));
                }
            }
        }

        dta.add(new Integer(104));
        /*PLA*/
        dta.add(new Integer(96));
        /*RTS*/

        int[] data = new int[dta.size()];
        for (int k = 0; k < dta.size(); k++) {
            data[k] = ((Integer) (dta.elementAt(k))).intValue();
        }

        rs.comment = "Gns:";
        rs.comment += Integer.toString(ss.start);
        rs.comment += "-";
        rs.comment += Integer.toString(ss.stop);
        rs.stop = 40000 + dta.size() - 1;
        rs.data = data;

        return rs;
    }

    void importRawSection(String fname) throws Exception {
        Section s = new Section(32767, 32767, Section.COMMON_SECTION, 0, 0);
        File f = new File(fname);
        s.comment = f.getName();
        RandomAccessFile raf = new RandomAccessFile(fname, "r");
        int l = (int) raf.length();
        s.data = new int[l];
        for (int p = 0; p < l; p++) {
            s.data[p] = raf.read();
        }
        raf.close();
        s.stop = s.start + l - 1;
        addSection(s);
    }

    /**
     * Importuje data opatrena hlavickami
     */
    void importHeaderedData(String fname) throws Exception {
        AtariExecutable ax = new AtariExecutable(fname);
        File f = new File(fname);
        String fnm = f.getName();
        ax.analyze(false);
        for (int i = 0; i < ax.getSectionCount(); i++) {
            Section s = (Section) ax.getSection(i).clone();
            s.comment = new String(fnm);
            this.addSection(s);
        }
    }

    /**
     * Prida sekci pro presun bloku pameti
     */
    Section createMoveBlockSection(int stadr, int source, int target, int length) {
        Section s = new Section(stadr, stadr + moveblock.length - 1 - 6, Section.MOVEBLOCK_SECTION);
        s.setMoveParameters(source, source + length - 1, target);
        /*Nastavit adresy*/
        moveblock[IDX_SRC_LO] = source % 256;
        moveblock[IDX_SRC_HI] = source / 256;
        moveblock[IDX_TGT_LO] = target % 256;
        moveblock[IDX_TGT_HI] = target / 256;
        /*Delka se musi o jednu snizit*/
        length--;
        moveblock[IDX_LEN_LO] = length % 256;
        moveblock[IDX_LEN_HI] = length / 256;
        s.data = new int[moveblock.length - 6];
        /*Kopirovat obsah sekce*/
        for (int i = 6; i < moveblock.length; i++) {
            s.data[i - 6] = moveblock[i];
        }
        /*Komentar*/
        StringBuffer sb = new StringBuffer("MV:");
        sb.append(source);
        sb.append("-");
        sb.append(source + length);
        sb.append(">");
        sb.append(target);
        s.comment = sb.toString();
        return s;
    }

    void addMoveBlockSection(int stadr, int source, int target, int length) {
        addSection(createMoveBlockSection(stadr, source, target, length));
    }

    /**
     * Rozdeleni sekce
     */
    void splitSection(int idx, int fosp) throws AtariExecutableException {
        Section s = getSection(idx);
        /*Kontrola rozsahu*/

        if (s.type == Section.COMMON_SECTION) {
            if (fosp <= s.start || fosp > s.stop) {
                throw new AtariExecutableException("First address of second part out of range");

            }
            /*Rozsah je v poradku, uprava puvodni sekce*/
            Section sps = new Section(fosp, s.stop, Section.COMMON_SECTION);
            /*Kopirovani dat do druhe sekce*/
            sps.data = new int[s.stop - fosp + 1];
            for (int i = fosp - s.start; i < s.stop - s.start + 1; i++) {
                sps.data[i - fosp + s.start] = s.data[i];
            }
            Section s3 = (Section) s.clone();
            s.data = new int[fosp - 1 - s.start + 1];
            s.stop = fosp - 1;
            for (int i = 0; i < fosp - 1 - s.start + 1; i++) {
                s.data[i] = s3.data[i];
            }
            s3 = null;
            addSection(sps, idx + 1);
        }
        else {
            if (s.type == Section.RUNINIT_SECTION) {
                s.type = Section.RUN_SECTION;
                Section si = new Section(738, 739, Section.INIT_SECTION, s.jump2);
                s.jump2 = 0;
                allSections.add(idx + 1, si);
                this.fireTableDataChanged();

            }
            else {
                throw new AtariExecutableException("Attemp to split RUN or INIT section");
            }
        }

    }

    /*TABLE MODEL METHODS*/
    public Object getValueAt(int y, int x) {
        Section s = getSection(y);

        /*Obrazek*/
        if (x == 0) {
            return AtariExecutable.iconTypes[s.type];
        }

        /*TYP SEKCE*/
        if (x == 1) {
            return Section.typeStrings[s.type];
        }

        /*Prvni adresa*/
        if (x == 2) {
            if (s.type == Section.COMMON_SECTION || s.type == Section.MOVEBLOCK_SECTION) {
                return Integer.toString(s.start);
            }
            else {
                return Integer.toString(s.jump);
            }

        }
        /*Druha adresa*/
        if (x == 3) {
            if (s.type == Section.COMMON_SECTION || s.type == Section.MOVEBLOCK_SECTION) {
                return Integer.toString(s.stop);
            }
            if (s.type == Section.RUNINIT_SECTION) {
                return Integer.toString(s.jump2);
            }
            return "";
        }
        /*Komentar*/
        if (x == 4) {
            return s.comment;
        }

        return "INVALID";
    }

    public int getColumnCount() {
        return 5;
    }

    public int getRowCount() {
        return getSectionCount();
    }

    public String getColumnName(int i) {
        switch (i) {
            case 0:
                return "X";
            case 1:
                return "TYPE";
            case 2:
                return "ADR1";
            case 3:
                return "ADR2";
            case 4:
                return "CMT";
        }
        return "";
    }

    void clear() {
        this.allSections.clear();
        fireTableDataChanged();
        filename = "";
        extraLen = 0;
    }

    void setFileName(String fn) {
        filename = fn;
    }

    void setComment(int idx, String cmt) {
        getSection(idx).comment = cmt;
        fireTableDataChanged();
    }

    public static ImageIcon[] iconTypes;

    public void initIcons() {

        iconTypes = new ImageIcon[5];

        iconTypes[0] = new javax.swing.ImageIcon(getClass().getResource("/dtxmanager/data.png"));
        iconTypes[2] = new javax.swing.ImageIcon(getClass().getResource("/dtxmanager/run.png"));
        iconTypes[1] = new javax.swing.ImageIcon(getClass().getResource("/dtxmanager/init.png"));
        iconTypes[3] = new javax.swing.ImageIcon(getClass().getResource("/dtxmanager/runinit.png"));
        iconTypes[4] = new javax.swing.ImageIcon(getClass().getResource("/dtxmanager/mvb.png"));
    }

    public Class getColumnClass(int idx) {
        if (idx == 0) {
            return javax.swing.ImageIcon.class;
        }
        else {
            return String.class;
        }

    }

    /*Kod pro presun bloku pameti*/
    static int moveblock[] = {
        0xFF, 0xFF, 0x0, 0x20, 0x84, 0x20, 0x48, 0x8, 0x78, 0xAD, 0xE, 0xD4, 0x48, 0xA9, 0x0, 0x8D, 0xE, 0xD4, 0xAD, 0x1, 0xD3, 0x48, 0x29, 0xFE, 0x8D, 0x1, 0xD3, 0xA5, 0x80, 0x8D, 0xFA, 0xFF,
         0xA5, 0x81, 0x8D, 0xFB, 0xFF, 0xA5, 0x82, 0x8D, 0xFE, 0xFF, 0xA5, 0x83, 0x8D, 0xFF, 0xFF, 0xA9, 0x47, 0x85, 0x80, 0xA9, 0x59, 0x85, 0x81, 0xA9, 0x2, 0x8D, 0xFC, 0xFF, 0xA9, 0x1, 0x8D, 0xFD,
         0xFF, 0xA9, 0x0, 0x85, 0x82, 0xA9, 0x40, 0x85, 0x83, 0xA0, 0x0, 0xB1, 0x80, 0x91, 0x82, 0xCE, 0xFC, 0xFF, 0xAE, 0xFC, 0xFF, 0xE0, 0xFF, 0xD0, 0xA, 0xCE, 0xFD, 0xFF, 0xAE, 0xFD, 0xFF, 0xE0,
         0xFF, 0xF0, 0x9, 0xC8, 0xD0, 0xE5, 0xE6, 0x81, 0xE6, 0x83, 0xD0, 0xDF, 0x68, 0x8D, 0x1, 0xD3, 0x68, 0x8D, 0xE, 0xD4, 0xAD, 0xFA, 0xFF, 0x85, 0x80, 0xAD, 0xFB, 0xFF, 0x85, 0x81, 0xAD, 0xFE,
         0xFF, 0x85, 0x82, 0xAD, 0xFF, 0xFF, 0x85, 0x83, 0x28, 0x68, 0x60
    };

    static final int IDX_SRC_LO = 48;
    static final int IDX_SRC_HI = 52;
    static final int IDX_TGT_LO = 66;
    static final int IDX_TGT_HI = 70;
    static final int IDX_LEN_LO = 56;
    static final int IDX_LEN_HI = 61;

    /*Purifikace spustitelneho souboru*/
    void purify() {

        /*Nejprve najit rozdelit vsechny RUNINIT sekce*/
        int l = this.allSections.size();
        Section s;
        for (int i = 0; i < l; i++) {
            s = (Section) this.allSections.get(i);
            if (s.type == Section.RUNINIT_SECTION) {
                s.type = Section.RUN_SECTION;
                Section si = new Section(738, 739, Section.INIT_SECTION, s.jump2);
                this.allSections.add(i + 1, si);
                s.jump2 = 0;
            }
        }

        /*Presunout vsechny RUN sekce na konec*/
        ArrayList al = new ArrayList();
        for (int i = 0; i < allSections.size(); i++) {
            s = (Section) this.allSections.get(i);
            if (s.type == Section.RUN_SECTION) {
                this.allSections.remove(i);
                al.add(s);
                i--;
            }
        }
        l = al.size();
        for (int i = 0; i < l; i++) {
            this.allSections.add((Section) al.get(i));
        }

        /*Data se zmenila*/
        this.fireTableDataChanged();
    }

    public ArrayList getAllSections() {
        return allSections;
    }

    public void setAllSections(ArrayList al) {
        allSections = al;
        this.fireTableDataChanged();
    }

    /**
     * Vytvori turbo verzi ze souboru
     */
    public void makeTurboVersion(int extraAddress, boolean extraCode, String outFile) throws AtariExecutableException {

        int originalRunAdr = -1;

        int firstAdr = Integer.MAX_VALUE;
        int lastAdr = Integer.MIN_VALUE;

        /*Kontrola na runinit*/
        int l = allSections.size();
        Section s;
        long totalSize = 0;

        for (int i = 0; i < l; i++) {
            s = (Section) allSections.get(i);
            /*Kontrola na RUNINIT*/
            if (s.type == Section.RUNINIT_SECTION) {
                throw new AtariExecutableException("File contains RUNINIT section");
            }
            /*Kontrola, RUN musi byt posledni*/
            if (s.type == Section.RUN_SECTION) {
                if (i != l - 1) {
                    throw new AtariExecutableException("RUN section is not last");
                }
                originalRunAdr = s.jump;
            }

            /*Rozsah min,max adresa*/
            if (s.type == Section.COMMON_SECTION || s.type == Section.MOVEBLOCK_SECTION) {
                if (s.start < firstAdr) {
                    firstAdr = s.start;
                }
                if (s.stop > lastAdr) {
                    lastAdr = s.stop;
                }
                totalSize += s.getDataLength();
            }

        }

        /*Kontrola velikosti*/
        if (totalSize > 65535) {
            throw new AtariExecutableException("File size exceeds 65535 bytes");
        }

        if (totalSize < 1) {
            throw new AtariExecutableException("File has no DATA sections");
        }

        /*Kontrola, jestli pridavny kod neovlivni min a max adresu*/
        if (extraCode == true) {

            if (extraAddress < 0 || extraAddress > 65535) {
                throw new AtariExecutableException("Extra code address exceeds 0-65535");
            }

            if (extraAddress < firstAdr) {
                firstAdr = extraAddress;
            }
            if (extraAddress > lastAdr) {
                lastAdr = extraAddress;
            }
        }

        /*Vlastni slucovani*/
        int[] pool = new int[65536];

        /*Vynulovat*/
        for (int i = 0; i < pool.length; i++) {
            pool[i] = 0;
        }

        /*Slouceni datovych sekci*/
        for (int i = 0; i < l; i++) {
            s = (Section) allSections.get(i);

            /*Je-li datova, tak nahraj data*/
            if (s.type == Section.COMMON_SECTION || s.type == Section.MOVEBLOCK_SECTION) {
                int sgm = 0;
                for (int k = s.start; k <= s.stop; k++) {
                    pool[k] = s.data[sgm];
                    sgm++;
                }
            }
        }

        Section rs = null;

        /*Pridavny kod*/
        if (extraCode == true) {

            int ofs = extraAddress;

            for (int i = 0; i < l; i++) {
                s = (Section) allSections.get(i);
                if (s.type == Section.INIT_SECTION) {
                    pool[ofs] = 32;
                    ofs++; //JSR
                    pool[ofs] = s.jump % 256;
                    ofs++;
                    pool[ofs] = s.jump / 256;
                    ofs++;
                    continue;
                }

                if (s.type == Section.RUN_SECTION) {
                    pool[ofs] = 76;
                    ofs++; //JMP
                    pool[ofs] = s.jump % 256;
                    ofs++;
                    pool[ofs] = s.jump / 256;
                    ofs++;
                }
            }

            /*Konci li pridavny kod za nejvyssi adresou, upravit*/
            if (ofs > lastAdr) {
                lastAdr = ofs;
            }
            rs = new Section(736, 737, Section.RUN_SECTION, extraAddress);

        }
        else {
            rs = new Section(736, 737, Section.RUN_SECTION, originalRunAdr);
        }

        /*Zakonceni*/
        AtariExecutable retVal = new AtariExecutable(outFile);

        int[] resultData = new int[lastAdr - firstAdr + 1];
        int sgm = 0;
        for (int i = firstAdr; i <= lastAdr; i++) {
            resultData[sgm] = pool[i];
            sgm++;
        }

        Section ds = new Section(firstAdr, lastAdr, Section.COMMON_SECTION);
        ds.data = resultData;

        retVal.addSection(ds);
        retVal.addSection(rs);

        int[] idxs = new int[2];
        idxs[0] = 0;
        idxs[1] = 1;

        try {
            retVal.saveFile(outFile, idxs, true);
        }
        catch (Exception e) {
            throw new AtariExecutableException("Unable to save file");
        }

    }

    /**
     * Slouci nekolik sekci do jedne
     */
    public void mergeSections(int[] indices) throws AtariExecutableException {

        int[] pool = new int[65536];

        for (int i = 0; i < pool.length; i++) {
            pool[i] = -1;
        }

        int l = indices.length;
        Section s;

        int minAdr = Integer.MAX_VALUE;
        int maxAdr = Integer.MIN_VALUE;

        int mergedLength = 0;
        int z = 0;

        int count = 0;

        /*Nejprve kontrola, jestli jsou datove a nastaveni rozsahu*/
        for (int i = 0; i < l; i++) {
            s = (Section) allSections.get(indices[i]);

            if (s.type == Section.COMMON_SECTION || s.type == Section.MOVEBLOCK_SECTION) {
                /*Rozsah adres*/
                if (s.start < minAdr) {
                    minAdr = s.start;
                }
                if (s.stop > maxAdr) {
                    maxAdr = s.stop;
                }

                /*Kopirovani dat a kontrola prekryvani*/
                z = 0;
                for (int k = s.start; k <= s.stop; k++) {
                    if (pool[k] == -1) {
                        pool[k] = s.data[z];
                        z++;
                    }
                    else {
                        throw new AtariExecutableException("Sections overlap");
                    }
                }

                count++;
            }
            else {
                throw new AtariExecutableException("RUN,INIT,RUNINIT sections can not be merged");
            }
        }

        mergedLength = maxAdr - minAdr + 1;

        /*Kontrola velikosti*/
        if (count < 1) {
            throw new AtariExecutableException("Nothing to merge");
        }

        /*Tvorba nove datove sekce*/
        Section ns = new Section(minAdr, maxAdr, Section.COMMON_SECTION);
        int[] dta = new int[mergedLength];
        z = 0;
        for (int i = minAdr; i <= maxAdr; i++) {
            if (pool[i] == -1) {
                dta[z] = 0;
            }
            else {
                dta[z] = pool[i];
            }
            z++;
        }
        ns.data = dta;
        ns.comment = "M";

        int fi = indices[0];
        deleteSections(indices);
        addSection(ns, fi);

    }

    /**
     * Vymaze sekce na danych indexech
     */
    public void deleteSections(int[] indices) {
        /*Delete it*/
        for (int i = 0; i < indices.length; i++) {
            deleteSection(indices[i] - i);
        }
    }

}
