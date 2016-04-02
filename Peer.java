

/* 
   PEER TO PEER FILE TRANSFER THROUGH CENTRALIZED SERVER
   The following is the code for peers:
*/
import java.io.File;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.math.*;
import javax.swing.JDialog; 



/**
 * @author Namratha, Parvathi, Rama, Sindhoora and Soundarya. 
 */


public class Peer{
    /** Main method for the program.
     *  Initializes peer information.
     *  @Param arg not used.
     *  @Return void.
    */
    public static void main(String args[]){
        try{
            
            BufferedReader inp=new BufferedReader(new InputStreamReader(System.in));
            String i="0";
            String ip="";
            while(i.equals("0")){
                System.out.println("Enter server's ip");
                ip=inp.readLine();
                System.out.println("Are you sure 1/0?");
                i=inp.readLine();
            }
            // sets Server's IP
            PeerIn.setServerIP(ip);
            i="0";
            String path="";
            while(i.equals("0")){
                System.out.println("Enter current directory path For ex:\n/Users/soundaryaramesh/Downloads/");
                System.out.println("C:\\Users\\Hedathri\\Documents\\NetBeansProjects\\trial");
                path=inp.readLine();
                System.out.println("Are you sure 1/0?");
                i=inp.readLine();
            }
            
        // this is the directory in which the files to be transferred are placed
            PeerIn.setPath(path);
            }catch(IOException f){}
        String myIP="";
        try{
        myIP=FindMyIP.getIP();
        }catch(Exception e2){}
	PeerIn.setIP(myIP);
	PeerIn.setUploadPortNo(18003);
        new First();
      
    }
}

// Displays the first welcome window

class First extends JFrame {
    public JLabel Label1;
    public JButton b1,b2;
    public JDialog d;
    
    First(){
        d= new JDialog();        
        Label1=new JLabel();
        Label1.setText("Welcome to P2P file transfer!");
        Label1.setBounds(100,20,300,40);
        
        b1=new JButton("Enter");
        b1.setBounds(110,80,150,40);
        ButtonHandler button=new ButtonHandler();
        b1.addActionListener(button);
        
        d.setTitle("Peer To Peer File Transfer");
        d.setSize(400,190);
        d.setLocationRelativeTo(null);
        d.setLayout(null);
        d.setVisible(true);
        d.add(Label1);
        d.add(b1);
        
          d.addWindowListener(new WindowAdapter(){
             @Override
             public void windowClosing(WindowEvent e){ 
                int i=JOptionPane.showConfirmDialog(null, "Exit anyway?", "Exit", 0);
                 if(i==1){
                     new First();
                 }
                 else{
                     JOptionPane.showMessageDialog(null,"exiting..");
                 }
                e.getWindow().dispose(); 
             }
         });
       
    }
        
    /** Calls the next window on clicking "Enter" button.
      * @Param ActionEvent e (Action Listener)
      * @Return void.
    */
  
   class ButtonHandler implements ActionListener
   {
       @Override
       public void actionPerformed(ActionEvent e){
        if(e.getSource()==b1){
            new UserWindow1();
            d.dispose();
        }
      }
    }
}

// Displays the second window containing a text box asking for peer-name

class UserWindow1 extends JFrame  {
        JFrame f;
        JLabel Label2;
        JTextArea Area1;
        JButton b1;
        String peerName;
        JDialog d;
        
        UserWindow1(){
            
            d=new JDialog();
            
            Label2=new JLabel();
            Label2.setText("Name");
            Label2.setBounds(20,80,40,25);
            
            Area1=new JTextArea();
            Area1.setBounds(80,80,150,25);
            
            b1=new JButton("Submit");
            b1.setBounds(90,150,100,40);
            
            ButtonHandler button=new ButtonHandler();
            b1.addActionListener(button);
            
            d.setTitle("Log in");
            d.setVisible(true);
            d.setLayout(null);
            d.setSize(300,270);
            d.setLocationRelativeTo(null);
            d.add(Label2);
            d.add(Area1);
            d.add(b1);
            
             d.addWindowListener(new WindowAdapter(){
             @Override
             public void windowClosing(WindowEvent e){ 
                int i=JOptionPane.showConfirmDialog(null, "Exit anyway?", "Exit", 0);
                 if(i==1){
                     new UserWindow1();
                 }
                 else{
                     JOptionPane.showMessageDialog(null,"exiting..");
                 }
                e.getWindow().dispose();
             }
         
         });
     
        }
    
