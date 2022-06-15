/*
 * LeeEditorView.java
 */
package leeeditor;

import java.awt.Color;
import javax.swing.text.BadLocationException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The application's main frame.
 */
public class LeeEditorView extends FrameView {

    private String action;
    private String location;
    private String temporal;
    private String rank;
    private String interest,  interest1;
    private String name;
    private int caretPos = 0;
    private int caretPoss[] = new int[20];
    private int c = 0;
    JPopupMenu jpm;
    private String author,  version,  runname,  filename;
    int colin = 0;

    public LeeEditorView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        ImageIcon icon = new ImageIcon(this.getClass().getResource("resources/icon.png"));
        this.getFrame().setIconImage(icon.getImage());
        jpm = new JPopupMenu("Keywords");
        JMenuItem item = new JMenuItem("start");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String text = editor.getText(0, caretPos);
                    editor.setText(text + "run run_name{\n\n}");
                } catch (BadLocationException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jpm.add(item);
        item = new JMenuItem("sample");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String text = editor.getText(0, caretPos);
                    editor.setText(text + "//sample\n @author appiah\n @version 1.0\n@run sell_enact \n{\n" + "action a='sell';\n" +
                            "interest ia='sell_pc', ia1='buy_pc';\n" +
                            "rank r='rank_9';\n" +
                            "location loc='tottenham';\n" +
                            "temporal temp='Sat-12/09/2019-12:00pm';\n" +
                            "name='Tottenham-Engagement';\n}");
                } catch (BadLocationException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jpm.add(item);
        item = new JMenuItem("sample1");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String text = editor.getText(0, caretPos);
                    editor.setText(text + "//sample\n @author appiah\n @version 1.0\n@run sell_enact \n{\n" + "enact::action a='sell';\n" +
                            "enact::interest ia='sell_pc', ia1='buy_pc';\n" +
                            "enact::rank r='rank_9';\n" +
                            "enact::location loc='tottenham';\n" +
                            "enact::temporal temp='Sat-12/09/2019-12:00pm';\n" +
                            "enact::name='Tottenham-Engagement';\n}");
                } catch (BadLocationException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jpm.add(item);
        item = new JMenuItem("sample2");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String text = editor.getText(0, caretPos);
                    editor.setText(text + "//sample\n @author appiah\n @version 1.0\n@run sell_enact \n{\n" + "1. action a='sell';\n" +
                            "2. interest ia='sell_pc', ia1='buy_pc';\n" +
                            "3. rank r='rank_9';\n" +
                            "4. location loc='tottenham';\n" +
                            "5. temporal temp='Sat-12/09/2019-12:00pm';\n" +
                            "6. name='Tottenham-Engagement';\n}");
                } catch (BadLocationException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jpm.add(item);

        jpm.addSeparator();
        item = new JMenuItem("Keywords");
        jpm.add(item);
        jpm.addSeparator();
        item = new JMenuItem("name");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String text = editor.getText(0, caretPos);
                    editor.setText(text + "name");
                } catch (BadLocationException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jpm.add(item);
        item = new JMenuItem("interest");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String text = editor.getText(0, caretPos);
                    editor.setText(text + "interest");
                } catch (BadLocationException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jpm.add(item);
        item = new JMenuItem("location");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String text = editor.getText(0, caretPos);
                    editor.setText(text + "location");
                } catch (BadLocationException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jpm.add(item);
        item = new JMenuItem("temporal");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String text = editor.getText(0, caretPos);
                    editor.setText(text + "temporal");
                } catch (BadLocationException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jpm.add(item);
        item = new JMenuItem("action");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String text = editor.getText(0, caretPos);
                    editor.setText(text + "action");
                } catch (BadLocationException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jpm.add(item);
        item = new JMenuItem("rank");
        item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String text = editor.getText(0, caretPos);
                    editor.setText(text + "rank");
                } catch (BadLocationException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jpm.add(item);


    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = LeeEditorApp.getApplication().getMainFrame();
            aboutBox = new LeeEditorAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        LeeEditorApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        varText = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        exetable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        editor = new javax.swing.JEditorPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        output = new javax.swing.JEditorPane();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newBtn = new javax.swing.JMenuItem();
        openBtn = new javax.swing.JMenuItem();
        saveBtn = new javax.swing.JMenuItem();
        saveOutBtn = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        runMenu = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        runBtn = new javax.swing.JButton();
        clearBtn = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(leeeditor.LeeEditorApp.class).getContext().getResourceMap(LeeEditorView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setName("mainPanel"); // NOI18N

        jSplitPane1.setDividerLocation(189);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jTabbedPane1.setBackground(resourceMap.getColor("jTabbedPane1.background")); // NOI18N
        jTabbedPane1.setForeground(resourceMap.getColor("jTabbedPane1.foreground")); // NOI18N
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        varText.setColumns(20);
        varText.setEditable(false);
        varText.setRows(5);
        varText.setText(editor.getText());
        varText.setName("varText"); // NOI18N
        jScrollPane4.setViewportView(varText);

        jPanel1.add(jScrollPane4);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        exetable.setBackground(resourceMap.getColor("exetable.background")); // NOI18N
        exetable.setForeground(resourceMap.getColor("exetable.foreground")); // NOI18N
        exetable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Run Name", "Code Name", "Version", "Execution Time", "Author"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        exetable.setName("exetable"); // NOI18N
        jScrollPane3.setViewportView(exetable);

        jPanel1.add(jScrollPane3);

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        editor.setBackground(resourceMap.getColor("editor.background")); // NOI18N
        editor.setForeground(resourceMap.getColor("editor.foreground")); // NOI18N
        editor.setText(resourceMap.getString("editor.text")); // NOI18N
        editor.setToolTipText(resourceMap.getString("editor.toolTipText")); // NOI18N
        editor.setCaretColor(resourceMap.getColor("editor.caretColor")); // NOI18N
        editor.setName("editor"); // NOI18N
        editor.setSelectedTextColor(resourceMap.getColor("editor.selectedTextColor")); // NOI18N
        editor.setSelectionColor(resourceMap.getColor("editor.selectionColor")); // NOI18N
        editor.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                editorCaretUpdate(evt);
            }
        });
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                editorKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                editorKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                editorKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(editor);

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        jSplitPane1.setLeftComponent(jTabbedPane1);

        jScrollPane1.setFont(resourceMap.getFont("jScrollPane1.font")); // NOI18N
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        output.setBackground(resourceMap.getColor("output.background")); // NOI18N
        output.setFont(resourceMap.getFont("output.font")); // NOI18N
        output.setForeground(resourceMap.getColor("output.foreground")); // NOI18N
        output.setText(resourceMap.getString("output.text")); // NOI18N
        output.setToolTipText(resourceMap.getString("output.toolTipText")); // NOI18N
        output.setName("output"); // NOI18N
        output.setSelectedTextColor(resourceMap.getColor("output.selectedTextColor")); // NOI18N
        output.setSelectionColor(resourceMap.getColor("output.selectionColor")); // NOI18N
        jScrollPane1.setViewportView(output);

        jSplitPane1.setRightComponent(jScrollPane1);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );

        menuBar.setBackground(resourceMap.getColor("menuBar.background")); // NOI18N
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setBackground(resourceMap.getColor("fileMenu.background")); // NOI18N
        fileMenu.setForeground(resourceMap.getColor("fileMenu.foreground")); // NOI18N
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newBtn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newBtn.setText(resourceMap.getString("newBtn.text")); // NOI18N
        newBtn.setName("newBtn"); // NOI18N
        newBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBtnActionPerformed(evt);
            }
        });
        fileMenu.add(newBtn);

        openBtn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openBtn.setText(resourceMap.getString("openBtn.text")); // NOI18N
        openBtn.setName("openBtn"); // NOI18N
        openBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openBtnActionPerformed(evt);
            }
        });
        fileMenu.add(openBtn);

        saveBtn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK));
        saveBtn.setText(resourceMap.getString("saveBtn.text")); // NOI18N
        saveBtn.setName("saveBtn"); // NOI18N
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });
        fileMenu.add(saveBtn);

        saveOutBtn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK));
        saveOutBtn.setText(resourceMap.getString("saveOutBtn.text")); // NOI18N
        saveOutBtn.setName("saveOutBtn"); // NOI18N
        saveOutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveOutBtnActionPerformed(evt);
            }
        });
        fileMenu.add(saveOutBtn);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        runMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        runMenu.setText(resourceMap.getString("runMenu.text")); // NOI18N
        runMenu.setName("runMenu"); // NOI18N
        runMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runMenuActionPerformed(evt);
            }
        });
        fileMenu.add(runMenu);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(leeeditor.LeeEditorApp.class).getContext().getActionMap(LeeEditorView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setForeground(resourceMap.getColor("helpMenu.foreground")); // NOI18N
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        runBtn.setBackground(resourceMap.getColor("runBtn.background")); // NOI18N
        runBtn.setForeground(resourceMap.getColor("runBtn.foreground")); // NOI18N
        runBtn.setText(resourceMap.getString("runBtn.text")); // NOI18N
        runBtn.setToolTipText(resourceMap.getString("runBtn.toolTipText")); // NOI18N
        runBtn.setName("runBtn"); // NOI18N
        runBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runBtnActionPerformed(evt);
            }
        });

        clearBtn.setBackground(resourceMap.getColor("clearBtn.background")); // NOI18N
        clearBtn.setText(resourceMap.getString("clearBtn.text")); // NOI18N
        clearBtn.setName("clearBtn"); // NOI18N
        clearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(clearBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
            .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(statusPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(runBtn)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusMessageLabel)
                            .addComponent(statusAnimationLabel)
                            .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3))
                    .addComponent(clearBtn)))
            .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(statusPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(runBtn)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents
    private void runMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runMenuActionPerformed
        try {

            parse();
            preprocessor("./leeinterpreter ");
            preprocessor("./leeinterpretercont ");
            preprocessor("./leeinterpreterfinal ");
        } catch (BadLocationException ex) {
            Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_runMenuActionPerformed

    private void setOutput(String text) {
        String data = output.getText();
        data += "\n" + text;
        output.setText(data);

    }

    private void runBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runBtnActionPerformed
        try {
            parse();
            preprocessor("./leeinterpreter ");
            
            preprocessor("./leeinterpretercont ");
            preprocessor("./leeinterpreterfinal ");
        } catch (BadLocationException ex) {
            Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }//GEN-LAST:event_runBtnActionPerformed

    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed

        output.setText("");
    }//GEN-LAST:event_clearBtnActionPerformed

    private void editorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_editorKeyTyped
        String[] keywords = {"name", "action", "location", "interest", "rank", "temporal"};
        if (editor.getText().startsWith(keywords[0])) {

        }
    }//GEN-LAST:event_editorKeyTyped

    private void editorCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_editorCaretUpdate
        try {

            if (editor.getText(editor.getCaretPosition() - 4, editor.getCaretPosition()).startsWith("name")) {
                editor.setForeground(Color.PINK);
            } else {
                editor.setForeground(Color.BLUE);
            }
            if (editor.getText(editor.getCaretPosition() - 8, editor.getCaretPosition()).startsWith("name")) {
                editor.setForeground(Color.ORANGE);
            } else {
                editor.setForeground(Color.BLUE);
            }
            if (editor.getText(editor.getCaretPosition() - 6, editor.getCaretPosition()).startsWith("name")) {
                editor.setForeground(Color.GREEN);
            } else {
                editor.setForeground(Color.BLUE);
            }
        } catch (BadLocationException ex) {
        //  Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_editorCaretUpdate

    private void editorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_editorKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            caretPos = editor.getCaretPosition();
            caretPoss[c] = caretPos;
        }
    }//GEN-LAST:event_editorKeyReleased

    private void editorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_editorKeyPressed
        if (evt.getKeyCode() == (KeyEvent.VK_CONTEXT_MENU)) {
            jpm.show(this.getComponent(), editor.getLocation().x + 40, editor.getLocation().y + 30);
        }
    }//GEN-LAST:event_editorKeyPressed

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        FileOutputStream fos = null;


        File f = new File("lee.vai");
        JFileChooser ifc = new JFileChooser();
        ifc.setAcceptAllFileFilterUsed(true);
        FileFilter filter = new FileNameExtensionFilter("(V)ariable (A)ssignment (I)nterpretation", ".vai", "vai", "VAI");
        ifc.setFileFilter(filter);
        ifc.setApproveButtonText("Save vai");
        ifc.setApproveButtonToolTipText("New VAI Saving");
        ifc.setDialogTitle("Variable Assignment Interpretation[Save Mode]");
        ifc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int r = ifc.showSaveDialog(this.getComponent());
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                f = ifc.getSelectedFile();
                fos = new FileOutputStream(f);
                fos.write(editor.getText().getBytes());
                this.getFrame().setTitle("LEELus Editor [" + f.getName() + "]");
            } catch (IOException ex) {
                Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        setOutput("Finished in saving vai file to system.");
    }//GEN-LAST:event_saveBtnActionPerformed

    private void openBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openBtnActionPerformed

        File f = new File("/");
        JFileChooser ifc = new JFileChooser();
        ifc.setAcceptAllFileFilterUsed(true);
        FileFilter filter = new FileNameExtensionFilter("(V)ariable (A)ssignment (I)nterpretation", ".vai", "vai", "VAI");
        ifc.setFileFilter(filter);
        ifc.setApproveButtonText("Assign");
        ifc.setApproveButtonToolTipText("New VAI Setting");
        ifc.setDialogTitle("Variable Assignment Interpretation");
        ifc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int r = ifc.showOpenDialog(this.getComponent());
        if (r == JFileChooser.APPROVE_OPTION) {
            FileInputStream fis = null;
            try {
                f = ifc.getSelectedFile();
                fis = new FileInputStream(f);
                byte data[] = new byte[fis.available()];
                fis.read(data);
                editor.setText(new String(data));
                setOutput(f.toString() + " is read and set for running....");
                this.getFrame().setTitle("LEELus Editor [" + f.getName() + "]");

            } catch (IOException ex) {
                Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }//GEN-LAST:event_openBtnActionPerformed

    private void saveOutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveOutBtnActionPerformed
        FileOutputStream fos = null;


        File f = new File("out.lee");
        JFileChooser ifc = new JFileChooser();
        ifc.setAcceptAllFileFilterUsed(true);
        FileFilter filter = new FileNameExtensionFilter("(L)ogical (E)nactment (E)nterpretation", ".lee", "lee", "LEE");
        ifc.setFileFilter(filter);
        ifc.setApproveButtonText("Save lee");
        ifc.setApproveButtonToolTipText("New LEE Output Saving");
        ifc.setDialogTitle("Logical Enactment Interpretation[Save Mode]");
        ifc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int r = ifc.showSaveDialog(this.getComponent());
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                f = ifc.getSelectedFile();
                fos = new FileOutputStream(f);
                fos.write(output.getText().getBytes());
                this.getFrame().setTitle("LEELus Editor [" + f.getName() + "]");

            } catch (IOException ex) {
                Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        setOutput("Finished in saving lee output file to system....");
   
    }//GEN-LAST:event_saveOutBtnActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        FileInputStream fos = null;


        File f = new File("out.lee");
        JFileChooser ifc = new JFileChooser();
        ifc.setAcceptAllFileFilterUsed(true);
        FileFilter filter = new FileNameExtensionFilter("(L)ogical (E)nactment (I)nterpretation", ".lee", "lee", "LEE");
        ifc.setFileFilter(filter);
        ifc.setApproveButtonText("Open LEE out");
        ifc.setApproveButtonToolTipText("New LEE Output Opening");
        ifc.setDialogTitle("Logical Enactment Interpretation[Open Mode]");
        ifc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int r = ifc.showOpenDialog(this.getComponent());
        if (r == JFileChooser.APPROVE_OPTION) {
            try {
                f = ifc.getSelectedFile();
                fos = new FileInputStream(f);
                byte data[] = new byte[fos.available()];
                fos.read(data);
                output.setText(new String(data));
                this.getFrame().setTitle("LEELus Editor [" + f.getName() + "]");

            } catch (IOException ex) {
                Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        setOutput("Reading LEE output file into LEE Editor....");
        
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void newBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBtnActionPerformed
        output.setText("");
        editor.setText("");
    }//GEN-LAST:event_newBtnActionPerformed

    private void preprocessor(String name) {
        try {
            Runtime run = Runtime.getRuntime();
            String env = "-a " + action + " -l " + location + " -i " + interest + ":" + interest1 +
                    " -t " + temporal + " -r " + rank + " -n " + name;
            Process p = run.exec(name + env.toString());
            
            InputStream is = p.getInputStream();
            byte[] databy = new byte[is.available()];
            int hpos = is.available();
            is.read(databy);
            String out = new String(databy, "US-ASCII");

            if (out.startsWith("LEE")) {
            //output.setCaretColor(new Color(23, 55, 66));
            // output.setCaretPosition(3);
            } else {

            }
            hpos++;
            setOutput(out);

        } catch (IOException ex) {
            Logger.getLogger(LeeEditorView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void parse() throws BadLocationException {
        String varis = editor.getText();
        String[] linvar = varis.split(";");
        int pos = 0;
        String data;

        if (linvar.length < 6) {
            setOutput("\nLine variables less than 6 parameter....");
        }
        for (int k = 0; k < linvar.length; k++) {

            String var = linvar[k];
            if (var.contains("action")) {
                pos = var.indexOf("=");
                action = var.substring(pos + 2, var.length() - 1);
                varText.append("\naction::=" + action);
            } else if (var.contains("location")) {
                pos = var.indexOf("=");
                location = var.substring(pos + 2, var.length() - 1);
                varText.append("\nlocation::=" + location);
            } else if (var.contains("temporal")) {
                pos = var.indexOf("=");
                temporal = var.substring(pos + 2, var.length() - 1);
                varText.append("\ntemporal::=" + temporal);
            } else if (var.contains("interest")) {
                String[] isn = var.split(",");
                pos = isn[0].indexOf("=");
                interest = var.substring(pos + 2, isn[0].length() - 1);
                pos = isn[1].indexOf("=");
                interest1 = isn[1].substring(pos + 2, isn[1].length() - 1);
                varText.append("\ninterest::=" + interest + ":" + interest1);
            } else if (var.contains("rank")) {
                pos = var.indexOf("=");
                rank = var.substring(pos + 2, var.length() - 1);
                varText.append("\nrank::=" + rank);
            } else if (var.contains("name")) {
                pos = var.indexOf("=");
                name = var.substring(pos + 2, var.length() - 1);
                varText.append("\nSitutation Name::=" + name);
            }
        }
        output.setText(output.getText() + " Preprocessor::" + "./leeinterpreter ");
           
        varText.append("\n======================");
        linvar = varis.split("/");
        String comm = "Comment::";
        // for (int k = 0; k < linvar.length; k++) {
        String var = varis;
        if (var.contains("//")) {
            pos = var.indexOf("/");
            int pos1 = var.indexOf("@");
            comm += var.substring(pos + 2, pos1 - 1);
        }

        editor.setToolTipText(comm);

        linvar = varis.split("@");
        for (int k = 0; k < linvar.length; k++) {
            var = linvar[k];
            if (var.contains("author")) {
                pos = var.indexOf("@");
                author = var.substring(pos + 8, var.length());

            } else if (var.contains("version")) {
                pos = var.indexOf("@");
                version = var.substring(pos + 9, var.length());

            } else if (var.contains("run")) {
                int pos1 = var.indexOf("@run");
                pos = var.indexOf("{");
                runname = var.substring(pos1 + 5, pos - 1);
            }
        }
        exetable.setValueAt(name, colin, 0);
        exetable.setValueAt(runname, colin, 1);
        exetable.setValueAt(version, colin, 2);
        exetable.setValueAt(new Date().toString(), colin, 3);
        exetable.setValueAt(author, colin, 4);
        colin++;

        setOutput("Parameter parsing is prepared for preprocessor...\n");

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearBtn;
    private javax.swing.JEditorPane editor;
    private javax.swing.JTable exetable;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newBtn;
    private javax.swing.JMenuItem openBtn;
    private javax.swing.JEditorPane output;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton runBtn;
    private javax.swing.JMenuItem runMenu;
    private javax.swing.JMenuItem saveBtn;
    private javax.swing.JMenuItem saveOutBtn;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextArea varText;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
