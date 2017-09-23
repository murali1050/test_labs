package services;

import java.io.RandomAccessFile;

class FlatFileCreation {
    public static void main(String args[]) throws Exception {
          RandomAccessFile f = new RandomAccessFile("C:\\Murali\\Documents\\SWC\\File Exchange\\Flat_Files\\Test_500MB.txt", "rw");
          f.setLength(1024l * 1024 * 512 * 1);
    }
}