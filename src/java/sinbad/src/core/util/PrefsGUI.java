package core.util;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.WindowConstants;
import org.json.*;

/*
 This is really ugly gui code, but it's a quick hack to port the python version.
 */


public class PrefsGUI {

    private JSONObject prefs;
    private boolean firstTime;
    
    private JCheckBox chk_share_usage;
    private JButton usage_info_btn;
    private JCheckBox chk_check_updates;
    private JCheckBox chk_show_progress;
    private JButton save_btn;
    private JDialog dialog;

    public PrefsGUI() {
        this(false);
    }
    
    public PrefsGUI(boolean firstTime) {
        this.firstTime = firstTime;
        prefs = Preferences.loadPrefs();
        show();
        
        if (firstTime) {
            System.out.println(
                    "\n"
                    + "The Preferences GUI will is now terminating the program.\n"
                    + "Run your program again to resume normal execution.\n");
        } else {
            System.out.println(
                      "\n"
                      + "The Preferences GUI will is now terminating the program. Remove the\n"
                      + "call to .preferences() in your program and run it again to resume\n"
                      + "normal execution.\n");
        }
        System.exit(0);        
    }
    
    public void save() {
        boolean share_usage = chk_share_usage.isSelected();
        boolean check_updates = chk_check_updates.isSelected();
        boolean show_progress = chk_show_progress.isSelected();
        
        prefs.put("share_usage",     share_usage);
        prefs.put("notify_updates",  check_updates);
        prefs.put("print_load_progress",  show_progress);
        Preferences.savePrefs(prefs);
        
        //System.out.printf("%s %s %s\n", share_usage, check_updates, show_progress);
        dialog.dispose();
    }
    
    public void show() {        
        dialog = new JDialog(null,"Sinbad preferences", JDialog.ModalityType.APPLICATION_MODAL);
        
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.add(Box.createRigidArea(new Dimension(5, 0)));
        if (firstTime) {
            JPanel twoLine = new JPanel();
            twoLine.setLayout(new BoxLayout(twoLine, BoxLayout.Y_AXIS));
            twoLine.add(new JLabel("It looks like you've used the Sinbad library a few times now."));
            twoLine.add(new JLabel("Please take a moment to adjust your preferences.\n"));
            topRow.add(twoLine);
        } else {
            topRow.add(new JLabel("Adjust your preferences"));
        }
        topRow.add(Box.createHorizontalGlue());
        mainPane.add(topRow);
        
        mainPane.add(new JSeparator());
        
        JPanel usage = new JPanel();
        usage.setLayout(new BoxLayout(usage, BoxLayout.X_AXIS));
        chk_share_usage = new JCheckBox("Share usage & diagnostics information");
        usage_info_btn = new JButton("More info...");
        chk_share_usage.setEnabled(false);
        usage_info_btn.setEnabled(false);
        usage.add(chk_share_usage);
        usage.add(usage_info_btn);
        mainPane.add(usage);
        
        JPanel updates = new JPanel();
        updates.setLayout(new BoxLayout(updates, BoxLayout.X_AXIS));
        chk_check_updates = new JCheckBox("Check and notify for updates");
        updates.add(chk_check_updates);
        updates.add(Box.createHorizontalGlue());
        mainPane.add(updates);
        
        JPanel progress = new JPanel();
        progress.setLayout(new BoxLayout(progress, BoxLayout.X_AXIS));
        chk_show_progress = new JCheckBox("Print data set load progress");
        progress.add(chk_show_progress);
        progress.add(Box.createHorizontalGlue());
        mainPane.add(progress);
        
        mainPane.add(new JSeparator());

        JPanel btns = new JPanel();
        btns.setLayout(new BoxLayout(btns, BoxLayout.X_AXIS));
        save_btn = new JButton("Save");
        btns.add(Box.createHorizontalGlue());
        btns.add(save_btn);
        btns.add(Box.createHorizontalGlue());
        mainPane.add(btns);
        
        usage_info_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Preferences.launchBrowser(Preferences.loadPrefs().optString("server_base", "http://cs.berry.edu/sinbad")
                                             + "/usage.php");
            }
        });
        
        save_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        
        chk_share_usage.setSelected(prefs.optBoolean("share_usage", false));
        chk_check_updates.setSelected(prefs.optBoolean("notify_updates", false));
        chk_show_progress.setSelected(prefs.optBoolean("print_load_progress", true));
        
        dialog.add(mainPane);
        dialog.pack();
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setAlwaysOnTop(true);

        JRootPane rootPane = SwingUtilities.getRootPane(save_btn);
        if (rootPane != null) rootPane.setDefaultButton(save_btn);
                
        dialog.setVisible(true);        
    }
}
