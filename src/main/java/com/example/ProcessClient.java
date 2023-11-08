package com.example;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;

public class ProcessClient implements Runnable {
    private Socket clientSocket;
    private String ipAdress = null;
    private String rootDirServer = null;
    DataInputStream dis = null;
    DataOutputStream dos = null;
    FileInputStream fis = null;
    FileOutputStream fos = null;
    String ipAddress = null;
    

    public ProcessClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.ipAdress = clientSocket.getInetAddress().getHostAddress();
        this.rootDirServer = System.getProperty("user.dir") + "\\" + "PublicFolder";
        SupportFunc.getInstance().createFolder(rootDirServer);
    }

    @Override
    public void run() {
        try {
            String ipAdress = clientSocket.getInetAddress().getHostAddress();
            dis = new DataInputStream(clientSocket.getInputStream());
            dos = new DataOutputStream(clientSocket.getOutputStream());
            while (true) {

                System.out.println("Watting command from client " + ipAdress);
                String command = dis.readUTF();
                if (command.equals("SEND_FILE")) {

                    System.out.println("Client " + ipAdress + " is sending file to server");
                    receiveFile(clientSocket);

                } else if (command.equals("RECEIVE_FILE")) {

                    System.out.println("Client " + ipAdress + " is receiving file from server");
                    sendFile(clientSocket);

                } else if (command.equals("SHOW_LIST_FILE")) {
                    showFile(clientSocket);
                    System.out.println("Client " + ipAdress + " show list file successfully");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showFile(Socket clientSocket) {
        try {
            String folderPath = dis.readUTF();
            System.out.println("Current folder: " + folderPath);
            File folder = new File(folderPath);
            File[] listOfFolder = folder.listFiles();
            String listFiles = "";
            String listDirectories = "";

            for (int i = 0; i < listOfFolder.length; i++) {
                if (listOfFolder[i].isFile()) {
                    listFiles += listOfFolder[i].getName() + "\n";
                } else if (listOfFolder[i].isDirectory()) {
                    listDirectories += listOfFolder[i].getName() + "\n";
                }
            }
    
            dos.writeUTF(listFiles);
            dos.writeUTF(listDirectories);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    

    private void receiveFile(Socket clientSocket) {
        try {

            String filePath = dis.readUTF();
            String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
            String pathSave = dis.readUTF();

            rootDirServer = pathSave;

            byte buffer[] = new byte[4096];
            int read = 0;
            fos = new FileOutputStream(rootDirServer + "\\" + fileName);

            while (true) {
                if (dis.readUTF().equals("SENDED")) {

                    read = dis.read(buffer);
                    dos.writeUTF("ACK");
                    fos.write(buffer, 0, read);
                } else {
                    break;
                }
            }

            fos.close();

            System.out.println("Client " + ipAdress + " sent file to server successfully");
            System.out.println("----------------------------------------------");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void sendFile(Socket clientSocket) {

        try {

            String filePath = dis.readUTF();
            System.out.println("File path: " + filePath);
            fis = new FileInputStream(filePath);
            byte[] buffer = new byte[4096];
            int read = 0;
            if (dis.readUTF().equals("READY_TO_SEND")) {
                while (true) {
                    if ((read = fis.read(buffer)) > 0) {
                        dos.writeUTF("SENDED");
                        dos.flush();
                        dos.write(buffer, 0, read);

                        if (!dis.readUTF().equals("ACK")) {
                            dos.writeUTF("DONE");
                            break;
                        }
                    } else {
                        dos.writeUTF("DONE");
                        System.out.println("Client " + ipAdress + " received file from server successfully");
                        break;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
