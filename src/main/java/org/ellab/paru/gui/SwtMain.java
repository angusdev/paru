package org.ellab.paru.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ellab.paru.Constants;
import org.ellab.paru.Paru;
import org.ellab.paru.Performance;
import org.ellab.paru.pattern.CharPattern;

public class SwtMain {
    private Paru paru;

    private Display display;
    private Shell shell;

    private Button btnBrowse;
    private Composite compositeStartStop;
    private Button btnStop;
    private Button btnStart;
    private Button btnCopyClipboard;
    private Text txtFile;
    private ProgressBar progressBar;
    private Label lblProgressResult;
    private Label lblEllipsedResult;
    private Label lblRemainingResult;
    private Label lblResult;
    private TabFolder tabFolder;
    private Spinner spinDateYear;
    private Text txtDatePrefix;
    private Text txtDateSuffix;
    private Composite compositeChar;
    private Label lblCharPrefix;
    private Label lblCharSuffix;
    private Text txtCharSuffix;
    private Text txtCharPrefix;
    private Label lblCharLength;
    private Spinner spinCharMin;
    private Label lblCharTo;
    private Spinner spinCharMax;
    private Button btnCharNumeric;
    private Button btnCharSpecial;
    private Button btnCharLower;
    private Button btnCharSpace;
    private Button btnCharUpper;
    private Text txtCharCustom;
    private Button btnCharCustom;
    private Label lblCharStart;
    private Text txtCharStart;
    private Label lblCharEnd;
    private Text txtCharEnd;
    private Link linkWebsite;

    private Text txtCommandLine;
    private Button btnCommnadLine;
    private Label lblVersion;
    private Composite compositeStatusbar;

    private static NumberFormat numfmt;
    static {
        numfmt = NumberFormat.getNumberInstance(Locale.US);
        numfmt.setMinimumIntegerDigits(0);
        numfmt.setGroupingUsed(false);
    }

    public static void main(String[] args) {
        SwtMain main = new SwtMain(args);
        main.show();
    }