        /**Spawns both client and server threads for each peer, enabling them to act as both server and client.
         * Uses multi-threading concept.
         * @Param ActionEvent e (Action Listener)
         * @Return void.
        */
   class ButtonHandler implements ActionListener     
    {
            @Override
            public void actionPerformed(ActionEvent e){
            if((e.getSource()==b1)&&!(Area1.getText()).equals("")){
                // Reads the peerName from the textbox
                peerName=Area1.getText();
                StringTokenizer name=new StringTokenizer(peerName," ");
                // Checks if the peerName has spaces in between
                if(!(name.nextToken().equals(peerName))){
                    JOptionPane.showMessageDialog(null,"No spaces allowed");
                    d.dispose();
                    new UserWindow1();
                }
                else{
                /* A peer acts both like a sender and receiver of files
                 At any point it should act as both a client(receiver of files) and a server(sender of files)
                 Hence separate PeerClient and PeerServer threads are spawned */
                PeerIn.peerName=peerName;
                PeerClientThread client=new PeerClientThread(peerName);
                ServerPeer server=new ServerPeer();
                client.start();
                System.out.println("Client thread has been called");
                server.start();
                System.out.println("Server thread has been called");
                d.dispose();
                }
            }
            else{
                JOptionPane.showMessageDialog(null,"Please enter a name!");
            }
        }
    }       
}
// the client part of the peer
class PeerClientThread extends Thread{
    Socket s;
    BufferedReader inp;
    PrintWriter outp;
    String peerName;
    PeerClientThread(String str){
        peerName=str;
    }
    /** Connects to the server through sockets and sends and receives the necessary information
      * @Param arg not used.
      * @Return void.
    */
    public void run(){
        try{
            s=new Socket(PeerIn.serverIP,18000);
            SocketClass.s=s;
            inp=new BufferedReader(new InputStreamReader(s.getInputStream()));
            SocketClass.inp=inp;
            outp=new PrintWriter(s.getOutputStream(),true);
            SocketClass.outp=outp;
            // peer sends it's ip address and upload port number details to the server through the socket
            outp.println(PeerIn.ipAddress);
            outp.println(PeerIn.uploadPortNumber);
            outp.println(peerName);
            String line;
            System.out.println(inp.readLine());
            StringTokenizer a=new StringTokenizer(inp.readLine(),",");
            // Receives the public key of RSA (which is used to encrypt the file) from the server
            PeerIn.n=new BigInteger(a.nextToken());
            PeerIn.e=new BigInteger(a.nextToken());
            new UserWindow2();
            
        }
        catch(IOException e){System.out.println(e);}
    }
}

// Displays the main menu containing add, download and exit radio buttons

class UserWindow2 extends JFrame  {
    Socket s=SocketClass.s;
    BufferedReader inp=SocketClass.inp;
    PrintWriter outp=SocketClass.outp;
    JFrame f;
    JLabel Label1;
    JRadioButton rb1,rb2,rb3;
    JButton b1;
    String choice;
    JDialog d;

