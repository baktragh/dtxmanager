package dtxmanager;

import dtxmanager.hexa.HexaEditorDialog;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class DtxManager {

    /*GUI*/
    static DtxFrame frmDtx;
    static AdrDialog dlgAdr;
    static MovDialog dlgMov;
    static MakeTurboDialog dlgTurbo;
    static DocumentationFrame frmDocu;
    static HexaEditorDialog frmHexa;

    static JFileChooser fcXex;
    static JFileChooser fcModXex;
    static JFileChooser fcSection;
    static JFileChooser fcProject;

    /*XEX FILES*/
    static AtariExecutable ae1;
    static AtariExecutable ae2;

    /*Envir.*/
    static String sp = System.getProperty("file.separator");
    static String configFile = System.getProperty("user.home") + sp + ".dtxmanager.cfg";

    public static void main(String[] args) {

        tryWindowsLookAndFeel();

        ae1 = new AtariExecutable("");
        ae2 = new AtariExecutable("");

        ae1.initIcons();

        /*GUI*/
        frmDtx = new DtxFrame();
        dlgAdr = new AdrDialog();
        dlgAdr.pack();
        centerContainer(dlgAdr);
        frmDtx.pack();
        centerContainer(frmDtx);
        dlgMov = new MovDialog();
        dlgMov.pack();
        centerContainer(dlgMov);
        dlgTurbo = new MakeTurboDialog();
        dlgTurbo.pack();
        centerContainer(dlgTurbo);
        frmDocu = new DocumentationFrame();
        frmDocu.pack();
        centerContainer(frmDocu);
        frmHexa = new HexaEditorDialog();
        frmHexa.pack();
        centerContainer(frmHexa);

        fcXex = new JFileChooser();
        fcModXex = new JFileChooser();
        fcSection = new JFileChooser();
        fcProject = new JFileChooser();

        loadLayout();

        if (args.length == 1) {
            ae1.clear();
            ae1.setFileName(args[0]);

            try {
                ae1.analyze(true);
            }
            catch (AtariExecutableException ex) {
                ex.printStackTrace();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        frmDtx.setVisible(true);

    }

    /*Operace provadene*/
 /*Presun sekce z jednoho souboru do druheho*/
    public static void moveSectionToMofidied(Section s) {
        /*Clone section*/
        Section sn = (Section) s.clone();
        ae2.addSection(sn);

    }

    /*Odstraneni sekce ze souboru*/
    public static void removeSection(int index) {
        ae2.deleteSection(index);
    }

    public static void centerContainer(Container c) {
        Dimension _d = Toolkit.getDefaultToolkit().getScreenSize();
        int _x, _y;
        _x = (_d.width - c.getBounds().width) / 2;
        _y = (_d.height - c.getBounds().height) / 2;
        c.setLocation(_x, _y);
    }

    /**
     * Ulozi projekt jako serializovany seznam sekci
     */
    public static void saveProject(String filename) throws Exception {
        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(ae2.getAllSections());
        fos.flush();
        fos.close();

    }

    /**
     * Nacte projekt jako serializovany seznam sekci
     */
    public static void loadProject(String filename) throws Exception {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList al = (ArrayList) ois.readObject();
        ae2.clear();
        ae2.setAllSections(al);
        ois.close();
    }

    public static void loadLayout() {

        int[] leftCols = new int[4];
        int[] rightCols = new int[4];

        int dividerLocation = 0;
        int[] frameBounds = new int[4];

        String[] fcs = new String[4];

        RandomAccessFile raf = null;

        try {
            raf = new RandomAccessFile(configFile, "r");
            for (int i = 0; i < 4; i++) {
                leftCols[i] = raf.readInt();
                rightCols[i] = raf.readInt();
                frameBounds[i] = raf.readInt();
            }
            dividerLocation = raf.readInt();
            for (int i = 0; i < 4; i++) {
                fcs[i] = raf.readUTF();
            }

            raf.close();
        }
        catch (Exception e) {
            /*Umyslne prazdne*/
            try {
                raf.close();
            }
            catch (Exception e1) {
                /*Nejde nic udelat*/
            }
            return;
        }

        /*Je jiste, ze se vse nacetlo OK*/
        frmDtx.setElementsLayout(leftCols, rightCols, frameBounds, dividerLocation);
        setChooserFile(fcXex, fcs[0]);
        setChooserFile(fcModXex, fcs[1]);
        setChooserFile(fcSection, fcs[2]);
        setChooserFile(fcProject, fcs[3]);
    }

    public static void saveLayout() {

        int[] leftCols = new int[4];
        int[] rightCols = new int[4];

        int[] dividerLocation = new int[1];
        int[] frameBounds = new int[4];

        RandomAccessFile raf = null;

        frmDtx.getElementsLayout(leftCols, rightCols, frameBounds, dividerLocation);

        try {
            raf = new RandomAccessFile(configFile, "rw");
            raf.setLength(0L);

            for (int i = 0; i < 4; i++) {
                raf.writeInt(leftCols[i]);
                raf.writeInt(rightCols[i]);
                raf.writeInt(frameBounds[i]);
            }
            raf.writeInt(dividerLocation[0]);

            raf.writeUTF(getFileFromChooser(fcXex));
            raf.writeUTF(getFileFromChooser(fcModXex));
            raf.writeUTF(getFileFromChooser(fcSection));
            raf.writeUTF(getFileFromChooser(fcProject));

            raf.close();
        }
        catch (Exception e) {
            /*Umyslne prazdne*/
            try {
                raf.close();
            }
            catch (Exception e1) {
                /*Nejde nic udelat*/
            }
            return;
        }
    }

    public static void programExit() {
        saveLayout();
        System.exit(0);
    }

    private static String getFileFromChooser(JFileChooser fc) {
        File f = fc.getCurrentDirectory();
        if (f != null && f.exists() && f.isDirectory()) {
            return f.getAbsolutePath();
        }
        return "";
    }

    private static void setChooserFile(JFileChooser fc, String fn) {
        if (fn.equals("")) {
            return;
        }
        File f = new File(fn);
        if (f.exists() && f.isDirectory()) {
            fc.setCurrentDirectory(f);
        }
    }

    private static void tryWindowsLookAndFeel() {

        /*Check if running on Windows. If not, just return*/
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            return;
        }

        /*Get all look and feel classes*/
        UIManager.LookAndFeelInfo[] lafInfos = UIManager.getInstalledLookAndFeels();
        String plafClasses[] = new String[lafInfos.length];

        for (int i = 0; i < lafInfos.length; i++) {
            plafClasses[i] = lafInfos[i].getClassName();
        }

        /*Check if there is a look and feel for windows*/
        String windowsLaF = null;

        for (String plafClassName : plafClasses) {
            String lc = plafClassName.toLowerCase();

            /*Windows and not classic*/
            if (lc.contains("windows") && !lc.contains("classic")) {
                windowsLaF = plafClassName;
                break;
            }
            /*Just windows*/
            if (lc.contains("windows")) {
                windowsLaF = plafClassName;
                break;
            }
        }

        /*No Look and Feel found, just return*/
        if (windowsLaF == null) {
            return;
        }

        /*Set Look and Feel found for Windows*/
        try {
            UIManager.setLookAndFeel(windowsLaF);
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }

    }

}
