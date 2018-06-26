package VideoCall;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class PMDialog extends javax.swing.JDialog {
    private final Socket pengirim;
    private final ObjectInputStream kepada;

    private final ObjectOutputStream socket;
    private final String input;
    private final String output;
    private JTextArea viewTextArea;
    private JTextField postTextField;
    private JButton postButton;
    private JScrollPane jScrollPanel;


    public PMDialog(ChatClient parent , Socket pengirim, ObjectInputStream kepada, ObjectOutputStream socket, String input, String output) {
        super(parent,false);


        this.pengirim = pengirim;
        this.kepada = kepada;
        this.socket = socket;
        this.input = input;
        this.output = output;
        setLocationRelativeTo(parent);
        initComponents();
    }

    void display(String res) {
        viewTextArea.setText(viewTextArea.getText() + res + "\n");
    }

    @SuppressWarnings("Unchecked")
    private void initComponents() {
        jScrollPanel = new javax.swing.JScrollPane();
        viewTextArea = new javax.swing.JTextArea();

        postTextField = new javax.swing.JTextField();
        postButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Private Message");

        viewTextArea.setEditable(false);
        viewTextArea.setColumns(20);
        viewTextArea.setLineWrap(true);
        viewTextArea.setRows(5);
        jScrollPanel.setViewportView(viewTextArea);


        postTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                postTextFieldActionPerformed(e);
            }
        });

        postButton.setText("Kirim");
        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                postButtonActionPerformed(e);
            }
        });
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        getContentPane().setLayout(groupLayout);
        groupLayout
                .setHorizontalGroup(
                        groupLayout
                                .createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(groupLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jScrollPanel , GroupLayout.DEFAULT_SIZE , 365  , Short.MAX_VALUE)
                                                .addGroup(groupLayout.createSequentialGroup()
                                                        .addComponent(postTextField)))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(postButton)
                                        .addContainerGap()
                                )
                );
        groupLayout
                .setVerticalGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPanel ,  GroupLayout.DEFAULT_SIZE ,263 , Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER))
                                        .addComponent(postTextField , GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE , GroupLayout.PREFERRED_SIZE)
                                        .addComponent(postButton)
                                        .addContainerGap())
                );

        pack();
    }

    private void postButtonActionPerformed(java.awt.event.ActionEvent evt){
        try {
            String message = "postPrivateText~"+pengirim+"~"+postTextField.getText()+"~"+kepada+"~\n";
            display(pengirim+": "+postTextField.getText()+"\n");
            socket.writeObject(message);
            postTextField.setText("");
        }catch (IOException ioex){
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ioex);
        }
    }

    private void postTextFieldActionPerformed(java.awt.event.ActionEvent evt){
        postButtonActionPerformed(evt);
    }

}