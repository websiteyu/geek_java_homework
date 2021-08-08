package com.mine;

import java.io.*;
import java.nio.file.Files;

public class MyClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException  {
        byte[] bytes = null;
        File file = new File("E:\\workspace\\study\\geek_classloader\\resources\\Hello.xlass");
        File decryptFile = new File("E:\\workspace\\study\\geek_classloader\\resources\\"+name+".class");
        try {
            decryptFileByStream(file,decryptFile);
            bytes = Files.readAllBytes(decryptFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defineClass(name,bytes,0,bytes.length);
    }


    public static void decryptFileByStream(File source, File dest) throws
            IOException {
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest);){
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                for(int i=0;i<buffer.length;i++){
                    buffer[i] = (byte)(255-buffer[i]);
                }
                os.write(buffer, 0, length);
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        MyClassLoader myClassLoader = new MyClassLoader();
        myClassLoader.findClass("Hello");
    }

}
