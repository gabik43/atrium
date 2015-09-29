package atrium.fx.ru.print;

import javax.print.PrintService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Timofey Boldyrev on 24.09.2015.
 */
public class PrintDialog extends JDialog {

    private PrinterTask printerTask;
    private PrintService[] ps;

    public PrintDialog(PrinterTask printerTask, PrintService[] ps) {
        super();
        this.printerTask = printerTask;
        this.ps = ps;
        create();
        setVisible(true);
    }

    private void create() {

        setTitle("Печать");
        setResizable(false);

        String[] listToComboBox = new String[ps.length];
        for (int i=0; i<ps.length; i++) {
            listToComboBox[i] = ps[i].getName();
        }

        final JComboBox comboBox = new JComboBox(listToComboBox);
        JButton[] button = new JButton[2];
        button[0] = new JButton("Отмена");
        button[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        button[1] = new JButton("OK");
        button[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrintService selectedPrintService = ps[comboBox.getSelectedIndex()];
                dispose();
                printerTask.startPrint(selectedPrintService);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(comboBox);
        panel.add(button[0]);
        panel.add(button[1]);

        add(panel);
        pack();
        setLocationRelativeTo(null);

    }


}
