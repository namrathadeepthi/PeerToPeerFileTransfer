/*
 PEER TO PEER TEXT FILE TRANSFER SYSTEM WITH                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             CENTRALIZED SERVER
 The following is the code for the SERVER.
 */

/* 
 Assumption:
 The server to peer connection is more secure than the peer to peer connection.
 This assumption is necessary as the priavte key of RSA used for decryption is sent by the server to the peer */
import java.net.*;
import java.io.*;
import java.util.*;
import java.math.*;




class Server{
    
    /*
     Main method of the program.
     Accepts connection from peers and spawns a new thread for each one.
     @Param arg not used.
     @Return void.
     */

    public static void main(String args[]){
    try{
	ServerSocket server=new ServerSocket(18000);
        /* Spawns a thread which helps quit the server */
    QuitThread q=new QuitThread(server);
    q.start();
	while(1==1){
        
        /* the while loop never breaks, implying any number of clients can connect to the server.
         the server's port is always listening and waiting for requests */

	    Socket s=server.accept();
	    System.out.println("A client just came in");
	    ServerThread serverThread=new ServerThread(s);
        serverThread.start();
	    
	}
	//server.close();
	
    }
    catch(IOException e){System.out.println(e);}
    }
}
/* A thread is always runnning which will quit when q is entered at the terminal */
class QuitThread extends Thread{
    ServerSocket s;
    public QuitThread(ServerSocket s){
        this.s=s;
    }
    public void run(){
        try{
        BufferedReader stdIn=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Press q to quit");
            if(stdIn.readLine().equals("q")){
                    //s.close();
                    System.exit(0);

            }
        }
        catch(IOException ee){}
    }
}

/* For each peer, a unique thread ServerThread is spawned. Each of it handles all the activities w.r.t that particular peer */

class ServerThread extends Thread{
    Socket s;
    BufferedReader inp;
    PrintWriter outp;
    BufferedReader stdIn;
    String peerName;
    String uploadPort;
    String ipAddress;
    static int count=0;
    ServerThread(Socket s){
	this.s=s;
	count++;
    }
    
