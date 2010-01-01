/* $Header: /Users/blentz/rails_rcs/cvs/18xx/rails/ui/swing/MessagePanel.java,v 1.6 2009/12/29 21:58:11 evos Exp $*/
package rails.ui.swing;

import java.awt.*;
import javax.swing.*;

import rails.util.*;

public class MessagePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JLabel message;

    Color background = new Color(225, 225, 225);

    public MessagePanel() {
        super();

        setBackground(background);
        setLines(1);
        setBorder(BorderFactory.createLoweredBevelBorder());

        message = new JLabel("");
        message.setBackground(background);
        message.setVerticalAlignment(SwingConstants.CENTER);
        message.setHorizontalAlignment(SwingConstants.CENTER);
        message.setOpaque(true);
        add(message);
        message.setVisible(true);
        setVisible(true);
    }

    public void setMessage(String messageText) {
        if (messageText != null) {
            int lines = messageText.split("<[Bb][Rr]>").length + 1;
            setLines(lines);
            message.setText("<html><center>" + messageText + "</center></html>");
        }
    }

    public void setLines(int numberOfLines) {
        setSize(1000, numberOfLines * 12);
    }

}