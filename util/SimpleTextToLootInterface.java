import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.apache.commons.lang.math.NumberUtils;

public class SimpleTextToLootInterface {

    static JFrame frame;
    static JTextArea text;
    static JButton btn;
    static JTextArea out;

    public static void main(String[] args) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        text = new JTextArea();
        text.setLineWrap(true);
        frame.setLayout(new GridLayout(3, 1));
        frame.add(text);
        btn = new JButton("Add");
        btn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String group = "";
                try {
                    int min = 1;
                    int max = 1;
                    double prb = 1;
                    double wt = 1;
                    int count = 0;
                    for (String t : text.getText().replace(" ","_").split("\n")) {
                        if (t.contains("-")) {
                            min = Integer.parseInt(t.split("-")[0]);
                            max = Integer.parseInt(t.split("-")[1]);
                        } else if (t.contains("%")) {
                            prb = Double.parseDouble(t.replace("%", "")) / 100.0;
                        } else if (t.contains("#")) {
                            wt = Double.parseDouble(t.replace("#", "")) / 100.0;
                        } else if (NumberUtils.isDigits(t)) {
                            min = Integer.parseInt(t);
                            max = min;
                        } else {
                            if (count > 0) group += ", ";
                            group += "new Loot(Material." + t.toUpperCase() + ", " + wt + ", " + min + ", " + max + ")";
                            count++;
                        }
                    }                     
                    if (count > 1) {
                        group = "new LootGroup(" + prb + "," + group + ")";
                    }
                    out.setText(group);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.toString());
                }
            }
        });
        frame.add(btn);
        out = new JTextArea();
        out.setLineWrap(true);
        frame.add(out);
        frame.setVisible(true);
    }
}