    UserWindow2(){
        d=new JDialog();
        Label1=new JLabel();
        Label1.setText("What do you wish to do?");
        Label1.setBounds(70,20,200,30);
        
        rb1=new JRadioButton("Add new file.");
        rb1.setBounds(40,60,200,30);
        
        rb2=new JRadioButton("Download a file.");
        rb2.setBounds(40,100,200,30);
        
        rb3=new JRadioButton("Exit.");
        rb3.setBounds(40,140,200,30);
        
        ButtonGroup bg=new ButtonGroup();
        bg.add(rb1);
        bg.add(rb2);
        bg.add(rb3);
        
        ButtonHandler button=new ButtonHandler(); 
        b1=new JButton("OK");
        b1.setBounds(100,190,70,40);
        b1.addActionListener(button);
        
        d.setTitle("Menu");
        d.setSize(300,300);
        d.setLocationRelativeTo(null);
        d.setLayout(null);
        d.setVisible(true);
        d.add(Label1);
        d.add(rb1);
        d.add(rb2);
        d.add(rb3);
        d.add(b1);
    
         d.addWindowListener(new WindowAdapter(){
             @Override
             public void windowClosing(WindowEvent e){
                     JOptionPane.showMessageDialog(null,"Click on exit button");
                     e.getWindow().dispose();
                     new UserWindow2(); 
             }
         });
    }
     
 
    /** Takes input from user (add or download file & exit) and appropriate actions are performed.
      * @Param ActionEvent e (Action Listener)
      * @Return void.
    */
  class ButtonHandler implements ActionListener  
  {
      @Override
      public void actionPerformed(ActionEvent e){
        try{
            String line=null;
            int count=0;
            
            //Prints menu on console window.
            while(count<4){
                count++;
                 line=inp.readLine();
                System.out.println(line);
            }
            //Add File
            if(rb1.isSelected()){
                choice="1";
                outp.println(choice);
                new AddFile();
                d.dispose();
            }
            //Download File
            else if(rb2.isSelected()){
                choice="2";
                outp.println(choice);
                line=inp.readLine();
                System.out.println(line);
                String x=inp.readLine();
                if(x.equals("0")){
                    JOptionPane.showMessageDialog(null,"No files with the server yet!");
                    new UserWindow2();
                }
                else{
                    // Opens another window which contains the peer list
                    new LookUp(line);
                }
                // disposes the current window
                d.dispose();
            }
            //Exit
            else if(rb3.isSelected()){
                choice="3";
                outp.println(choice);
                System.out.println(inp.readLine());
                s.close();
                JOptionPane.showMessageDialog(null,"Connction removed!");
                System.exit(0);
            }
            //Default
            else{
                choice="4";
                outp.println(choice);
                JOptionPane.showMessageDialog(null,"Choose one option!");
                //dialogue box saying "choose one option"
                new UserWindow2();
                d.dispose();
            }
        }catch(IOException e1){}
    }
  }   
    
}

/*
  Objects of this class store information of the peer. 
*/
class PeerIn{
    static String serverIP;
    static int uploadPortNumber;
    static String ipAddress;
    static String peerName;
    static BigInteger n;
    static BigInteger e;
    static String currentDirectory;
    static ArrayList<String> myFilesList=new ArrayList<String>();
    /** Sets IP address of the server.
     * @Param String ip - IP address of the peer passed from the main method.
     * @Return void.
     */
    static void setServerIP(String ip){
        serverIP=ip;
    }
    /** Assigns the path of the current working directory to currentDirectory.
     * @Param String path - Current directory path from the main method.
     * @Return void.
     */
    static void setPath(String path){
        currentDirectory=path;
    }

  /** Sets IP address of the peer.
     * @Param String ip - IP address of the peer passed from the main method.
     * @Return void.
    */
    static void setIP(String ip){
	ipAddress=ip;
    }
    /**Sets upload port number of the peer.
      * @Param Integer port - Upload port number of the peer passed from the main method.
      * @Return void.
    */
    static void setUploadPortNo(int port){
	uploadPortNumber=port;
    }
    /** Adds names of files of the peer to the array list.
      * @Param String f - Name of the file to be added.
      * @Return 0 - if file name already exists, 1 - if file name is successfully added
    */
    static int addFiles(String f){
        Iterator itr=myFilesList.iterator();
        while(itr.hasNext()){
            String t=(String)itr.next();
            if(t.equals(f)){
                return 0;
            }
        }
        myFilesList.add(f);
        return 1;
    }
}