    /*
     This method runs the server thread which updates the array lists-- that maintain a list of files,lists of peers and peerfileinfo that has the list of files and the names of peers that have the file.
     */

    
    public void run(){
	try{
        // rsa algorithm is implemented in the constructor of this class
        RSAImplement rsa=new RSAImplement();
	    inp=new BufferedReader(new InputStreamReader(s.getInputStream()));
	    outp=new PrintWriter(s.getOutputStream(),true);
	   //Inputs ip address, upload port and peer name from the peer
        ipAddress=inp.readLine();
	    uploadPort=inp.readLine();
	    peerName=inp.readLine();
	    outp.println("Connecting to server..");
        // When a peer connects to the server, key to encrypt(public key) is sent by the server
        outp.println(rsa.n+","+rsa.e);
	    PeerInfo peer=new PeerInfo();
	    System.out.println(ipAddress+" "+uploadPort);
        // creates an object of PeerInfo class and updates the arraylist peersInfo
     	    peer.setDetails(ipAddress,uploadPort,peerName,rsa.n,rsa.e,rsa.d);
	    PeersInformation.addPeer(peer);
	    System.out.println("Connected!");
	   
	    while(1==1){
		// Breaks the loop only when the client wants to exit
		outp.println("Enter\n1 to Add a file\n2 to Look-up for a file\n 3 to exit connection");
		int choice=0;
		try{
		choice=Integer.parseInt(inp.readLine());
		}catch(NumberFormatException e){}
            // When the peer wants to add a file
		if(choice==1){
            // When menu option is chosen at the peer,the loop should be continued

            if(inp.readLine().equals("0")){
                
                System.out.println("going back");
                continue;
            }
            else{
                outp.println("Enter file name");
                String input=inp.readLine();
                System.out.println(input);
                // creates a file object after receiving file name from the peer
                File f=new File(input);
                // updates the arraylists peersFileInfo and files
                PeersInformation.addFile(f);
                PeersInformation.addFileInfo(new PeerFileInfo(f,peerName));
                outp.println("File added successfully!");
                System.out.println("Files are");
                PeersInformation.displayFiles(peerName);
            }
		}
            // When a peer chooses to download a file
		else if(choice==2){
		    int flag=1;
		    flag=PeersInformation.displayFiles(s,peerName);
             //When there are no files  0 is sent to the peer part of the code
            if(flag==0){
                outp.println("0");
                continue;
		    }
            outp.println("1");
		    String file="";
            if(inp.readLine().equals("0"))
                    continue;
             outp.println("Choose a file");
            // Reads the filename from the peer
                file=inp.readLine();
            // displays all peers with the chosen file
                PeersInformation.displayPeerFileDetails(file);
                flag=PeersInformation.displayPeerFileDetails(s,file);
            if(inp.readLine().equals("0"))
                continue;
			outp.println("Choose peer");
            // Reads the name of the peer chosen
			String peeR=inp.readLine();
            // Sends the peer details to the peer
			flag=PeersInformation.displayPeerDetails(s,peeR);
            // Inputs the peer name and filename from the peer as a string separated by commas
            StringTokenizer woo=new StringTokenizer(inp.readLine(),",");
            String pp=woo.nextToken();
            String ff=woo.nextToken();
            // updates the arraylists peer and peersFileInfo with the newly added file's info
            PeersInformation.addFile(new File(ff));
            PeersInformation.addFileInfo(new PeerFileInfo(new File(ff),pp));
		}
            /* When a peer wants to disconnect the following code removes all his files and info
             from all the arraylists */
		else if(choice==3){
			PeersInformation.removeFileInfo(peerName);
			PeersInformation.removeFile(peerName);
			PeersInformation.removePeer(peerName);
			outp.println("Connection successfully removed");
			System.out.println(peerName+" left");
			break;
		}
            // when any other option is chosen, it's considered invalid
		else{
		    outp.println("Invalid choice");
		}
	    }
	    s.close();
	}
	catch(IOException e){System.out.println(e);}
    }
}

// An object of this class has also information about a particular peer

class PeerInfo{
    int portNo;
    String ipAddress;
    String peerName;
    BigInteger n;
    BigInteger e;
    BigInteger d;
    
    /**
     * This method sets details of peers; IP address, port number and name.
     * @Param String ip - IP address of the peer, String p - port number, String name - peer name
     * BigInteger n, e, d - variables used for RSA algorithm implementation.
     * @Return void.
     */

    public void setDetails(String ip,String p,String name,BigInteger n,BigInteger e,BigInteger d){
	try{
            portNo=Integer.parseInt(p);
	}catch(NumberFormatException e1){}
	ipAddress=ip;
	peerName=name;
        this.n=n;
        this.e=e;
        this.d=d;
    }
}
//An object of the folowing class holds all details of a peer w.r.t. the files he has.

class PeerFileInfo{
    File fileName;
    String peerName;
    PeerFileInfo(File file,String name){
	fileName=file;
	peerName=name;
    }
}
//An object of the following class holds details of the file to be dealt with.

class File{
    String fileName;
    File(String f){
	fileName=f;
    }
}

/*
 The following class holds information of all the peers in array lists.
 peersInfo holds basic information about each peer connected to the server.
 peersFilesInfo holds details of files belonging to each peer.
 files is a list of all files available to any peer to download.

 */

class PeersInformation{
    static ArrayList<PeerInfo> peersInfo=new ArrayList<PeerInfo>();
    static ArrayList<PeerFileInfo> peersFileInfo=new ArrayList<PeerFileInfo>();
    static ArrayList<File> files=new ArrayList<File>();
    
    /*
     Adds a file to the list of available files
     @Param Fil newFile - details of file to be added.
     @Reurn void..
     */
    
