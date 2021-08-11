package com.mine;

import java.io.*;

public class SecondMineClassLoader extends ClassLoader {
    public SecondMineClassLoader(String basePath,String fileSuffix){
        BASE_PATH = basePath;
        FILE_SUFFIX = "."+fileSuffix;
    }

    private final String BASE_PATH;
    private final String FILE_SUFFIX;

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException  {
        String filePath = BASE_PATH + name + FILE_SUFFIX;
        byte[] bytes = getDecryptBytes(filePath);
        return defineClass(name,bytes,0,bytes.length);
    }

    public static byte[] getDecryptBytes(String filePath){
        byte[] buffer = null;
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();){
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                for(int i=0;i<b.length;i++){
                    b[i] = (byte)(255-b[i]);
                }
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;

    }


    public static void main(String[] args) throws ClassNotFoundException {
        SecondMineClassLoader myClassLoader = new SecondMineClassLoader("E:\\workspace\\study\\geek_classloader\\resources\\","xlass");
        myClassLoader.findClass("Hello");
    }
}