/* This is the client part of the peer. A peer receives the required file from another peer by connecting through sockets. Socket information is provided by the server */
class FileObtainment{
    String ip;
    int portNo;
    String fileName;
    String newFileName;
    Socket ns;
    BufferedReader ninp;
    PrintWriter noutp;
    FileWriter fr;
    BigInteger n;
    BigInteger d;
    

    public FileObtainment(String ip,int port,String file,BigInteger n,BigInteger d){
        this.ip=ip;
        portNo=port;
        fileName=file;
        this.n=n;
        this.d=d;
    }
    /** Establishes connection with the peer who has the required file.
      * File is received word my word. These words are combined into a single new file.
      * @Param arg not used.
      * @Return void.
    */
    public void start(){
		try{
		    System.out.println("Connection linked!!");
                    System.out.println("The received ip is "+ip+"  The portno is :"+portNo);
                    ns=new Socket(ip,portNo);
             // Helps read from the socket
            ninp=new BufferedReader(new InputStreamReader(ns.getInputStream()));
            // Helps write to the socket
		    noutp=new PrintWriter(ns.getOutputStream(),true);
            // File name is sent to the other peer
		    noutp.println(fileName);
		    System.out.println("File name sent!!");
		    newFileName=fileName;
                    fr=new FileWriter(newFileName);
		    String line;
		    while((line=ninp.readLine())!=null){
                if(!(line.equals("1"))){
                BigInteger encrypted=new BigInteger(line);
                BigInteger decrypted=decrypt(encrypted);
                fr.write(fromBigInteger(decrypted));
                fr.write(" ");
               }
                else{
                    fr.write("\n");
            }
            }
		    fr.close();
                    ns.close();
		    System.out.println("\n\nAnd here is the file for you!");
		    System.out.println(newFileName);
            // File is opened in write mode. An already existing file is re-written
		    BufferedReader br=new BufferedReader(new FileReader(newFileName));
		    while((line=br.readLine())!=null){
                        System.out.println(line);
		    }
		    br.close();
                    JOptionPane.showMessageDialog(null,newFileName+" has been downloaded successfully!");
                    new UserWindow2();
		      
            }catch(IOException e){}
    }
    // A method used for dencrypting a file
    public BigInteger decrypt(BigInteger encrypted) {
        return encrypted.modPow(d, n);
    }
    // A method used to convert a BigInteger to a String
    public String fromBigInteger(BigInteger big)
    {
        return new String(big.toByteArray());
    }
}

// Class used to add files to the server's database

class AddFile extends JFrame {
    Socket s=SocketClass.s;
    BufferedReader inp=SocketClass.inp;
    PrintWriter outp=SocketClass.outp;
    JFrame f;
   
