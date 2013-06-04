package org.rapla.plugin.freetime.client;

import java.util.Locale;

import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContext;
import org.rapla.gui.DefaultPluginOption;
import org.rapla.plugin.freetime.FreetimePlugin;


public class FreetimeAdminOptions extends DefaultPluginOption {

//    private JLabel lbForegroundColor;
//    private JColorChooser ccForeground;
//    private JLabel lbBackgroundColor;
//    private JColorChooser ccBackground;

    public FreetimeAdminOptions(RaplaContext raplaContext) throws Exception {
        super(raplaContext);
        setChildBundleName(FreetimePlugin.RESOURCE_FILE);
        initJComponents();

    }


    private void initJComponents() {

//        this.lbForegroundColor = new JLabel(getString("foreground"));
//        this.ccForeground = new JColorChooser();
//
//        this.lbBackgroundColor = new JLabel(getString("background"));
//        this.ccBackground = new JColorChooser();

        //todo: add customizable resourcetyp and resource

    }


//    /* (non-Javadoc)
//     * @see org.rapla.gui.DefaultPluginOption#createPanel()
//     */
//    protected JPanel createPanel() throws RaplaException {
//        JPanel parentPanel = super.createPanel();
//        JPanel content = new JPanel();
//        double[][] sizes = new double[][]{
//                {5, TableLayout.PREFERRED, 5, TableLayout.FILL, 5}
//                , {TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED}
//        };
//        TableLayout tableLayout = new TableLayout(sizes);
//        content.setLayout(tableLayout);
//        content.add(lbForegroundColor, "1,0");
//        content.add(ccForeground, "3,0");
//        content.add(lbBackgroundColor, "1,2");
//        content.add(ccBackground, "3,2");
//        parentPanel.add(new JScrollPane(content), BorderLayout.CENTER);
//        return parentPanel;
//    }
//
//    protected void addChildren(DefaultConfiguration newConfig) {
//        readUserInputValues();
//        FreetimePlugin.storeParametersToConfig(newConfig);
//    }
//
//    private void readUserInputValues() {
//        FreetimePlugin.BACKGROUND_COLOR = ccBackground.getColor();
//        FreetimePlugin.FOREGROUND_COLOR = ccForeground.getColor();
//    }
//
//
//    protected void readConfig(Configuration config) {
//        FreetimePlugin.loadConfigParameters(config);
//        ccBackground.setColor(FreetimePlugin.BACKGROUND_COLOR);
//        ccForeground.setColor(FreetimePlugin.FOREGROUND_COLOR);
//
//    }


    public Class<? extends PluginDescriptor<?>> getPluginClass() {
        return FreetimePlugin.class;
    }

    public String getName(Locale locale) {
        return "Freetime Service Options";
    }


}