    /* the methods are synchronized as no two peers should access the arraylist at the same time */
    static synchronized void addFile(File newFile){
	Iterator itr=files.iterator();
	int flag=1;
	while(itr.hasNext()){
	    File file=(File)itr.next();
	    if(file.fileName.equals(newFile.fileName)){
		flag=0;
		break;
	    }
	}
	if(flag==1){
	    files.add(newFile);
	}
    }
    /*
     Removes the files specific to that  particular peer from the files arraylist when a peer disconnects.
     @Param String peerName - name of peer who wants to disconnect.
     @Return void.
     */

    static synchronized void removeFile(String peerName){
	Iterator itr1=files.iterator();
	ArrayList<File> fileToRemove=new ArrayList<File>();
	while(itr1.hasNext()){
	    int flag=1;
	    File f=(File)itr1.next();
	    Iterator itr2=peersFileInfo.iterator();
	    while(itr2.hasNext()){
		PeerFileInfo info=(PeerFileInfo)itr2.next();
		if(f.equals(info.fileName)){
		    flag=0;
		    break;
		}
	    }
	    if(flag==1)
		fileToRemove.add(f);
	}
	if(!fileToRemove.isEmpty())
	    files.removeAll(fileToRemove);
    }
    /**
     * Sends list of files available to download to the peer that asks for it.
     * @Param Socket s - peer who asks for the list, String peer - name of peer.
     * @Return void.
     */

    static synchronized int displayFiles(Socket s,String peer){
	
    
    try{
        int flag=0,count=0;
	    PrintWriter outp=new PrintWriter(s.getOutputStream(),true);
		Iterator itr=files.iterator();
		while(itr.hasNext()){
            flag=0;
		    File f=(File)itr.next();
             Iterator itr1=peersFileInfo.iterator();
            while(itr1.hasNext()){
                PeerFileInfo p=(PeerFileInfo)itr1.next();
                String dummy=p.fileName.fileName;
                if((p.peerName.equals(peer))&&(f.fileName.equals(dummy))){
                    flag=1;
                    break;
                }
            }
            if(flag==0){
      		    outp.print(" "+f.fileName);
                count++;
            }
        }
        outp.println("");
	       
        if(count==0){
                return 0;
        }
	}
	catch(IOException e){System.out.println(e);}
    
	return 1;
    }
    /**
     * Prints list of files available to download on the console.
     * @Param String peer - name of the peer
     * @Return void.
     */

    static synchronized void displayFiles(String peer){
        Iterator itr=files.iterator();
        int count=0,flag=0;
        while(itr.hasNext()){
            flag=0;
            File f=(File)itr.next();
            Iterator itr1=peersFileInfo.iterator();
            while(itr1.hasNext()){
                PeerFileInfo p=(PeerFileInfo)itr1.next();
                if(peer.equals(p.peerName)&&(f.fileName.equals(p.fileName))){
                    flag=1;
                    break;
                }
            }
            if(flag==0){
                System.out.print(f.fileName+" ");
                count++;
            }
        }
    }
    
    /**
     * Adds new peer to the array list.
     * @Param PeerInf peer - information of peer to be added.
     * @Return void.
     */
    
    static synchronized void addPeer(PeerInfo peer){
	peersInfo.add(peer);
    }
    
    /**
     * Removes a peer from the list once she/he sends request for exit.
     * @Param String peerName - name of peer to be removed.
     * @Return void.
     */
    
    static void removePeer(String peerName){
	Iterator itr=peersInfo.iterator();
	while(itr.hasNext()){
	    PeerInfo peer=(PeerInfo)itr.next();
	    if((peer.peerName).equals(peerName)){
		peersInfo.remove(peer);
		break;
	    }
	}
    }

    /**
     * Sends information of a peer to another peer.
     * Info includes ip address, port no, peer and the key to decrypt(private key)
     * @Param Socket s - peer who asked for the information, String pName - name of the peer whose information is being sent.
     * @Return 1 - if successful, 0 - if peer doesn't exist.
     */