    JTextField text;
    JButton b,b1;
    JList list;
    DefaultListModel listArray;
    String fileName;
    JDialog d;
    AddFile(){
        
        d=new JDialog();        
        
        ButtonHandler button=new ButtonHandler();
        
        text=new JTextField();
        text.setBounds(300,120,100,30);
        d.add(text);
        b=new JButton("Add File");
        b.setBounds(310,160,80,20);
        b.addActionListener(button);
        d.add(b);
        b1=new JButton("Menu");
        b1.setBounds(400,400,80,30);
        b1.addActionListener(button);
        d.add(b1);
        listArray=new DefaultListModel();
        /* a list of all text files in the current directory are displayed. This makes it convenient for the user to choose files */
        File f=new File(PeerIn.currentDirectory);
        File[] fArray=f.listFiles();
        for(File x:fArray){
            if(x.isFile()){
                if(x.getName().endsWith(".txt")){
                    listArray.addElement(x.getName());
                    
                }
                else
                    continue;
            }
        }

        
        list=new JList(listArray);
        list.setBounds(30,40,200,400);
        d.add(list);
        
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setBounds(30,40,200,400);
        d.add(listScroller);
        
        ListSelector lstHandler = new ListSelector();
        list.addListSelectionListener(lstHandler);
        list.addMouseListener(new MouseListener());
        
        d.setTitle("Add File");
        d.setSize(500,500);
        d.setLocationRelativeTo(null);
        d.setLayout(null);        
        d.setVisible(true);      
        
        d.addWindowListener(new WindowAdapter(){
             @Override
             public void windowClosing(WindowEvent e){
                     JOptionPane.showMessageDialog(null,"Click on menu to exit");
                     e.getWindow().dispose();
                     new AddFile(); 
             }
         });
    }       
    /** Adds file name entered by the peer to the server's list, only if it is valid.
      * @Param ActionEvent e (Action Listener)
      * @Retun void.
    */
    class ButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e){
      try{
        fileName=text.getText();
          int flag=0;
          if(e.getSource()==b && !(fileName.equals(""))){
              try{
                  BufferedReader buff=new BufferedReader(new FileReader(fileName));
              }catch(FileNotFoundException e10){
                  // when the file name entered is not found in the current directory, exception is raised
                  JOptionPane.showMessageDialog(null,"This file does not exist in the current directory. Re-enter");
                  flag=1;
                  new AddFile();
                  d.dispose();
              }
              if(flag==0&&(PeerIn.addFiles(fileName)==0)){
                  // when a file is added more than once, following message is displayed
                  JOptionPane.showMessageDialog(null,"This file is already added");
                  new AddFile();
                  d.dispose();
              }
              // If the file is in the current directory and not added before, it gets added successfully
              else if(flag==0){
              outp.println("1");
              System.out.println(inp.readLine());
              outp.println(fileName);
              System.out.println(inp.readLine());
              JOptionPane.showMessageDialog(null,"File added successfully!");
              new UserWindow2();
              d.dispose();  
              }
          }
          // if menu button is pressed, then menu window is displayed.
          else if(e.getSource()==b1)
          {     
              outp.println("0");
              new UserWindow2();
              d.dispose();
          }
          // when Add File button is pressed without entering any filename, following message is displayed
          else{
              JOptionPane.showMessageDialog(null,"Please enter a file name!");
          }
          }catch(IOException e1){}
    }
  }      
    public String getFile(){
        return fileName;
    }
    
    class MouseListener extends MouseAdapter
    {
        
        @Override
        public void mouseClicked(MouseEvent e){
      try{
        fileName=text.getText();
          int flag=0;
          // if the file is double-clicked also, the file gets added
          if(2==e.getClickCount()){
              try{
                  BufferedReader buff=new BufferedReader(new FileReader(fileName));
              }catch(FileNotFoundException e10){
                  JOptionPane.showMessageDialog(null,"This file does not exist in the current directory. Re-enter");
                  flag=1;
                  new AddFile();
                  d.dispose();
              }
              if(flag==0&&(PeerIn.addFiles(fileName)==0)){
                  JOptionPane.showMessageDialog(null,"This file is already added");
                  new AddFile();
                  d.dispose();
              }
              else if(flag==0){
              outp.println("1");
              System.out.println(inp.readLine());
              outp.println(fileName);
              System.out.println(inp.readLine());
              JOptionPane.showMessageDialog(null,"File added successfully!");
              new UserWindow2();
              d.dispose();  
             }
          }
        }catch(IOException e1){}
    }
 }
class ListSelector implements ListSelectionListener  {
        @Override
        /** Selected file name from the scroll list is set as the value in JLabel, text.
         * @Param ListSelectionEvent listSelectionEvent - similar to action listener.
         * @Return void.
         */
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            String s = list.getSelectedValue().toString();
            text.setText(s);
        }
    }
}


/*
  Details of the peer's own socket are stored in the objects of this class.
*/
class SocketClass {
     static Socket s;
     static BufferedReader inp;
     static PrintWriter outp;
}

/* Once download radio button is clicked, it leads the user to this window.
 All the files available with the server, not present with the current peer are displayed */


