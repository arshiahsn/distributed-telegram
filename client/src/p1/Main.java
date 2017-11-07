//Client
package p1;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.ListeningPoint;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.swing.*;
import java.util.Timer;
import org.apache.log4j.BasicConfigurator;
import java.util.TimerTask;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;


public class Main extends JFrame implements SipListener{
    
    public class ScheduledTask extends TimerTask {
        public void run() {
            scheduledReg();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    JButton startButton = new JButton();
    JButton buttonRegisterStateless = new JButton();
    JButton buttonSendMessage = new JButton();
    
    JScrollPane scrollPane = new JScrollPane();
    JTextArea textArea = new JTextArea();
    JTextField myNameTextField = new JTextField("Name");
    JTextField myPortNumberTextField = new JTextField("5060");
    JTextField serverIpAddressTextField = new JTextField("sipsipsip.com");
    JTextField serverPortNumberTextField = new JTextField("5060");
    JTextField destinationIdTextField = new JTextField(6);
    //JTextField destinationServerTextField = new JTextField(6);
    //JTextField destinationPortTextField = new JTextField(6);
    JTextField messageBoxTextField = new JTextField("Message Content");
    
    JLabel mySipAddressLabel = new JLabel("My sip address: ");
    JLabel serverSipAddressLabel = new JLabel("Server Hostname: ");
    JLabel destinationIdLabel = new JLabel("Buddy Username");
    //JLabel destinationServerLabel = new JLabel("Address");
    //JLabel destinationPortLabel = new JLabel("Port");
    //JLabel atSignLabel = new JLabel("@");
    //JLabel sipLabel = new JLabel("sip:");
    
    private static final long serialVersionUID = 1L;
    // Objects used to communicate to the JAIN SIP API.
    SipFactory sipFactory;          // Used to access the SIP API.
    SipStack sipStack;              // The SIP stack.
    SipProvider sipProvider;        // Used to send SIP messages.
    MessageFactory messageFactory;  // Used to create SIP message factory.
    HeaderFactory headerFactory;    // Used to create SIP headers.
    AddressFactory addressFactory;  // Used to create SIP URIs.
    ListeningPoint listeningPoint;  // SIP listening IP address/port.
    Properties properties;          // Other properties.
    Boolean regFlag = false;		//Registration flag
    Long SeqNo = 1L;				//The Sequence Number of the flow
    
    Timer time = new Timer();				// Instantiate Timer Object
    ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
    Map<Long,String> seqNoList;				//List of Messages sent but not seen by the buddy yet
    
    // Objects keeping local configuration.
    String myIp;                      // The local IP address.
    String serverIp;
    int myPort =  Integer.valueOf(myPortNumberTextField.getText());                // The local port.
    int serverPort = 5060;
    
    String protocol = "udp";        // The local protocol (UDP).
    int tag = (new Random()).nextInt(); // The local tag.
    Address contactAddress;         // The contact address.
    ContactHeader contactHeader;    // The contact header.
    
    public InetAddress getCurrentIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
            .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) networkInterfaces
                .nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while(nias.hasMoreElements()) {
                    InetAddress ia= (InetAddress) nias.nextElement();
                    if (!ia.isLinkLocalAddress()
                        && !ia.isLoopbackAddress()
                        && ia instanceof Inet4Address) {
                        return ia;
                    }
                }
            }
        } catch (SocketException e) {
            this.textArea.append("unable to get current IP " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Creates new form SipClient
     */
    public Main() {
        try {
            initComponents();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    private void initComponents() throws UnknownHostException {
        
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SIP Client");
        setLocationByPlatform(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
            }
        });
        
        seqNoList = new HashMap<Long,String>();
        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        scrollPane.setViewportView(textArea);
        messageBoxTextField.setColumns(20);
        
        buttonRegisterStateless.setText("Register");
        buttonRegisterStateless.setEnabled(true);
        buttonRegisterStateless.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRegisterStateless(evt);
            }
        });
        
        buttonSendMessage.setText("Send Msg");
        buttonSendMessage.setEnabled(true);
        buttonSendMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSendMessage(evt);
            }
        });
        
        startButton.setText("Start");
        startButton.setEnabled(true);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onOpen();
            }
        });
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        layout.setHorizontalGroup(
                                  layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                  .addGroup(layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                      .addComponent(scrollPane)
                                                      .addGroup(layout.createSequentialGroup()
                                                                .addComponent(messageBoxTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(buttonSendMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 2, Short.MAX_VALUE))
                                                      .addGroup(layout.createSequentialGroup()
                                                                //.addComponent(sipLabel , javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(destinationIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                //.addComponent(atSignLabel , javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                //.addComponent(destinationServerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                //.addComponent(destinationPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGap(0, 2, Short.MAX_VALUE))
                                                      .addGroup(layout.createSequentialGroup()
                                                                .addComponent(destinationIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                //.addComponent(destinationServerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                //.addComponent(destinationPortLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGap(0, 2, Short.MAX_VALUE))
                                                      .addGroup(layout.createSequentialGroup()
                                                                .addComponent(serverIpAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(serverPortNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(buttonRegisterStateless, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGap(0, 2, Short.MAX_VALUE))
                                                      .addComponent(serverSipAddressLabel)
                                                      .addGroup(layout.createSequentialGroup()
                                                                .addComponent(myNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(myPortNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGap(0, 2, Short.MAX_VALUE))
                                                      .addComponent(mySipAddressLabel))
                                            .addContainerGap())
                                  );
        
        layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addComponent(mySipAddressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(myNameTextField)
                                                    .addComponent(myPortNumberTextField))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(serverSipAddressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(serverIpAddressTextField)
                                                    .addComponent(serverPortNumberTextField)
                                                    .addComponent(startButton)
                                                    .addComponent(buttonRegisterStateless))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(destinationIdLabel))
                                                    //.addComponent(destinationServerLabel)
                                                    //.addComponent(destinationPortLabel))
                                          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    //.addComponent(sipLabel)
                                                    .addComponent(destinationIdTextField))
                                                    //.addComponent(atSignLabel)
                                                    //.addComponent(destinationServerTextField)
                                                    //.addComponent(destinationPortTextField))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                    .addComponent(messageBoxTextField)
                                                    .addComponent(buttonSendMessage))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                          .addContainerGap())
                                );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    private void onOpen() {//GEN-FIRST:event_onOpen
        // A method called when you open your application.
        
        try {
            // Get the local IP address.            
            this.myIp = getCurrentIp().toString().substring(1);
            this.myPort = Integer.parseInt(myPortNumberTextField.getText());
            this.serverIp = serverIpAddressTextField.getText();
            serverIp = java.net.InetAddress.getByName(serverIp).toString();
            this.serverPort = Integer.parseInt(serverPortNumberTextField.getText());
            
            // Create the SIP factory and set the path name.
            this.sipFactory = SipFactory.getInstance();
            this.sipFactory.setPathName("gov.nist");
            // Create and set the SIP stack properties.
            this.properties = new Properties();
            this.properties.setProperty("javax.sip.STACK_NAME", "stack");
            // Create the SIP stack.
            this.sipStack = this.sipFactory.createSipStack(this.properties);
            // Create the SIP message factory.
            this.messageFactory = this.sipFactory.createMessageFactory();
            // Create the SIP header factory.
            this.headerFactory = this.sipFactory.createHeaderFactory();
            // Create the SIP address factory.
            this.addressFactory = this.sipFactory.createAddressFactory();
            // Create the SIP listening point and bind it to the local IP address, port and protocol.
            this.listeningPoint = this.sipStack.createListeningPoint("0.0.0.0", this.myPort, this.protocol);
            // Create the SIP provider.
            this.sipProvider = this.sipStack.createSipProvider(this.listeningPoint);
            // Add our application as a SIP listener.
            this.sipProvider.addSipListener(this);
            // Create the contact address used for all SIP messages.
            this.contactAddress = this.addressFactory.createAddress("sip:" + this.myIp + ":" + this.myPort);
            // Create the contact header used for all SIP messages.
            this.contactHeader = this.headerFactory.createContactHeader(contactAddress);
            // Display the local IP address and port in the text area.
            this.textArea.append("Local address: " + this.myIp + ":" + this.myPort + "\n");
            
            //destinationIdTextField.setText("sip:alice@"+ this.serverIp +":" + this.serverPort);
            destinationIdTextField.setText("Example");
            //destinationServerTextField.setText(this.serverIp.toString());
            //destinationPortTextField.setText("5060");
        }
        catch(Exception e) {
            // If an error occurs, display an error message box and exit.
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }
    
    private void scheduledReg()
    {
        try
        {
            // Get the destination address from the text field.
            Address addressTo = this.addressFactory.createAddress("sip:"+myNameTextField.getText()+"@"+ this.serverIp +":" + this.serverPort);
            Address addressFrom = this.addressFactory.createAddress("sip:"+myNameTextField.getText()+"@"+serverIp+":"+serverPort);
            // Create the request URI for the SIP message.
            javax.sip.address.URI requestURI = addressTo.getURI();
            // Create the SIP message headers
            
            // The "Via" headers.
            ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
            ViaHeader viaHeader = this.headerFactory.createViaHeader(this.serverIp, this.serverPort, "udp", null);
            viaHeaders.add(viaHeader);
            // The "Max-Forwards" header.
            MaxForwardsHeader maxForwardsHeader = this.headerFactory.createMaxForwardsHeader(70);
            // The "Call-Id" header.
            CallIdHeader callIdHeader = this.sipProvider.getNewCallId();
            // The "CSeq" header.
            CSeqHeader cSeqHeader = this.headerFactory.createCSeqHeader(SeqNo,"REGISTER");
            // The "From" header.
            FromHeader fromHeader = this.headerFactory.createFromHeader(this.contactAddress, String.valueOf(this.tag));
            // The "To" header.
            ToHeader toHeader = this.headerFactory.createToHeader(addressTo, null);
   
            // Create the REGISTER request.
            Request request = this.messageFactory.createRequest(
                                                                requestURI,
                                                                "REGISTER",
                                                                callIdHeader,
                                                                cSeqHeader,
                                                                fromHeader,
                                                                toHeader,
                                                                viaHeaders,
                                                                maxForwardsHeader);
            // Add the "Contact" header to the request.
            request.addHeader(contactHeader);
            
            // Send the request statelessly through the SIP provider and add to SeqNom.
            this.sipProvider.sendRequest(request);
            SeqNo++;
        }
        catch(Exception e) {
            // If an error occurred, display the error.
            this.textArea.append("Request sent failed: " + e.getMessage() + "\n");
        }
    }
    
    private void onRegisterStateless(java.awt.event.ActionEvent evt) {
        try {
            this.textArea.append("Initiating registeration...\n");
            time.schedule(st, 0, 4000); // Create Repetitively task for every 1 secs
        }
        catch(Exception e) {
            // If an error occurred, display the error.
            this.textArea.append("Request sent failed: " + e.getMessage() + "\n");
        }
        
    }//GEN-LAST:event_onRegisterStateless
    
    private void onSendMessage(java.awt.event.ActionEvent evt) {
        try {
            
            // Get the destination address from the text field.
            //Address addressTo = this.addressFactory.createAddress(this.destinationSipAddressTextField.getText());
            Address addressTo = this.addressFactory.createAddress("sip:"+destinationIdTextField.getText()+"@"+serverIp+":5060");
            Address addressFrom = this.addressFactory.createAddress("sip:"+myNameTextField.getText()+"@"+serverIp+":"+serverPort);
            // Create the request URI for the SIP message.
            //URI requestURI = addressTo.getURI();
            URI requestURI = this.addressFactory.createAddress("sip:"+serverIp+":"+serverPort).getURI();
            // The "Via" headers.
            ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
            ViaHeader viaHeader = this.headerFactory.createViaHeader(this.serverIp, this.serverPort, "udp", null);
            viaHeaders.add(viaHeader);
            // The "Max-Forwards" header.
            MaxForwardsHeader maxForwardsHeader = this.headerFactory.createMaxForwardsHeader(70);
            // The "Call-Id" header.
            CallIdHeader callIdHeader = this.sipProvider.getNewCallId();
            // The "CSeq" header.
            CSeqHeader cSeqHeader = this.headerFactory.createCSeqHeader(SeqNo,"MESSAGE");
            // The "From" header.
            FromHeader fromHeader = this.headerFactory.createFromHeader(addressFrom, String.valueOf(this.tag));
            // The "To" header.
            ToHeader toHeader = this.headerFactory.createToHeader(addressTo, null);
            
            // Create the MESSAGE request.
            Request request = this.messageFactory.createRequest(
                                                                requestURI,
                                                                "MESSAGE",
                                                                callIdHeader,
                                                                cSeqHeader,
                                                                fromHeader,
                                                                toHeader,
                                                                viaHeaders,
                                                                maxForwardsHeader);
            // Add the "Contact" header to the request.
            request.addHeader(contactHeader);
            ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text", "plain");
            request.setContent(">>"+ messageBoxTextField.getText() +"<<", contentTypeHeader);
            
            // Send the request statelessly through the SIP provider.
            this.sipProvider.sendRequest(request);
            //add this message in the list,cause it just has been sent
            seqNoList.put(SeqNo, messageBoxTextField.getText());
            //System.out.println("\nMessage SCeq is : " + SeqNo.toString());
            SeqNo++;
            // Display the message in the text area.
        }
        catch(Exception e) {
            // If an error occurred, display the error.
            this.textArea.append("Message Request sent failed: " + e.getMessage() + "\n");
        }
    }
    
    private void onInvite(java.awt.event.ActionEvent evt) {
        try {
            
            // Get the destination address from the text field.
            Address addressTo = this.addressFactory.createAddress("sip:"+destinationIdTextField.getText()+"@"+serverIp+":5060");
            Address addressFrom = this.addressFactory.createAddress("sip:"+myNameTextField.getText()+"@"+serverIp+":"+serverPort);
            javax.sip.address.URI requestURI = addressTo.getURI();
            // Create the SIP message headers.
            
            // The "Via" headers.
            ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
            ViaHeader viaHeader = this.headerFactory.createViaHeader(this.serverIp, this.serverPort, "udp", null);
            viaHeaders.add(viaHeader);
            // The "Max-Forwards" header.
            MaxForwardsHeader maxForwardsHeader = this.headerFactory.createMaxForwardsHeader(70);
            // The "Call-Id" header.
            CallIdHeader callIdHeader = this.sipProvider.getNewCallId();
            // The "CSeq" header.
            CSeqHeader cSeqHeader = this.headerFactory.createCSeqHeader(1L,"INVITE");
            // The "From" header.
            FromHeader fromHeader = this.headerFactory.createFromHeader(addressFrom, String.valueOf(this.tag));
            // The "To" header.
            ToHeader toHeader = this.headerFactory.createToHeader(addressTo, null);            
            //The "Contact" header
            ContactHeader contactHeader = this.headerFactory.createContactHeader(this.contactAddress);
            
            // Create the INVITE request.
            Request request = this.messageFactory.createRequest(
                                                                requestURI,
                                                                "INVITE",
                                                                callIdHeader,
                                                                cSeqHeader,
                                                                fromHeader,
                                                                toHeader,
                                                                viaHeaders,
                                                                maxForwardsHeader);
            request.addHeader(contactHeader);
            
            // Send the request statelessly through the SIP provider.
            this.sipProvider.sendRequest(request);
            
            // Display the message in the text area.
            this.textArea.append(
                                 "Request sent:\n" + request.toString() + "\n\n");
        }
        catch(Exception e) {
            // If an error occurred, display the error.
            this.textArea.append("Request sent failed: " + e.getMessage() + "\n");
            
        }
    }
    
    private void onBye(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onBye
        // A method called when you click on the "Bye" button.
    }//GEN-LAST:event_onBye
    
    /**
     * @param args the command line arguments
     */
    public static void main(String... args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        BasicConfigurator.configure();
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
        
        
    }
    
    
    @Override
    public void processRequest(RequestEvent requestEvent) {
        Request request = requestEvent.getRequest();
        // Display the response message in the text area.
        if(request.getMethod().equals("MESSAGE"))
        {
            String buffer = request.toString();
            buffer = buffer.substring(buffer.indexOf(">>")+2, buffer.indexOf("<<"));
            String tempId = ""+request.getHeader("From");
            tempId = tempId.substring((tempId.indexOf("<")+5), (tempId.indexOf("@")));
            this.textArea.append(tempId+": \n");
            this.textArea.append("> "+buffer+"\n");
            
            //Return 200 OK as seen
            try {
                
                // Get the destination address from the text field.
                //Address addressTo = this.addressFactory.createAddress(this.destinationSipAddressTextField.getText());
                String tempAddressTo = request.getHeader(FromHeader.NAME).toString();
                tempAddressTo = tempAddressTo.substring(tempAddressTo.indexOf("<")+5, tempAddressTo.indexOf("@"));
                Address addressTo = this.addressFactory.createAddress("sip:"+tempAddressTo+"@"+serverIp+":"+serverPort);
                Address addressFrom = this.addressFactory.createAddress("sip:"+myNameTextField.getText()+"@"+serverIp+":"+serverPort);
                System.out.println("\nSending SEEN 200 OK to : " + addressTo.toString());
                // Create the request URI for the SIP message.
                //URI requestURI = addressTo.getURI();
                URI requestURI = this.addressFactory.createAddress("sip:"+serverIp+":"+serverPort).getURI();
                // The "Via" headers.
                ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
                ViaHeader viaHeader = this.headerFactory.createViaHeader(this.serverIp, this.serverPort, "udp", null);
                viaHeaders.add(viaHeader);
                // The "Max-Forwards" header.
                MaxForwardsHeader maxForwardsHeader = this.headerFactory.createMaxForwardsHeader(70);
                // The "Call-Id" header.
                CallIdHeader callIdHeader = this.sipProvider.getNewCallId();
                // The "CSeq" header.
                CSeqHeader cSeqHeader = (CSeqHeader)request.getHeader(CSeqHeader.NAME);               
                // The "From" header.
                FromHeader fromHeader = this.headerFactory.createFromHeader(addressFrom, String.valueOf(this.tag));
                // The "To" header.
                ToHeader toHeader = this.headerFactory.createToHeader(addressTo, null);
                
                // Create the MESSAGE request.
                Response seenResponse = this.messageFactory.createResponse(
                                                                       200,
                                                                       callIdHeader,
                                                                       cSeqHeader,
                                                                       fromHeader,
                                                                       toHeader,
                                                                       viaHeaders,
                                                                       maxForwardsHeader);
                // Add the "Contact" header to the request.
                seenResponse.addHeader(contactHeader);
                System.out.println("Seen Response : " + seenResponse.toString());
                // Send the request statelessly through the SIP provider.
                this.sipProvider.sendResponse(seenResponse);
            }
            catch(Exception e) {
                // If an error occurred, display the error.
                this.textArea.append("Seen 200OK sent failed: " + e.getMessage() + "\n");
            }
            System.out.println("\nSeen 200OK sent!");
        }     
    }
    
    @Override
    public void processResponse(ResponseEvent responseEvent) {
        // A method called when you receive a SIP request.
        // Get the response.
        Response response = responseEvent.getResponse();
        // Display the response message in the text area.
        if(response.getStatusCode()== Response.OK)
        {
        	//if cSeq is 1 then this 200OK refers to a registration otherwise it refers to a message
        	CSeqHeader cSeq = (CSeqHeader)response.getHeader(CSeqHeader.NAME);
        	if(cSeq.getSeqNumber() == 1l && !regFlag) {
	            this.textArea.append("Registeration successful.\n");
	            regFlag = true;
        	}
        	else {
        		//the buddy has seen your message :)
                System.out.println("\nReceived Seen 200OK!");
                Long tempSeq = cSeq.getSeqNumber();
                System.out.println("\n" + tempSeq.toString());
                if(seqNoList.containsKey(tempSeq))
                {
                    System.out.println("\nThis Seq is in the list!");
                    String fromString = ""+response.getHeader("From");
                    fromString = fromString.substring((fromString.indexOf("<")+5), (fromString.indexOf("@")));
                    this.textArea.append("\n'"+seqNoList.get(tempSeq)+"' delievered to "+fromString+"\n");
                    //remove it from buffer as it's been seen
                    seqNoList.remove(tempSeq);
                }
        	}
        }
        if(response.getStatusCode()== Response.NOT_FOUND)
        {
            //Remove message from buffer if not delievered
            this.textArea.append("User not found!\n");
            CSeqHeader cseqHeader = (CSeqHeader)response.getHeader(CSeqHeader.NAME);
            if(seqNoList.containsKey(cseqHeader.getSeqNumber()))
                seqNoList.remove(cseqHeader.getSeqNumber());
        }
        if(response.getStatusCode()==Response.TOO_MANY_HOPS)
            this.textArea.append("Too many hops to the destination!\n");
    }
    
    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        // A method called when a SIP operation times out.
    }
    
    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        // A method called when a SIP operation results in an I/O error.
    }
    
    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        // A method called when a SIP transaction terminates.
    }
    
    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        // A method called when a SIP dialog terminates.
    }
}