    static synchronized int displayPeerDetails(Socket s,String pName){
	Iterator itr=peersInfo.iterator();
	int flag=0;
	try{
	    PrintWriter outp=new PrintWriter(s.getOutputStream(),true);
	    while(itr.hasNext()){
		PeerInfo peer=(PeerInfo)itr.next();
		if(peer.peerName.equals(pName)){
		    outp.print(""+peer.portNo);
		    outp.print(","+peer.ipAddress);
            outp.print(","+peer.n);
            outp.println(","+peer.d);
		    flag=1;
		    break;
        }
        }
    }
	catch(IOException e){System.out.println(e);}
	return 1;
    }
    
    /**
     * Adds new information about which peer has a file.
     * @Param PeerFileInfo - fileInfor - information of file to be added.
     * @Return void.
     */
    
    static synchronized void addFileInfo(PeerFileInfo fileInfo){
	peersFileInfo.add(fileInfo);
    }
    
    /**
     * Removes information about files/peers when they send request for exit.
     * @Param String peerName - name of the peer to be removed.
     * @Return void.
     */
    
    static void removeFileInfo(String peerName){
	Iterator itr=peersFileInfo.iterator();
	ArrayList<PeerFileInfo> fileToRemove=new ArrayList<PeerFileInfo>();
	while(itr.hasNext()){
	    PeerFileInfo info=(PeerFileInfo)itr.next();
	    if((info.peerName).equals(peerName)){
		fileToRemove.add(info);
	    }
	}
	if(!fileToRemove.isEmpty())
	    peersFileInfo.removeAll(fileToRemove);
    }

    /**
     * Sends list of peers having the selected file to the peer who wants to download it.
     * @Param Socket s - details of peer asking for the list of peers with the file, String file - name of the file.
     * @Return 1 - if successful, 0 - if peers don't exist.
     */

    static synchronized int displayPeerFileDetails(Socket s,String file){
	Iterator itr=peersFileInfo.iterator();
	int flag=0;
	try{
	    System.out.println("I'm in here");
	    PrintWriter outp=new PrintWriter(s.getOutputStream(),true);
	    while(itr.hasNext()){
		PeerFileInfo peer=(PeerFileInfo)itr.next();
		if(file.equals(peer.fileName.fileName)){
		    outp.print(" "+peer.peerName);
		    flag=1;
		}
	    }
	    if(flag==0){
		//outp.println("Invalid-file-name Try-again");
		return 0;
	    }
	    outp.println("");
	}
	catch(IOException e){System.out.println(e);}
	return 1;
	
    }
    /**
     * Prints list of peers having the selected file to download on the console.
     * @Param String file - name of file to be downloaded.
     * @Return void.
     */
    
    static synchronized void displayPeerFileDetails(String file){
        Iterator itr=peersFileInfo.iterator();
        int flag=0;
            System.out.println("The peers are");
            while(itr.hasNext()){
                PeerFileInfo peer=(PeerFileInfo)itr.next();
                if(file.equals(peer.fileName.fileName)){
                    System.out.println(" "+peer.peerName);
                    flag=1;
                }
            }
            if(flag==0){
                System.out.println("Invalid-file-name Try-again");
               
            }
            

    }
}

/*
 The following class implements RSA algorithm and generates the public and private key which will be sent to the 'the peer who sends the file' and 'the peer who wants the file' respectively.
 */


class RSAImplement{
    private BigInteger p;
    private BigInteger q;
    private BigInteger z;
    BigInteger n;
    BigInteger e;
    BigInteger d;
    int x = 2000;
    BigInteger one = new BigInteger("1");
    public RSAImplement() {
        Random rnd = new Random();
        p = BigInteger.probablePrime(x, rnd);
        q = BigInteger.probablePrime(x, rnd);
        z = (p.subtract(one)).multiply(q.subtract(one));
        n = p.multiply(q);
        e = BigInteger.probablePrime(x / 2, rnd);
        while (!(z.gcd(e).compareTo(one) == 0 && e.compareTo(z) < 0))
        {
            e= BigInteger.probablePrime(x/2,rnd);
        }
        d = e.modInverse(z);
    }
   }