class LookUp extends JFrame { 
    Socket s=SocketClass.s;
    BufferedReader inp=SocketClass.inp;
    PrintWriter outp=SocketClass.outp;
    JFrame f;
    JLabel label2;
    JLabel text;
    JButton b,b1;
    JList list;
    DefaultListModel listArray;
    String fileList;
    JDialog d;
    
    LookUp(String line)
    { 
        d=new JDialog();
        
        label2=new JLabel();
        label2.setText("Chosen File");
        label2.setBounds(300,90,100,30);
        d.add(label2);
        
        text=new JLabel();
        text.setBounds(300,120,100,30);
        d.add(text);
        
        ButtonHandler button=new ButtonHandler();
        
        b=new JButton("Next");
        b.setBounds(300,160,80,20);
        b.addActionListener(button);
        d.add(b);
        
        b1=new JButton("Menu");
        b1.setBounds(400,400,80,30);
        b1.addActionListener(button);
        d.add(b1);
        
        // lists all the files available at the server, but not added with this very peer
        listArray=new DefaultListModel();
        fileList=line;
        StringTokenizer str=new StringTokenizer(line," ");
        while(str.hasMoreTokens()){
            listArray.addElement(str.nextToken());
        }
       
        list=new JList(listArray);
        list.setBounds(30,40,200,400);
        d.add(list);
        
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setBounds(30,40,200,400);
        d.add(listScroller);
        
        ListSelector lstHandler = new ListSelector();
        list.addListSelectionListener(lstHandler);
        list.addMouseListener(new MouseListener());
        
        d.setTitle("Files for Download");
        d.setSize(500,500);
        d.setLocationRelativeTo(null);
        d.setLayout(null);        
        d.setVisible(true);
        
         d.addWindowListener(new WindowAdapter(){
             @Override
             public void windowClosing(WindowEvent e){
                     JOptionPane.showMessageDialog(null,"Click on menu to exit");
                     e.getWindow().dispose();
                     new LookUp(fileList); 
             }
         });
    }   
    /** Sends selected file name to the server and receives list of peers with that file.
      * @Param ActionEvent e (Action Listener)
      * @Return void.
    */
    class ButtonHandler implements ActionListener
    {
     @Override
     public void actionPerformed(ActionEvent e){
        try{
            String st =text.getText();
            if(e.getSource()==b&&st.equals("")){
            JOptionPane.showMessageDialog(null,"Please choose a file!");
            d.dispose();
            new LookUp(fileList);
            }
            else if(e.getSource()==b1){
                outp.println("0");
                new UserWindow2();
                d.dispose();
            }
            else{
                outp.println("1");
                System.out.println(inp.readLine());
                outp.println(st);
                String str=inp.readLine();
                new PeerLookUp(st,str);
                d.dispose();
            } 
        }catch(IOException e1){}  
    }
    }   
    class MouseListener extends MouseAdapter{
        // A double click with the mouse will help you choose files too
        @Override
        public void mouseClicked(MouseEvent e)
        {
            try{
            if(e.getClickCount() == 2)
            { 
                String st =text.getText();
                outp.println("1");
                System.out.println(inp.readLine());
                outp.println(st);
                String str=inp.readLine();
                new PeerLookUp(st,str);
                d.dispose();
            }
            }
            catch(IOException e2){}
                    
        }
    }
    
    class ListSelector implements ListSelectionListener  {
       @Override
       /**
         * Selected file name from the scroll list is set as the value in JLabel, text.
         * @Param ListSelectionEvent listSelectionEvent - similar to action listener.
         * @Return void.
       */
       public void valueChanged(ListSelectionEvent listSelectionEvent) {
          String s = list.getSelectedValue().toString();
          text.setText(s);
       }
    }
}

/* Appears immpediately after choosing a file is done. In this window, all peers with the chosen file are displayed. */