    public SwtMain(String[] args) {
        display = new Display();
        shell = new Shell(display, SWT.SHELL_TRIM & ~SWT.RESIZE & ~SWT.MAX);
        shell.setImage(new Image(display, SwtMain.class.getResourceAsStream(Constants.ICON)));
        shell.setSize(460, 320);
        shell.setText(Paru.APPNAME + " " + Paru.VERSION + " - " + Paru.APPDESC);
        shell.setLayout(new GridLayout(5, false));

        Label lblFile = new Label(shell, SWT.NONE);
        lblFile.setText("&File:");

        txtFile = new Text(shell, SWT.BORDER);
        txtFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        btnBrowse = new Button(shell, SWT.NONE);
        btnBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        btnBrowse.setText("&...");

        compositeStartStop = new Composite(shell, SWT.NONE);
        compositeStartStop.setLayout(new GridLayout(2, false));
        compositeStartStop.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

        btnStart = new Button(compositeStartStop, SWT.NONE);
        btnStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnStart.setEnabled(false);
        btnStart.setFont(SWTResourceManager.getFont("Tahoma", 16, SWT.NORMAL));
        btnStart.setText("&Start");

        btnStop = new Button(compositeStartStop, SWT.NONE);
        btnStop.setEnabled(false);
        btnStop.setFont(SWTResourceManager.getFont("Tahoma", 16, SWT.NORMAL));
        btnStop.setText("S&top");

        progressBar = new ProgressBar(shell, SWT.NONE);
        progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));

        Composite compositeProgress = new Composite(shell, SWT.NONE);
        compositeProgress.setLayout(new GridLayout(7, true));
        compositeProgress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));

        Label lblProgress = new Label(compositeProgress, SWT.NONE);
        lblProgress.setText("Progress:");

        lblProgressResult = new Label(compositeProgress, SWT.NONE);
        lblProgressResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        Label lblEllipsed = new Label(compositeProgress, SWT.NONE);
        lblEllipsed.setText("Ellipsed:");

        lblEllipsedResult = new Label(compositeProgress, SWT.NONE);
        lblEllipsedResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        Label lblRemaining = new Label(compositeProgress, SWT.NONE);
        lblRemaining.setText("Remaining:");

        lblRemainingResult = new Label(compositeProgress, SWT.NONE);
        lblRemainingResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        tabFolder = new TabFolder(shell, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));

        TabItem tbtmDate = new TabItem(tabFolder, SWT.NONE);
        tbtmDate.setToolTipText("yyyymmdd, yymmdd, ddmmyyyy, ddmmyy, ddmm, mmdd");
        tbtmDate.setText("&Date");

        Composite compositeDate = new Composite(tabFolder, SWT.NONE);
        tbtmDate.setControl(compositeDate);
        compositeDate.setLayout(new GridLayout(2, false));

        Label lblDateYear = new Label(compositeDate, SWT.NONE);
        lblDateYear.setText("&Year:");

        spinDateYear = new Spinner(compositeDate, SWT.BORDER);
        spinDateYear.setMaximum(99);
        spinDateYear.setMinimum(1);
        spinDateYear.setSelection(1);

        Label lblDatePrefix = new Label(compositeDate, SWT.NONE);
        lblDatePrefix.setText("&Prefix:");

        txtDatePrefix = new Text(compositeDate, SWT.BORDER);
        txtDatePrefix.setToolTipText("Comma separated.  E.g. foo,bar");
        txtDatePrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblDateSuffix = new Label(compositeDate, SWT.NONE);
        lblDateSuffix.setText("S&uffix:");

        txtDateSuffix = new Text(compositeDate, SWT.BORDER);
        txtDateSuffix.setToolTipText("Comma separated.  E.g. foo,bar");
        txtDateSuffix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        TabItem tbtmChar = new TabItem(tabFolder, SWT.NONE);
        tbtmChar.setText("C&har");

        compositeChar = new Composite(tabFolder, SWT.NONE);
        tbtmChar.setControl(compositeChar);
        compositeChar.setLayout(new GridLayout(9, false));

        lblCharLength = new Label(compositeChar, SWT.NONE);
        lblCharLength.setText("Len&gth:");

        spinCharMin = new Spinner(compositeChar, SWT.BORDER);
        spinCharMin.setMaximum(99);
        spinCharMin.setMinimum(1);
        spinCharMin.setSelection(4);

        lblCharTo = new Label(compositeChar, SWT.NONE);
        lblCharTo.setText("&to");

        spinCharMax = new Spinner(compositeChar, SWT.BORDER);
        spinCharMax.setMaximum(99);
        spinCharMax.setMinimum(1);
        spinCharMax.setSelection(4);
        new Label(compositeChar, SWT.NONE);
        new Label(compositeChar, SWT.NONE);

        btnCharNumeric = new Button(compositeChar, SWT.CHECK);
        btnCharNumeric.setSelection(true);
        btnCharNumeric.setToolTipText(CharPattern.NUMERIC);
        btnCharNumeric.setText("&0-9");

        btnCharSpecial = new Button(compositeChar, SWT.CHECK);
        btnCharSpecial.setToolTipText(CharPattern.SPECIAL);
        btnCharSpecial.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        btnCharSpecial.setText("Spec&ial");

        lblCharPrefix = new Label(compositeChar, SWT.NONE);
        lblCharPrefix.setText("&Prefix:");

        txtCharPrefix = new Text(compositeChar, SWT.BORDER);
        txtCharPrefix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        lblCharStart = new Label(compositeChar, SWT.NONE);
        lblCharStart.setText("Sta&rt:");

        txtCharStart = new Text(compositeChar, SWT.BORDER);
        txtCharStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnCharLower = new Button(compositeChar, SWT.CHECK);
        btnCharLower.setToolTipText(CharPattern.LOWER);
        btnCharLower.setText("&a-z");

        btnCharSpace = new Button(compositeChar, SWT.CHECK);
        btnCharSpace.setToolTipText("\" \"");
        btnCharSpace.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        btnCharSpace.setText("Spac&e");

        lblCharSuffix = new Label(compositeChar, SWT.NONE);
        lblCharSuffix.setText("S&uffix:");

        txtCharSuffix = new Text(compositeChar, SWT.BORDER);
        txtCharSuffix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        lblCharEnd = new Label(compositeChar, SWT.NONE);
        lblCharEnd.setText("E&nd:");

        txtCharEnd = new Text(compositeChar, SWT.BORDER);
        txtCharEnd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        btnCharUpper = new Button(compositeChar, SWT.CHECK);
        btnCharUpper.setToolTipText(CharPattern.UPPER);
        btnCharUpper.setText("A-&Z");

        btnCharCustom = new Button(compositeChar, SWT.CHECK);
        btnCharCustom.setEnabled(false);
        btnCharCustom.setText("Cust&om:");

        txtCharCustom = new Text(compositeChar, SWT.BORDER);
        txtCharCustom.setEnabled(false);
        txtCharCustom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        btnCommnadLine = new Button(shell, SWT.NONE);
        btnCommnadLine.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        btnCommnadLine.setText("Command &Line:");

        txtCommandLine = new Text(shell, SWT.BORDER);
        txtCommandLine.setEditable(false);
        txtCommandLine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        lblResult = new Label(shell, SWT.NONE);
        lblResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));
        lblResult.setAlignment(SWT.CENTER);
        lblResult.setFont(SWTResourceManager.getFont("Lucida Console", 20, SWT.NORMAL));

        btnCopyClipboard = new Button(shell, SWT.NONE);
        btnCopyClipboard.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));
        btnCopyClipboard.setFont(SWTResourceManager.getFont("Tahoma", 16, SWT.NORMAL));
        btnCopyClipboard.setText("&Copy to Clipboard");

        compositeStatusbar = new Composite(shell, SWT.NONE);
        GridLayout gl_compositeStatusbar = new GridLayout(2, false);
        gl_compositeStatusbar.marginHeight = 0;
        gl_compositeStatusbar.marginWidth = 0;
        compositeStatusbar.setLayout(gl_compositeStatusbar);
        compositeStatusbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

        lblVersion = new Label(compositeStatusbar, SWT.NONE);
        lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblVersion.setToolTipText(Constants.LIB_ALL);
        lblVersion.setText("Java " + System.getProperty("java.version") + ", " + System.getProperty("os.name")
                + ", SWT " + SWT.getVersion());

        linkWebsite = new Link(compositeStatusbar, SWT.NONE);
        linkWebsite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
        linkWebsite.setText("<a>@GitHub</a>");

        addEventHandler();
        addDropTarget();
    }

    public void show() {
        Monitor primary = display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);

        shell.open();
        shell.pack();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        if (paru != null) {
            paru.stop();
        }

        try {
            display.dispose();
        }
        catch (SWTException ex) {
            ;
        }
    }

    private void addEventHandler() {
        addStartStopEventHandler();
        addCharEventHandler();

        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent evt) {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setText("Open");
                String dir = System.getProperty("user.home");
                if (dir == null || dir.trim().length() == 0) {
                    dir = File.listRoots()[0].getAbsolutePath();
                }
                fd.setFilterPath(dir);
                String[] filterExts = { "*.zip;*.xls;*.xlsx;*.pdf", "*.*" };
                String[] filterNames = { "All Supported Files (" + filterExts[0] + ")",
                        "All Files (" + filterExts[1] + ")" };
                fd.setFilterExtensions(filterExts);
                fd.setFilterNames(filterNames);
                String selected = fd.open();
                if (selected != null) {
                    txtFile.setText(selected);
                }
            }
        });

        txtFile.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                btnStart.setEnabled(txtFile.getText() != null && txtFile.getText().trim().length() > 0);
            }
        });

        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                genCommandLine();
            }
        });

        btnCommnadLine.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                genCommandLine();
            }
        });

        txtCommandLine.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtCommandLine.selectAll();
            }
        });

        txtCommandLine.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                txtCommandLine.selectAll();
            }
        });

        btnCopyClipboard.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String s = lblResult.getText();
                if (s != null && s.trim().length() > 0) {
                    Clipboard cb = new Clipboard(display);
                    TextTransfer textTransfer = TextTransfer.getInstance();
                    cb.setContents(new Object[] { s }, new Transfer[] { textTransfer });
                }
            }
        });

        linkWebsite.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (Desktop.isDesktopSupported()) {
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
        btnStart.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
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
                            display.asyncExec(new Runnable() {
                                public void run() {
                                    progressBar.setMinimum(0);
                                    progressBar.setMaximum(
                                            finalCombinations >= Integer.MAX_VALUE ? (int) Math.sqrt(finalCombinations)
                                                    : (int) finalCombinations);
                                    lblProgressResult.setText("" + finalCombinations);
                                }
                            });
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
                            display.asyncExec(new Runnable() {
                                @Override
                                public void run() {
                                    String totalStr = finalPerf.getTotal() > 1000000000
                                            ? String.format("%.2fB", finalPerf.getTotal() / 1000000000.0)
                                            : numfmt.format(finalPerf.getTotal());
                                    lblProgressResult.setText(numfmt.format(finalPerf.getCurrent()) + "/" + totalStr);
                                    progressBar.setSelection(finalPerf.getTotal() >= Integer.MAX_VALUE
                                            ? (int) Math.sqrt(finalPerf.getCurrent())
                                            : (int) finalPerf.getCurrent());
                                    lblEllipsedResult.setText(finalPerf.millisToStr(finalPerf.getEllapsed()));
                                    lblRemainingResult.setText(finalPerf.millisToStr(finalPerf.getRemaining()));
                                }
                            });
                        }
                    }, 50);
                }
                catch (ParseException ex) {
                    ;
                }
                catch (IOException ex) {
                    ;
                }

                if (paru != null) {
                    btnStop.setEnabled(true);
                    btnStart.setText(Constants.BTN_PAUSE);

                    paru.start();
                }
            }
        });

        btnStop.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent evt) {
                paru.stop();
            }
        });
    }

    private void addCharEventHandler() {
        spinCharMin.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (spinCharMax.getSelection() < spinCharMin.getSelection()) {
                    spinCharMax.setSelection(spinCharMin.getSelection());
                }
            }
        });

        spinCharMax.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (spinCharMin.getSelection() > spinCharMax.getSelection()) {
                    spinCharMin.setSelection(spinCharMax.getSelection());
                }
            }
        });
    }

    private void addDropTarget() {
        int operations = DND.DROP_COPY | DND.DROP_DEFAULT;
        DropTarget target = new DropTarget(shell, operations);

        final FileTransfer fileTransfer = FileTransfer.getInstance();
        Transfer[] types = new Transfer[] { fileTransfer };
        target.setTransfer(types);

        target.addDropListener(new DropTargetAdapter() {
            public void dragEnter(DropTargetEvent event) {
                if (event.detail == DND.DROP_DEFAULT) {
                    if ((event.operations & DND.DROP_COPY) != 0) {
                        event.detail = DND.DROP_COPY;
                    }
                    else {
                        event.detail = DND.DROP_NONE;
                    }
                }
                for (int i = 0; i < event.dataTypes.length; i++) {
                    if (fileTransfer.isSupportedType(event.dataTypes[i])) {
                        event.currentDataType = event.dataTypes[i];
                        // files should only be copied
                        if (event.detail != DND.DROP_COPY) {
                            event.detail = DND.DROP_NONE;
                        }
                        break;
                    }
                }
            }

            public void dragOver(DropTargetEvent event) {
                event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
            }

            public void dragOperationChanged(DropTargetEvent event) {
                if (event.detail == DND.DROP_DEFAULT) {
                    if ((event.operations & DND.DROP_COPY) != 0) {
                        event.detail = DND.DROP_COPY;
                    }
                    else {
                        event.detail = DND.DROP_NONE;
                    }
                }
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    if (event.detail != DND.DROP_COPY) {
                        event.detail = DND.DROP_NONE;
                    }
                }
            }

            public void drop(DropTargetEvent event) {
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    String[] files = (String[]) event.data;
                    if (files.length > 0) {
                        txtFile.setText(files[0]);
                    }
                }
            }
        });
    }

    private void onJobCompleted() {
        display.asyncExec(new Runnable() {
            @Override
            public void run() {
                btnStart.setText(Constants.BTN_START);
                btnStop.setEnabled(false);
            }
        });

        paru = null;
    }

    private void showResult(String str, int stage) {
        final String finalStr = str;
        final int finslStage = stage;

        display.asyncExec(new Runnable() {
            public void run() {
                FontData[] fd = lblResult.getFont().getFontData();
                if (finalStr.length() <= 30) {
                    if (fd[0].getHeight() != 20) {
                        fd[0].setHeight(20);
                        lblResult.setFont(new Font(display, fd[0]));
                    }
                }
                else {
                    if (fd[0].getHeight() != 12) {
                        fd[0].setHeight(12);
                        lblResult.setFont(new Font(display, fd[0]));
                    }
                }
                if (finslStage == 1) {
                    lblResult.setForeground(new Color(display, new RGB(0, 128, 0)));
                }
                else if (finslStage == 2) {
                    lblResult.setForeground(new Color(display, new RGB(128, 0, 0)));
                }
                else {
                    lblResult.setForeground(new Color(display, new RGB(0, 0, 0)));
                }
                lblResult.setText(finalStr);
            }
        });
    }

    private String[] genCommandLine(boolean display) {
        return tabFolder.getSelectionIndex() == 1
                ? Paru.getCharCmdLineArgs(txtFile.getText(), btnCharNumeric.getSelection(), btnCharLower.getSelection(),
                        btnCharUpper.getSelection(), btnCharSpecial.getSelection(), btnCharSpace.getSelection(),
                        txtCharCustom.getText(), spinCharMin.getSelection(), spinCharMax.getSelection(),
                        txtCharStart.getText(), txtCharEnd.getText(), txtCharPrefix.getText(), txtCharSuffix.getText(),
                        display)
                : Paru.getDateCmdLineArgs(txtFile.getText(), spinDateYear.getSelection(), txtDatePrefix.getText(),
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