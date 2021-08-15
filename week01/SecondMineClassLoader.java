package com.mine;

import java.io.*;
import java.util.Properties;

public class SecondMineClassLoader extends ClassLoader {
    public SecondMineClassLoader(String basePath,String fileSuffix){
        if(basePath==null||"".equals(basePath)||fileSuffix==null||"".equals(fileSuffix)){
            throw new RuntimeException("入参为空!");
        }
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

    public static Properties getProperties(){
        Properties properties = new Properties();
        final String OS_NAME = System.getProperty("os.name");
        String basePath = "";
        if(OS_NAME.startsWith("Windows")){
            basePath = "E:\\workspace\\study\\geek_classloader\\resources\\";
        }else if(OS_NAME.startsWith("Linux")){
            basePath = "E:/workspace/study/geek_classloader/resources/";
        }else if(OS_NAME.startsWith("Mac")){
            basePath = "E:/workspace/study/geek_classloader/resources/";
        }
        properties.setProperty("basePath",basePath);
        properties.setProperty("fileSuffix","xlass");
        return properties;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Properties properties = getProperties();
        SecondMineClassLoader myClassLoader = new SecondMineClassLoader(properties.getProperty("basePath"),properties.getProperty("fileSuffix"));
        myClassLoader.findClass("Hello");
    }
}