class PeerLookUp extends JFrame { 
    Socket s=SocketClass.s;
    BufferedReader inp=SocketClass.inp;
    PrintWriter outp=SocketClass.outp;
    JFrame f;
    JLabel label2;
    JLabel text;
    JButton b,b1;
    JList list;
    DefaultListModel listArray;
    String peerList,fileName;
    JDialog d;
    PeerLookUp(String st,String line)
    {
        d=new JDialog();
    
        label2=new JLabel();
        label2.setText("Chosen Peer");
        label2.setBounds(300,90,100,30);
        d.add(label2);
        
        text=new JLabel();
        text.setBounds(300,120,100,30);
        d.add(text);
        
        ButtonHandler button=new ButtonHandler();
        
        b=new JButton("Next");
        b.setBounds(300,160,80,20);
        b.addActionListener(button);
        d.add(b);
     
        b1=new JButton("Menu");
        b1.setBounds(400,400,80,30);
        b1.addActionListener(button);
        d.add(b1);
        
        listArray=new DefaultListModel();
        peerList=line;
        this.fileName=st;
        StringTokenizer str=new StringTokenizer(line," ");
        while(str.hasMoreTokens()){
            listArray.addElement(str.nextToken());
        }
       
        list=new JList(listArray);
        list.setBounds(30,40,200,400);
        d.add(list);
        
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setBounds(30,40,200,400);
        d.add(listScroller);
        
        ListSelector lstHandler = new ListSelector();
        list.addListSelectionListener(lstHandler);
        list.addMouseListener(new MouseListener());
        
        d.setTitle("Peer List");
        d.setSize(500,500);
        d.setLocationRelativeTo(null);
        d.setLayout(null);        
        d.setVisible(true);
        
         d.addWindowListener(new WindowAdapter(){
             @Override
             public void windowClosing(WindowEvent e){
                     JOptionPane.showMessageDialog(null,"Click on menu to exit");
                     e.getWindow().dispose();
                     new PeerLookUp(fileName,peerList); 
             }
         });
    }
    /** Sends chosen peer name to the server.
      * Receives the peer's IP address and port number.
      * Calls File Obtainment method to download the file.
      * @Param ActionEvent e (Action Listener)
      * @Return void.
    */
    class ButtonHandler implements ActionListener
   {
        public void actionPerformed(ActionEvent e){
        try{
            String name=text.getText();
            if(e.getSource()==b&&name.equals("")){
                JOptionPane.showMessageDialog(null,"Please choose a peer!");
                d.dispose();
                new PeerLookUp(fileName,peerList);
            }
            // if menu option is chosen, menu window is displayed. Current window disposes.
            else if(e.getSource()==b1){
                    outp.println("0");
                    new UserWindow2();
                    d.dispose();
                }
            else{
                outp.println("1");
                System.out.println(inp.readLine());
                outp.println(name);
                //the server sends the peer details as a string separated by commas
                String str=inp.readLine();
                StringTokenizer p=new StringTokenizer(str,",");
                d.dispose();
                JOptionPane.showMessageDialog(null,"Connecting to peer...");
                String ip;
                int port=0;
                try{
                    port=Integer.parseInt(p.nextToken());
                }catch(NumberFormatException e3){}
                ip=p.nextToken();
                // the private key used to decrypt the file are also sent
                BigInteger n=new BigInteger(p.nextToken());
                BigInteger d=new BigInteger(p.nextToken());
                FileObtainment f=new FileObtainment(ip,port,fileName,n,d);
                outp.println(PeerIn.peerName+","+fileName);
                f.start();
                // once the file has been downloaded, this info gets updated in the server's database
                JOptionPane.showMessageDialog(null,fileName+" added to server's database");
                PeerIn.addFiles(fileName);
            }
        }catch(IOException e1){}  
    }
  }   
     class MouseListener extends MouseAdapter
   {
        @Override
        public void mouseClicked(MouseEvent e){
        try{
            // A double click with mouse will help you choose the peer
            String name=text.getText();
           
            if(e.getClickCount() == 2)
            {    outp.println("1");
                System.out.println(inp.readLine());
                outp.println(name);
                String str=inp.readLine();
                StringTokenizer p=new StringTokenizer(str,",");
                d.dispose();
                JOptionPane.showMessageDialog(null,"Connecting to peer...");
                String ip;
                int port=0;
                try{
                    port=Integer.parseInt(p.nextToken());
                }catch(NumberFormatException e3){}
                ip=p.nextToken();
                BigInteger n=new BigInteger(p.nextToken());
                BigInteger d=new BigInteger(p.nextToken());
                FileObtainment f=new FileObtainment(ip,port,fileName,n,d);
                outp.println(PeerIn.peerName+","+fileName);
                f.start();
                JOptionPane.showMessageDialog(null,fileName+" added to server's database");
                PeerIn.addFiles(fileName);
            }
        }catch(IOException e1){}  
    }
     }
    class ListSelector implements ListSelectionListener{
        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            String s = list.getSelectedValue().toString();
            text.setText(s);
        }
    }
}

