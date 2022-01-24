package org.ellab.paru.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ellab.paru.Constants;
import org.ellab.paru.Paru;
import org.ellab.paru.Performance;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SwingMain extends JFrame {
    private static final long serialVersionUID = 1L;

    private JLabel lblProgressResult;
    private JLabel lblEllipsedResult;
    private JLabel lblRemainingResult;
    private JLabel lblResult;
    private JButton btnBrowse;
    private JButton btnStart;
    private JButton btnCopyClipboard;
    private JTextField txtFile;
    private JTabbedPane tabbedPane;
    private JTextField txtDatePrefix;
    private JTextField txtDateSuffix;
    private JProgressBar progressBar;
    private JSpinner spinDateYear;
    private JLabel lblWebsite;
    private JTextField txtCommandLine;
    private JButton btnCommandLine;
    private JPanel panelChar;
    private JLabel lblLength;
    private JSpinner spinCharMin;
    private JLabel lblCharTo;
    private JSpinner spinCharMax;
    private JLabel lblCharPrefix;
    private JLabel lblCharSuffix;
    private JTextField txtCharPrefix;
    private JTextField txtCharSuffix;
    private JLabel lblCharStart;
    private JLabel lblCharEnd;
    private JTextField txtCharStart;
    private JTextField txtCharEnd;
    private JCheckBox btnCharNumeric;
    private JCheckBox btnCharLower;
    private JCheckBox btnCharUpper;
    private JCheckBox btnCharSpecial;
    private JCheckBox btnCharSpace;
    private JCheckBox btnCharCustom;
    private JTextField txtCharCustom;
    private JPanel panelContainer;
    private JPanel panelStatusbar;
    private JLabel lblVersion;
    private JPanel panelStartStop;
    private JButton btnStop;

    private Paru paru;

    private static NumberFormat numfmt;
    static {
        numfmt = NumberFormat.getNumberInstance(Locale.US);
        numfmt.setMinimumIntegerDigits(0);
        numfmt.setGroupingUsed(false);
    }

    public static void main(String[] args) throws Exception {
        new SwingMain();
    }

    public SwingMain() throws Exception {
        setIconImage(Toolkit.getDefaultToolkit().getImage(SwingMain.class.getResource(Constants.ICON)));
        getContentPane().setBackground(SystemColor.control);
        setResizable(false);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        setTitle(Paru.APPNAME + " " + Paru.VERSION + " - " + Paru.APPDESC);
        setSize(500, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        panelContainer = new JPanel();
        panelContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE, 0.0 };
        panelContainer.setLayout(gridBagLayout);
        getContentPane().add(panelContainer);

        JLabel lblfile = new JLabel("File:");
        lblfile.setDisplayedMnemonic('f');
        GridBagConstraints gbc_lblfile = new GridBagConstraints();
        gbc_lblfile.insets = new Insets(0, 0, 5, 5);
        gbc_lblfile.anchor = GridBagConstraints.WEST;
        gbc_lblfile.gridx = 0;
        gbc_lblfile.gridy = 0;
        panelContainer.add(lblfile, gbc_lblfile);

        txtFile = new JTextField();
        lblfile.setLabelFor(txtFile);
        GridBagConstraints gbc_txtFile = new GridBagConstraints();
        gbc_txtFile.gridwidth = 2;
        gbc_txtFile.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtFile.insets = new Insets(2, 0, 5, 5);
        gbc_txtFile.anchor = GridBagConstraints.NORTH;
        gbc_txtFile.gridx = 1;
        gbc_txtFile.gridy = 0;
        panelContainer.add(txtFile, gbc_txtFile);
        txtFile.setColumns(10);

        btnBrowse = new JButton("...");
        btnBrowse.setMnemonic('.');
        GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
        gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
        gbc_btnBrowse.gridx = 3;
        gbc_btnBrowse.gridy = 0;
        panelContainer.add(btnBrowse, gbc_btnBrowse);

        panelStartStop = new JPanel();
        GridBagConstraints gbc_panelStartStop = new GridBagConstraints();
        gbc_panelStartStop.insets = new Insets(0, 0, 5, 0);
        gbc_panelStartStop.gridwidth = 4;
        gbc_panelStartStop.fill = GridBagConstraints.BOTH;
        gbc_panelStartStop.gridx = 0;
        gbc_panelStartStop.gridy = 1;
        panelContainer.add(panelStartStop, gbc_panelStartStop);
        GridBagLayout gbl_panelStartStop = new GridBagLayout();
        gbl_panelStartStop.columnWidths = new int[] { 0, 0 };
        gbl_panelStartStop.rowHeights = new int[] { 0 };
        gbl_panelStartStop.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panelStartStop.rowWeights = new double[] { Double.MIN_VALUE };
        panelStartStop.setLayout(gbl_panelStartStop);

        btnStart = new JButton("Start");
        GridBagConstraints gbc_btnStart = new GridBagConstraints();
        gbc_btnStart.fill = GridBagConstraints.BOTH;
        gbc_btnStart.insets = new Insets(0, 0, 0, 5);
        gbc_btnStart.gridx = 0;
        gbc_btnStart.gridy = 0;
        panelStartStop.add(btnStart, gbc_btnStart);
        btnStart.setMnemonic('s');
        btnStart.setEnabled(false);
        btnStart.setFont(new Font("Tahoma", Font.PLAIN, 16));

        btnStop = new JButton("Stop");
        btnStop.setEnabled(false);
        btnStop.setMnemonic('t');
        btnStop.setFont(new Font("Tahoma", Font.PLAIN, 16));
        GridBagConstraints gbc_btnStop = new GridBagConstraints();
        gbc_btnStop.anchor = GridBagConstraints.WEST;
        gbc_btnStop.fill = GridBagConstraints.VERTICAL;
        gbc_btnStop.gridx = 1;
        gbc_btnStop.gridy = 0;
        panelStartStop.add(btnStop, gbc_btnStop);

        progressBar = new JProgressBar();
        GridBagConstraints gbc_progressBar = new GridBagConstraints();
        gbc_progressBar.insets = new Insets(0, 0, 5, 0);
        gbc_progressBar.gridwidth = 4;
        gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
        gbc_progressBar.gridx = 0;
        gbc_progressBar.gridy = 2;
        panelContainer.add(progressBar, gbc_progressBar);

        JPanel panelProgress = new JPanel();
        GridBagConstraints gbc_panelProgress = new GridBagConstraints();
        gbc_panelProgress.gridwidth = 4;
        gbc_panelProgress.insets = new Insets(0, 0, 5, 0);
        gbc_panelProgress.fill = GridBagConstraints.HORIZONTAL;
        gbc_panelProgress.gridx = 0;
        gbc_panelProgress.gridy = 3;
        panelContainer.add(panelProgress, gbc_panelProgress);
        panelProgress.setLayout(new GridLayout(0, 6, 0, 0));

        JLabel lblProgress = new JLabel("Progress:");
        panelProgress.add(lblProgress);

        lblProgressResult = new JLabel("");
        panelProgress.add(lblProgressResult);

        JLabel lblEllipsed = new JLabel("Ellipsed:");
        panelProgress.add(lblEllipsed);

        lblEllipsedResult = new JLabel("");
        panelProgress.add(lblEllipsedResult);

        JLabel lblRemaining = new JLabel("Remaining:");
        panelProgress.add(lblRemaining);

        lblRemainingResult = new JLabel("");
        panelProgress.add(lblRemainingResult);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
        gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
        gbc_tabbedPane.gridwidth = 4;
        gbc_tabbedPane.fill = GridBagConstraints.BOTH;
        gbc_tabbedPane.gridx = 0;
        gbc_tabbedPane.gridy = 4;
        panelContainer.add(tabbedPane, gbc_tabbedPane);

        JPanel panelDate = new JPanel();
        panelDate.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab("Date", null, panelDate, null);
        GridBagLayout gbl_panelDate = new GridBagLayout();
        gbl_panelDate.columnWidths = new int[] { 0, 0, 0 };
        gbl_panelDate.rowHeights = new int[] { 0, 0, 0 };
        gbl_panelDate.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_panelDate.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        panelDate.setLayout(gbl_panelDate);

        JLabel lblDateYear = new JLabel("Year:");
        lblDateYear.setDisplayedMnemonic('y');
        GridBagConstraints gbc_lblDateYear = new GridBagConstraints();
        gbc_lblDateYear.insets = new Insets(0, 0, 5, 5);
        gbc_lblDateYear.anchor = GridBagConstraints.WEST;
        gbc_lblDateYear.gridx = 0;
        gbc_lblDateYear.gridy = 0;
        panelDate.add(lblDateYear, gbc_lblDateYear);

        spinDateYear = new JSpinner();
        lblDateYear.setLabelFor(spinDateYear);
        spinDateYear.setModel(new SpinnerNumberModel(1, 1, 99, 1));
        GridBagConstraints gbc_spinDateYear = new GridBagConstraints();
        gbc_spinDateYear.insets = new Insets(0, 0, 5, 0);
        gbc_spinDateYear.anchor = GridBagConstraints.WEST;
        gbc_spinDateYear.gridx = 1;
        gbc_spinDateYear.gridy = 0;
        panelDate.add(spinDateYear, gbc_spinDateYear);

        JLabel lblDatePrefix = new JLabel("Prefix:");
        lblDatePrefix.setDisplayedMnemonic('p');
        GridBagConstraints gbc_lblDatePrefix = new GridBagConstraints();
        gbc_lblDatePrefix.anchor = GridBagConstraints.WEST;
        gbc_lblDatePrefix.insets = new Insets(0, 0, 5, 5);
        gbc_lblDatePrefix.gridx = 0;
        gbc_lblDatePrefix.gridy = 1;
        panelDate.add(lblDatePrefix, gbc_lblDatePrefix);

        txtDatePrefix = new JTextField();
        lblDatePrefix.setLabelFor(txtDatePrefix);
        GridBagConstraints gbc_txtDatePrefix = new GridBagConstraints();
        gbc_txtDatePrefix.insets = new Insets(0, 0, 5, 0);
        gbc_txtDatePrefix.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtDatePrefix.gridx = 1;
        gbc_txtDatePrefix.gridy = 1;
        panelDate.add(txtDatePrefix, gbc_txtDatePrefix);
        txtDatePrefix.setColumns(10);

        JLabel lblDateSuffix = new JLabel("Suffix:");
        lblDateSuffix.setDisplayedMnemonic('u');
        GridBagConstraints gbc_lblDateSuffix = new GridBagConstraints();
        gbc_lblDateSuffix.anchor = GridBagConstraints.WEST;
        gbc_lblDateSuffix.insets = new Insets(0, 0, 5, 5);
        gbc_lblDateSuffix.gridx = 0;
        gbc_lblDateSuffix.gridy = 2;
        panelDate.add(lblDateSuffix, gbc_lblDateSuffix);

        txtDateSuffix = new JTextField();
        lblDateSuffix.setLabelFor(txtDateSuffix);
        GridBagConstraints gbc_txtDateSuffix = new GridBagConstraints();
        gbc_txtDateSuffix.insets = new Insets(0, 0, 5, 0);
        gbc_txtDateSuffix.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtDateSuffix.gridx = 1;
        gbc_txtDateSuffix.gridy = 2;
        panelDate.add(txtDateSuffix, gbc_txtDateSuffix);
        txtDateSuffix.setColumns(10);

        panelChar = new JPanel();
        tabbedPane.addTab("Char", null, panelChar, null);
        panelChar.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_panelChar = new GridBagLayout();
        gbl_panelChar.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panelChar.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_panelChar.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
        gbl_panelChar.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        panelChar.setLayout(gbl_panelChar);

        lblLength = new JLabel("Length:");
        lblLength.setDisplayedMnemonic('g');
        GridBagConstraints gbc_lblLength = new GridBagConstraints();
        gbc_lblLength.insets = new Insets(0, 0, 5, 5);
        gbc_lblLength.gridx = 0;
        gbc_lblLength.gridy = 0;
        panelChar.add(lblLength, gbc_lblLength);

        spinCharMin = new JSpinner();
        lblLength.setLabelFor(spinCharMin);
        spinCharMin.setModel(new SpinnerNumberModel(4, 1, 99, 1));
        GridBagConstraints gbc_spinCharMin = new GridBagConstraints();
        gbc_spinCharMin.anchor = GridBagConstraints.WEST;
        gbc_spinCharMin.insets = new Insets(0, 0, 5, 5);
        gbc_spinCharMin.gridx = 1;
        gbc_spinCharMin.gridy = 0;
        panelChar.add(spinCharMin, gbc_spinCharMin);

        lblCharTo = new JLabel("to");
        lblCharTo.setDisplayedMnemonic('t');
        GridBagConstraints gbc_lblCharTo = new GridBagConstraints();
        gbc_lblCharTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblCharTo.gridx = 2;
        gbc_lblCharTo.gridy = 0;
        panelChar.add(lblCharTo, gbc_lblCharTo);

        spinCharMax = new JSpinner();
        lblCharTo.setLabelFor(spinCharMax);
        spinCharMax.setModel(new SpinnerNumberModel(4, 1, 99, 1));
        GridBagConstraints gbc_spinCharMax = new GridBagConstraints();
        gbc_spinCharMax.insets = new Insets(0, 0, 5, 5);
        gbc_spinCharMax.gridx = 3;
        gbc_spinCharMax.gridy = 0;
        panelChar.add(spinCharMax, gbc_spinCharMax);

        btnCharNumeric = new JCheckBox("0-9");
        btnCharNumeric.setMnemonic('0');
        btnCharNumeric.setSelected(true);
        GridBagConstraints gbc_btnCharNumeric = new GridBagConstraints();
        gbc_btnCharNumeric.anchor = GridBagConstraints.WEST;
        gbc_btnCharNumeric.insets = new Insets(0, 0, 5, 5);
        gbc_btnCharNumeric.gridx = 6;
        gbc_btnCharNumeric.gridy = 0;
        panelChar.add(btnCharNumeric, gbc_btnCharNumeric);

        btnCharSpecial = new JCheckBox("Special");
        btnCharSpecial.setMnemonic('i');
        GridBagConstraints gbc_btnCharSpecial = new GridBagConstraints();
        gbc_btnCharSpecial.gridwidth = 2;
        gbc_btnCharSpecial.anchor = GridBagConstraints.WEST;
        gbc_btnCharSpecial.insets = new Insets(0, 0, 5, 0);
        gbc_btnCharSpecial.gridx = 7;
        gbc_btnCharSpecial.gridy = 0;
        panelChar.add(btnCharSpecial, gbc_btnCharSpecial);

        lblCharPrefix = new JLabel("Prefix:");
        lblCharPrefix.setDisplayedMnemonic('p');
        GridBagConstraints gbc_lblCharPrefix = new GridBagConstraints();
        gbc_lblCharPrefix.anchor = GridBagConstraints.WEST;
        gbc_lblCharPrefix.insets = new Insets(0, 0, 5, 5);
        gbc_lblCharPrefix.gridx = 0;
        gbc_lblCharPrefix.gridy = 1;
        panelChar.add(lblCharPrefix, gbc_lblCharPrefix);

        txtCharPrefix = new JTextField();
        lblCharPrefix.setLabelFor(txtCharPrefix);
        GridBagConstraints gbc_txtCharPrefix = new GridBagConstraints();
        gbc_txtCharPrefix.gridwidth = 3;
        gbc_txtCharPrefix.insets = new Insets(0, 0, 5, 5);
        gbc_txtCharPrefix.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCharPrefix.gridx = 1;
        gbc_txtCharPrefix.gridy = 1;
        panelChar.add(txtCharPrefix, gbc_txtCharPrefix);
        txtCharPrefix.setColumns(10);

        lblCharStart = new JLabel("Start:");
        lblCharStart.setDisplayedMnemonic('r');
        GridBagConstraints gbc_lblCharStart = new GridBagConstraints();
        gbc_lblCharStart.anchor = GridBagConstraints.WEST;
        gbc_lblCharStart.insets = new Insets(0, 0, 5, 5);
        gbc_lblCharStart.gridx = 4;
        gbc_lblCharStart.gridy = 1;
        panelChar.add(lblCharStart, gbc_lblCharStart);

        txtCharStart = new JTextField();
        lblCharStart.setLabelFor(txtCharStart);
        GridBagConstraints gbc_txtCharStart = new GridBagConstraints();
        gbc_txtCharStart.insets = new Insets(0, 0, 5, 5);
        gbc_txtCharStart.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCharStart.gridx = 5;
        gbc_txtCharStart.gridy = 1;
        panelChar.add(txtCharStart, gbc_txtCharStart);
        txtCharStart.setColumns(10);

        btnCharLower = new JCheckBox("a-z");
        btnCharLower.setMnemonic('a');
        GridBagConstraints gbc_btnCharLower = new GridBagConstraints();
        gbc_btnCharLower.anchor = GridBagConstraints.WEST;
        gbc_btnCharLower.insets = new Insets(0, 0, 5, 5);
        gbc_btnCharLower.gridx = 6;
        gbc_btnCharLower.gridy = 1;
        panelChar.add(btnCharLower, gbc_btnCharLower);

        btnCharSpace = new JCheckBox("Space");
        btnCharSpace.setMnemonic('e');
        GridBagConstraints gbc_btnCharSpace = new GridBagConstraints();
        gbc_btnCharSpace.gridwidth = 2;
        gbc_btnCharSpace.anchor = GridBagConstraints.WEST;
        gbc_btnCharSpace.insets = new Insets(0, 0, 5, 0);
        gbc_btnCharSpace.gridx = 7;
        gbc_btnCharSpace.gridy = 1;
        panelChar.add(btnCharSpace, gbc_btnCharSpace);

        lblCharSuffix = new JLabel("Suffix:");
        lblCharSuffix.setDisplayedMnemonic('u');
        GridBagConstraints gbc_lblCharSuffix = new GridBagConstraints();
        gbc_lblCharSuffix.anchor = GridBagConstraints.WEST;
        gbc_lblCharSuffix.insets = new Insets(0, 0, 0, 5);
        gbc_lblCharSuffix.gridx = 0;
        gbc_lblCharSuffix.gridy = 2;
        panelChar.add(lblCharSuffix, gbc_lblCharSuffix);

        txtCharSuffix = new JTextField();
        lblCharSuffix.setLabelFor(txtCharSuffix);
        GridBagConstraints gbc_txtCharSuffix = new GridBagConstraints();
        gbc_txtCharSuffix.gridwidth = 3;
        gbc_txtCharSuffix.insets = new Insets(0, 0, 0, 5);
        gbc_txtCharSuffix.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCharSuffix.gridx = 1;
        gbc_txtCharSuffix.gridy = 2;
        panelChar.add(txtCharSuffix, gbc_txtCharSuffix);
        txtCharSuffix.setColumns(10);

        lblCharEnd = new JLabel("End:");
        lblCharEnd.setDisplayedMnemonic('n');
        GridBagConstraints gbc_lblCharEnd = new GridBagConstraints();
        gbc_lblCharEnd.insets = new Insets(0, 0, 0, 5);
        gbc_lblCharEnd.anchor = GridBagConstraints.WEST;
        gbc_lblCharEnd.gridx = 4;
        gbc_lblCharEnd.gridy = 2;
        panelChar.add(lblCharEnd, gbc_lblCharEnd);

        txtCharEnd = new JTextField();
        lblCharEnd.setLabelFor(txtCharEnd);
        GridBagConstraints gbc_txtCharEnd = new GridBagConstraints();
        gbc_txtCharEnd.insets = new Insets(0, 0, 0, 5);
        gbc_txtCharEnd.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCharEnd.gridx = 5;
        gbc_txtCharEnd.gridy = 2;
        panelChar.add(txtCharEnd, gbc_txtCharEnd);
        txtCharEnd.setColumns(10);

        btnCharUpper = new JCheckBox("A-Z");
        btnCharUpper.setMnemonic('z');
        GridBagConstraints gbc_btnCharUpper = new GridBagConstraints();
        gbc_btnCharUpper.anchor = GridBagConstraints.WEST;
        gbc_btnCharUpper.insets = new Insets(0, 0, 0, 5);
        gbc_btnCharUpper.gridx = 6;
        gbc_btnCharUpper.gridy = 2;
        panelChar.add(btnCharUpper, gbc_btnCharUpper);

        btnCharCustom = new JCheckBox("Custom");
        btnCharCustom.setEnabled(false);
        btnCharCustom.setMnemonic('o');
        GridBagConstraints gbc_btnCharCustom = new GridBagConstraints();
        gbc_btnCharCustom.insets = new Insets(0, 0, 0, 5);
        gbc_btnCharCustom.anchor = GridBagConstraints.WEST;
        gbc_btnCharCustom.gridx = 7;
        gbc_btnCharCustom.gridy = 2;
        panelChar.add(btnCharCustom, gbc_btnCharCustom);

        txtCharCustom = new JTextField();
        txtCharCustom.setEnabled(false);
        GridBagConstraints gbc_txtCharCustom = new GridBagConstraints();
        gbc_txtCharCustom.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCharCustom.gridx = 8;
        gbc_txtCharCustom.gridy = 2;
        panelChar.add(txtCharCustom, gbc_txtCharCustom);
        txtCharCustom.setColumns(10);

        btnCommandLine = new JButton("Command Line");
        btnCommandLine.setMnemonic('l');
        GridBagConstraints gbc_btnCommandLine = new GridBagConstraints();
        gbc_btnCommandLine.gridwidth = 2;
        gbc_btnCommandLine.insets = new Insets(0, 0, 5, 5);
        gbc_btnCommandLine.gridx = 0;
        gbc_btnCommandLine.gridy = 5;
        panelContainer.add(btnCommandLine, gbc_btnCommandLine);

        txtCommandLine = new JTextField();
        txtCommandLine.setEditable(false);
        GridBagConstraints gbc_txtCommandLine = new GridBagConstraints();
        gbc_txtCommandLine.gridwidth = 2;
        gbc_txtCommandLine.insets = new Insets(0, 0, 5, 0);
        gbc_txtCommandLine.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCommandLine.gridx = 2;
        gbc_txtCommandLine.gridy = 5;
        panelContainer.add(txtCommandLine, gbc_txtCommandLine);
        txtCommandLine.setColumns(10);

        lblResult = new JLabel(" ");
        lblResult.setHorizontalAlignment(SwingConstants.CENTER);
        lblResult.setFont(new Font("Tahoma", Font.PLAIN, 20));
        GridBagConstraints gbc_lblResult = new GridBagConstraints();
        gbc_lblResult.insets = new Insets(0, 5, 5, 0);
        gbc_lblResult.gridwidth = 4;
        gbc_lblResult.fill = GridBagConstraints.BOTH;
        gbc_lblResult.gridx = 0;
        gbc_lblResult.gridy = 6;
        panelContainer.add(lblResult, gbc_lblResult);

        btnCopyClipboard = new JButton("Copy to Clipboard");
        btnCopyClipboard.setMnemonic('c');
        btnCopyClipboard.setFont(new Font("Tahoma", Font.PLAIN, 16));
        GridBagConstraints gbc_btnCopyClipboard = new GridBagConstraints();
        gbc_btnCopyClipboard.insets = new Insets(0, 0, 5, 0);
        gbc_btnCopyClipboard.gridwidth = 4;
        gbc_btnCopyClipboard.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnCopyClipboard.gridx = 0;
        gbc_btnCopyClipboard.gridy = 7;
        panelContainer.add(btnCopyClipboard, gbc_btnCopyClipboard);

        panelStatusbar = new JPanel();
        GridBagConstraints gbc_panelStatusbar = new GridBagConstraints();
        gbc_panelStatusbar.gridwidth = 4;
        gbc_panelStatusbar.insets = new Insets(0, 0, 0, 0);
        gbc_panelStatusbar.fill = GridBagConstraints.BOTH;
        gbc_panelStatusbar.gridx = 0;
        gbc_panelStatusbar.gridy = 8;
        panelContainer.add(panelStatusbar, gbc_panelStatusbar);
        panelStatusbar.setLayout(new BorderLayout(0, 0));

        lblWebsite = new JLabel("@GitHub");
        panelStatusbar.add(lblWebsite);
        lblWebsite.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblWebsite.setForeground(Color.BLUE);
        lblWebsite.setHorizontalAlignment(SwingConstants.RIGHT);
        lblWebsite.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        lblVersion = new JLabel("Java " + System.getProperty("java.version") + ", " + System.getProperty("os.name")
                + ", Swing");
        panelStatusbar.add(lblVersion, BorderLayout.WEST);

        addEventHandler();
        addDropTarget();

        setVisible(true);
    }

    private void addEventHandler() {
        addCharEventHandler();
        addStartStopEventHandler();

        txtFile.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                doCheck();
            }

            public void removeUpdate(DocumentEvent e) {
                doCheck();
            }

            public void insertUpdate(DocumentEvent e) {
                doCheck();
            }

            private void doCheck() {
                btnStart.setEnabled(txtFile.getText() != null && txtFile.getText().trim().length() > 0);
            }
        });

        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dir = System.getProperty("user.home");
                if (dir == null || dir.trim().length() == 0) {
                    dir = File.listRoots()[0].getAbsolutePath();
                }
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("Zip Files", "zip"));
                fc.setCurrentDirectory(new File(dir));
                int returnVal = fc.showOpenDialog(SwingMain.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    txtFile.setText(file.getAbsolutePath());
                }
            }
        });

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                genCommandLine();
            }
        });

        btnCommandLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genCommandLine();
            }
        });

        txtCommandLine.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txtCommandLine.selectAll();
            }
        });

        txtCommandLine.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtCommandLine.selectAll();
            }
        });

        btnCopyClipboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(lblResult.getText()), null);
            }
        });

        lblWebsite.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0 && Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(Constants.URL));
                    }
                    catch (Exception ex) {
                    }
                }
            }
        });
    }

    private void addStartStopEventHandler() {
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (paru != null) {
                    if (paru.isRunning()) {
                        paru.pause();
                        btnStart.setText(Constants.BTN_RESUME);
                    }
                    else {
                        paru.resume();
                        btnStart.setText(Constants.BTN_PAUSE);
                    }

                    return;
                }

                final String[] args = genCommandLine();

                showResult(Constants.MSG_WORKING, 0);

                try {
                    paru = new Paru(args, new Paru.ParuListener() {
                        public void combinations(long combinations) {
                            final long finalCombinations = combinations;
                            progressBar.setMinimum(0);
                            progressBar.setMaximum(finalCombinations >= Integer.MAX_VALUE ? (int) Math
                                    .sqrt(finalCombinations) : (int) finalCombinations);
                            lblProgressResult.setText("" + finalCombinations);
                        }

                        public void result(String result) {
                            showResult(result, 1);
                            onJobCompleted();
                        }

                        public void noresult() {
                            showResult(Constants.MSG_NO_RESULT, 2);
                            onJobCompleted();
                        }

                        public void error(String s, Exception ex) {
                            if (ex == null) {
                                showResult(s, 2);
                            }
                            else {
                                showResult("Error: " + s, 2);
                            }
                            onJobCompleted();
                        }

                        public void progress(Performance perf, String current) {
                            final Performance finalPerf = perf;

                            if (current != null && current.length() > 0) {
                                showResult(current, 0);
                            }
                            else {
                                showResult(Constants.MSG_WORKING, 0);
                            }
                            String totalStr = finalPerf.getTotal() > 1000000000 ? String.format("%.2fB",
                                    finalPerf.getTotal() / 1000000000.0) : numfmt.format(finalPerf.getTotal());
                            lblProgressResult.setText(numfmt.format(finalPerf.getCurrent()) + "/" + totalStr);
                            progressBar.setValue(finalPerf.getTotal() >= Integer.MAX_VALUE ? (int) Math.sqrt(finalPerf
                                    .getCurrent()) : (int) finalPerf.getCurrent());
                            lblEllipsedResult.setText(finalPerf.millisToStr(finalPerf.getEllapsed()));
                            lblRemainingResult.setText(finalPerf.millisToStr(finalPerf.getRemaining()));
                        }
                    }, 50);
                }
                catch (ParseException ex) {
                    ;
                }
                catch (IOException ex) {
                    ;
                }

                btnStop.setEnabled(true);
                btnStart.setText(Constants.BTN_PAUSE);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        paru.start();
                    }
                });

                thread.start();
            }
        });
    }

    private void addCharEventHandler() {
        spinCharMin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (((Integer) spinCharMax.getValue()).compareTo((Integer) spinCharMin.getValue()) < 0) {
                    spinCharMax.setValue(spinCharMin.getValue());
                }
            }
        });

        spinCharMax.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (((Integer) spinCharMin.getValue()).compareTo((Integer) spinCharMax.getValue()) > 0) {
                    spinCharMin.setValue(spinCharMax.getValue());
                }
            }
        });
    }

    private void addDropTarget() {
        new DropTarget(this, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    Transferable transferable = dtde.getTransferable();

                    // we accept only Strings
                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        @SuppressWarnings("unchecked")
                        List<File> droppedFiles = (List<File>) transferable
                                .getTransferData(DataFlavor.javaFileListFlavor);
                        if (droppedFiles.size() > 0) {
                            txtFile.setText(droppedFiles.get(0).getAbsolutePath());
                        }
                        dtde.getDropTargetContext().dropComplete(true);
                    }
                    else {
                        dtde.rejectDrop();
                    }
                }
                catch (Exception exception) {
                    System.err.println("Exception" + exception.getMessage());
                    dtde.rejectDrop();
                }
            }
        });
    }

    private void onJobCompleted() {
        btnStart.setText(Constants.BTN_START);
        btnStop.setEnabled(false);

        paru = null;
    }

    private void showResult(String s, int stage) {
        if (s.length() <= 30) {
            lblResult.setFont(new Font("Tahoma", Font.PLAIN, 20));
        }
        else {
            lblResult.setFont(new Font("Tahoma", Font.PLAIN, 12));
        }
        if (stage == 1) {
            lblResult.setForeground(new Color(0, 128, 0));
        }
        else if (stage == 2) {
            lblResult.setForeground(new Color(128, 0, 0));
        }
        else {
            lblResult.setForeground(new Color(0, 0, 0));
        }
        lblResult.setText(s);
    }

    private String[] genCommandLine(boolean display) {
        return tabbedPane.getSelectedIndex() == 1 ? Paru.getCharCmdLineArgs(txtFile.getText(),
                btnCharNumeric.isSelected(), btnCharLower.isSelected(), btnCharUpper.isSelected(),
                btnCharSpecial.isSelected(), btnCharSpace.isSelected(), txtCharCustom.getText(),
                (Integer) spinCharMin.getValue(), (Integer) spinCharMax.getValue(), txtCharStart.getText(),
                txtCharEnd.getText(), txtCharPrefix.getText(), txtCharSuffix.getText(), display) : Paru
                .getDateCmdLineArgs(txtFile.getText(), (Integer) spinDateYear.getValue(), txtDatePrefix.getText(),
                        txtDateSuffix.getText(), display);
    }

    private String[] genCommandLine() {
        String args[] = genCommandLine(true);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }
        txtCommandLine.setText("java -jar paru.jar " + sb.toString().trim());

        return genCommandLine(false);
    }
}
