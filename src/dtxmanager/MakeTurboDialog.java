/*
 * MakeTurboDialog.java
 *
 * Created on 26. leden 2008, 19:36
 */
package dtxmanager;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 */
public class MakeTurboDialog extends javax.swing.JDialog {

    /**
     * Creates new form MakeTurboDialog
     */
    public MakeTurboDialog() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jfcOutFile = new javax.swing.JFileChooser();
        pButtons = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pControls = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jtfOutFile = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jtfExtraCode = new javax.swing.JTextField();
        jcbExtraCode = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jlbLength = new javax.swing.JLabel();

        setTitle("Turbo version");
        setModal(true);

        btnOk.setText("Make turbo version");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OnMakeTurboVersion(evt);
            }
        });
        pButtons.add(btnOk);

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pButtons.add(btnCancel);

        getContentPane().add(pButtons, java.awt.BorderLayout.SOUTH);

        pControls.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Output file:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pControls.add(jLabel2, gridBagConstraints);

        jtfOutFile.setColumns(28);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pControls.add(jtfOutFile, gridBagConstraints);

        btnBrowse.setText("Browse");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pControls.add(btnBrowse, gridBagConstraints);

        jLabel3.setText("Extra code address:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pControls.add(jLabel3, gridBagConstraints);

        jtfExtraCode.setColumns(6);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pControls.add(jtfExtraCode, gridBagConstraints);

        jcbExtraCode.setText("Add extra code");
        jcbExtraCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbExtraCodeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.2;
        pControls.add(jcbExtraCode, gridBagConstraints);

        jLabel1.setText("Extra code length:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pControls.add(jLabel1, gridBagConstraints);

        jlbLength.setText("?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pControls.add(jlbLength, gridBagConstraints);

        getContentPane().add(pControls, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jcbExtraCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbExtraCodeActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jcbExtraCodeActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void OnMakeTurboVersion(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OnMakeTurboVersion
        String s = jtfOutFile.getText().trim();
        String a = jtfExtraCode.getText().trim();
        boolean b = this.jcbExtraCode.isSelected();
        int eadr = 0;
        int base = 10;
        if (a.startsWith("$")) {
            a = a.substring(1);
            base = 16;
        }

        try {
            if (b == true) {
                eadr = Integer.parseInt(a, base);
            }
        }
        catch (Exception e1) {
            warning(e1);
            return;
        }

        try {
            DtxManager.ae2.makeTurboVersion(eadr, b, s);
        }
        catch (Exception e) {
            warning(e);
            return;
        }

        JOptionPane.showMessageDialog(this, "Done OK. Test turbo version with emulator !");

    }//GEN-LAST:event_OnMakeTurboVersion

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        int k = jfcOutFile.showSaveDialog(this);
        if (k != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = jfcOutFile.getSelectedFile();
        this.jtfOutFile.setText(f.getAbsolutePath());

    }//GEN-LAST:event_btnBrowseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MakeTurboDialog().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JCheckBox jcbExtraCode;
    private javax.swing.JFileChooser jfcOutFile;
    private javax.swing.JLabel jlbLength;
    private javax.swing.JTextField jtfExtraCode;
    private javax.swing.JTextField jtfOutFile;
    private javax.swing.JPanel pButtons;
    private javax.swing.JPanel pControls;
    // End of variables declaration//GEN-END:variables

    void warning(Exception e) {
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append(e.getClass().getName());
        e.printStackTrace();
        if (e.getMessage() != null) {

            sb.append(':');
            sb.append("<BR>");
            sb.append(e.getMessage());
            sb.append("</HTML>");

        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setVisible(boolean b) {
        int l = DtxManager.ae2.getExtraCodeLength();
        this.jlbLength.setText(Integer.toString(l));
        super.setVisible(b);

    }
}