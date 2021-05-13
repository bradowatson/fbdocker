/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.watson.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BWatson
 */
public class FileManip {

    public static void main(String[] args) {
        System.out.println(getTextFileContents("c:/temp/MWA JVM Availability\\ManagementModule.xml"));
    }

    public static void copyFiles(File src, File dest) throws IOException {
    	//Check to ensure that the source is valid...
    	if (!src.exists()) {
            throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath()+".");
    	} else if (!src.canRead()) { //check to ensure we have rights to the source...
            throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath()+".");
    	}
    	//is this a directory copy?
    	if (src.isDirectory()) 	{
            if (!dest.exists()) { //does the destination already exist?
                    //if not we need to make it exist if possible (note this is mkdirs not mkdir)
                if (!dest.mkdirs()) {
                    throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath() + ".");
                }
            }
            //get a listing of files...
            String list[] = src.list();
            //copy all the files in the list.
            for (int i = 0; i < list.length; i++)
            {
                File dest1 = new File(dest, list[i]);
                File src1 = new File(src, list[i]);
                copyFiles(src1 , dest1);
            }
    	} else {
            //This was not a directory, so lets just copy the file
            FileInputStream fin = null;
            FileOutputStream fout = null;
            byte[] buffer = new byte[4096]; //Buffer 4K at a time (you can change this).
            int bytesRead;
            try {
                //open the files for input and output
                fin =  new FileInputStream(src);
                fout = new FileOutputStream (dest);
                //while bytesRead indicates a successful read, lets write...
                while ((bytesRead = fin.read(buffer)) >= 0) {
                    fout.write(buffer,0,bytesRead);
                }
            } catch (IOException e) { //Error copying file...
                IOException wrapper = new IOException("copyFiles: Unable to copy file: " +
                                        src.getAbsolutePath() + "to" + dest.getAbsolutePath()+".");
                wrapper.initCause(e);
                wrapper.setStackTrace(e.getStackTrace());
                throw wrapper;
            } finally { //Ensure that the files are closed (if they were open).
                if (fin != null) { fin.close(); }
                if (fout != null) { fout.close(); }
            }
    	}
    }

    public static String getTextFileContents(String file) {
        return(getTextFileContents(file.substring(0, file.replaceAll("\\\\", "/").lastIndexOf("/")), file.substring(file.replaceAll("\\\\", "/").lastIndexOf("/"), file.length())));
    }

    public static String getTextFileContents(File file) {
        return getTextFileContents(file.getAbsolutePath());
    }

    public static String getTextFileContents(String dir, String fileName) {
    	StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        try {
            FileInputStream fis = new FileInputStream(new File(dir, fileName));
            Charset inputCharset = Charset.forName("UTF8");
            InputStreamReader isr = new InputStreamReader(fis, inputCharset);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
		text.append(line).append("\r\n");
			}
            /*Scanner in = new Scanner(new File(dir, fileName));
            while(in.hasNextLine()) {
                text.append(in.nextLine()).append("\r\n");
            }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(FileManip.class.getName()).log(Level.SEVERE, null, ex);
        }
        return text.toString();
    }

    public static void replaceStringInFile(String file, String repl, String with) {
        replaceStringInFile(file.substring(0, file.replaceAll("\\\\", "/").lastIndexOf("/")), file.substring(file.replaceAll("\\\\", "/").lastIndexOf("/"), file.length()), repl, with);
    }

    public static void replaceStringInFile(File file, String repl, String with) {
        replaceStringInFile(file.getAbsolutePath(), repl, with);
    }

    public static void replaceStringInFile(String dir, String fileName, String repl, String with) {
    	String text = getTextFileContents(dir, fileName);
        if(text.contains(repl))
        {
        File updateFile = new File(dir, fileName);
            try {
                FileWriter writer = new FileWriter(updateFile);
                text = text.replaceAll("(?i)" + repl, with);
                writer.write(text);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