/* This is the server part of the peer.
 It waits for requests and transfer files accordingly */

class ServerPeer extends Thread{
    /**Accepts connections from other peers in order to send a file.
      * @Param arg not used.
      * @Return void.
    */
    public void run(){
	try{
	    ServerSocket ser=new ServerSocket(18003);
// The peer is listening on the port 18003
	    System.out.println("Listening..");
            int count=0;
	    while(count<100){
            // when multiple peers request for files, file transfer must be handles by spawning threads
                Socket request=ser.accept();
                System.out.println("Connection established");
                ServerPeerThread peerServer=new ServerPeerThread(request);
                peerServer.start();
                count++;
            }
            ser.close();
	}catch(IOException e){}
    }
}

// When multiple peer-requests come, each one becomes an unique object of this class

class ServerPeerThread extends Thread{
    Socket client;
    BufferedReader inp3;
    PrintWriter outp3;
    BufferedReader br;
    BigInteger e=PeerIn.e;
    BigInteger n=PeerIn.n;
    public ServerPeerThread(Socket r){
	client=r;
    }
    /**Reads the file word by word and sends each word to the peer client.
      * @Param arg not used.
      * @Return void.
    */
    public void run(){
	try{
            inp3=new BufferedReader(new InputStreamReader(client.getInputStream()));
            outp3=new PrintWriter(client.getOutputStream(),true);
        /* synchronized, because if multiple peers try to download the same file, a lock should be placed on the file being read */
            synchronized(this){
                String file=inp3.readLine();
                new PrintWindow(file);
                System.out.println(file);
                // file is opened in read mode
                br=new BufferedReader(new FileReader(file));
                String line;
                while((line=br.readLine())!=null){
                    if(!(line.equals(""))){
                        StringTokenizer word=new StringTokenizer(line," ");
                        while(word.hasMoreTokens()){
                            BigInteger message=toBigInteger(word.nextToken());
                            // encrypted word is written to the socket
                            outp3.println(encrypt(message));
                        }
                    }
                        outp3.println("1");
                }
            }
        System.out.println("File sent");
            client.close();
             br.close();
        }catch(IOException e){}
    }
    class PrintWindow extends JFrame{
        PrintWindow(String file){
            JOptionPane.showMessageDialog(null,"File "+file+" is being transferred...");
        }
    }
    // this method helps encrypt the message
    public BigInteger encrypt(BigInteger message) {
        return message.modPow(e, n);
    }
    // this method converts a string into a BigInteger
    public BigInteger toBigInteger(String str)
    {
        return new BigInteger(str.getBytes());
    }

}


class FindMyIP {
    static String iP;
    /**System's IP address is found.
      * @Param arg not used.
      * @Return String iP - IP address of the system is returned.
    */
    public static String getIP() throws Exception {
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements();)
        {
            NetworkInterface e = n.nextElement();
            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();)
            {
                InetAddress addr = a.nextElement();
                String ip=addr.getHostAddress();
                StringTokenizer s=new StringTokenizer(ip,".");
                if(s.nextToken().equals("192"))
                    iP=ip;             
            }
        }
        return iP;
    }
}

