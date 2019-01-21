package com.gome.stage.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

/**
 * 发布使用到的公共方法和工具
 */
@SuppressWarnings("rawtypes")
public class PublishUtil
{
    public static final int SUCCESS = 0;
    public static final int SAMEFILENAME = 1;
    public static final int FILENOTEXISTS = 2;
    public static final int WRITEFILEERROR = 3;

    //系统属性
    public static String OS_name = "";        //Operating system name
    public static String sep_f = "";          //Operating system file separator
    public static String sep_p = "";          //Operating system path separator
    public static String java_ext_dirs = "";  //JDK install dir
    public static String java_home = "";      //Java Rumtime install dir
    public static String hostAddress = "";    //Host IP address
    public static String databaseIP = "";     //Database server IP address
    public static String hostIP = "";         //Host IP address

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
    private static final Logger logger = LoggerFactory.getLogger(PublishUtil.class);

    static
    {
        Properties prop = null;
        try
        {
            prop = new Properties(System.getProperties());
            OS_name = prop.getProperty("os.name", "Windows 2000");
            java_ext_dirs = prop.getProperty("java.ext.dirs");
            java_home = prop.getProperty("java.home");

            sep_f = prop.getProperty("file.separator");
            sep_p = prop.getProperty("path.separator");
        }
        catch (Exception e)
        {
            logger.error("获取系统属性异常:" + e.getMessage(), e);
        }
        finally
        {
            if (prop != null)
            {
                prop.clear();
                prop = null;
            }
        }

    }

    /**
     * 从properties中得到字符型参数
     *
     * @param p
     * @param key
     * @return
     */
    public static String consume(Properties p, String key)
    {
        String s = null;
        if ((p != null) && (key != null))
        {
            s = p.getProperty(key);
            if (s != null)
            {
                p.remove(key);
            }
        }
        return (s == null || "".equals(s.trim())) ? s : s.trim();
    }

    /**
     * 从properties中得到整型参数
     *
     * @param p
     * @param key
     * @return
     */
    public static int consumeInt(Properties p, String key)
    {
        int n = -1;
        String s = consume(p, key);
        if (s != null)
        {
            try
            {
                n = Integer.parseInt(s);
            }
            catch (Exception e)
            {
                logger.error("从properties中得到整型参数异常:" + e.getMessage(), e);
            }
        }
        return n;
    }

    /**
     * 从properties中得到整型参数
     *
     * @param p
     * @param key
     * @param vdefault
     * @return
     */
    public static int consumeInt(Properties p, String key, int vdefault)
    {
        int n = vdefault;
        String s = consume(p, key);
        if (s != null)
        {
            try
            {
                n = Integer.parseInt(s);
            }
            catch (Exception e)
            {
                logger.error("从properties中得到整型参数异常:" + e.getMessage(), e);
            }
        }
        return n;
    }

    /**
     * 从properties中得到长整型参数
     *
     * @param p
     * @param key
     * @return
     */
    public static long consumeLong(Properties p, String key)
    {
        long n = -1;
        String s = consume(p, key);
        if (s != null)
        {
            try
            {
                n = Long.parseLong(s);
            }
            catch (Exception e)
            {
                logger.error("从properties中得到长整型参数异常:" + e.getMessage(), e);
            }
        }
        return n;
    }

    /**
     * 从properties中得到双精度型参数
     *
     * @param p
     * @param key
     * @return
     */
    public static double consumeDouble(Properties p, String key)
    {
        double n = -1.0;
        String s = consume(p, key);
        if (s != null)
        {
            try
            {
                n = Double.parseDouble(s);
            }
            catch (Exception e)
            {
                logger.error("从properties中得到双精度型参数异常:" + e.getMessage(), e);
            }
        }
        return n;
    }

    /**
     * 字符串替换
     *
     * @param sourceStr
     * @param oldStr
     * @param newStr
     * @return
     */
    public static String replace(String sourceStr, String oldStr, String newStr)
    {
        if (sourceStr == null || oldStr == null || "".equals(oldStr)) //若源串为空或老串为空则返回源串
        {
            return sourceStr;
        }

        if (newStr == null)
        {
            newStr = "";       //若新串为空则将它设为空字符串
        }

        int i = sourceStr.indexOf(oldStr);   //取得源字符串的起始位置
        if (i == -1)                           //没找到替换串,返回源串
        {
            return sourceStr;
        }
        else                                 //找到了,进行替换
        {
            return sourceStr.substring(0, i) + newStr + replace(sourceStr.substring(i + oldStr.length()), oldStr, newStr);
        }
    }

    /**
     * 文件拷贝
     *
     * @param source
     * @param dest
     * @return
     */
    public static int CopyFile(String source, String dest)
    {
        File infile = null;
        FileInputStream in = null;
        FileOutputStream out = null;
        try
        {
            if (source.equals(dest))
            {
                return SAMEFILENAME;  //判断源文件与目标文件是否相同
            }
            infile = new File(source);
            if (!infile.exists() || !infile.isFile())
            {
                return FILENOTEXISTS;
            }
            in = new FileInputStream(infile);
            out = new FileOutputStream(new File(dest));
            byte[] b = new byte[in.available()];
            in.read(b);
            out.write(b);
            return SUCCESS;
        }
        catch (Exception e)
        {
            logger.error("文件拷贝异常:" + e.getMessage(), e);
            return WRITEFILEERROR;
        }
        finally
        {
            infile = null;
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Exception e1)
                {
                    logger.error(e1.getMessage(), e1);
                }
                in = null;
            }
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (Exception e2)
                {
                    logger.error("文件拷贝异常:" + e2.getMessage(), e2);
                }
                out = null;
            }
        }
    }

    /**
     * 创建目录
     *
     * @param dir
     */
    public static void md(String dir)
    {
        File m_fDir = new File(dir);

        if (m_fDir.exists())
        {  //若目录存在则不作任何操作
        }
        else
        {
            m_fDir.mkdirs();    //否则建立目录
        }
    }

    /**
     * 删除目录
     *
     * @param dir
     */
    public static void rm(String dir)
    {
        try
        {
            Runtime runtime = Runtime.getRuntime();
            if ("Windows 2000".equalsIgnoreCase(OS_name))
            {
                runtime.exec("rmdir " + dir + " /s/q");
            }
            else if ("Linux".equalsIgnoreCase(OS_name))
            {
                runtime.exec("rm -rf " + dir);
            }
        }
        catch (Exception ex)
        {
            logger.error("删除目录异常:" + ex.getMessage(), ex);
        }
    }

    /**
     * 分割字符串，将结果放入Vecotr中
     *
     * @param source 源字符串
     * @param sep    分隔符
     * @return
     */
    @SuppressWarnings({"unchecked" })
    public static Vector divisionString(String source, String sep)
    {

        Vector v = new Vector();

        if (source == null)
        {
            return v;
        }

        if ("".equals(source) || sep == null || "".equals(sep))
        {
            v.add(source);
            return v;
        }
        int loc = 0;
        while (true)
        {
            loc = source.indexOf(sep);
            if (loc == -1)
            {
                v.add(source.trim());
                break;
            }
            else
            {
                v.add(source.substring(0, loc).trim());
                source = source.substring(loc + sep.length());
            }
        }
        return v;
    }

    public static String getOSEncodingStr(String str)
    {
        String outstr = "";
        try
        {
            if (PublishUtil.OS_name.indexOf("Windows") > -1)
            {
                outstr = new String(str.getBytes("ISO_8859_1"), "gb2312");
            }
            else
            {
                return str;
            }
            return outstr;
        }
        catch (Exception e)
        {
            logger.error("getOSEncodingStr异常:" + e.getMessage(), e);
            return str;
        }
    }

    /**
     * 转换字符编码
     *
     * @param strSource
     * @return
     */
    public static String TransISO8859_GB2312(String strSource)
    {
        try
        {
            return new String(strSource.getBytes("ISO8859-1"), "GB2312");
        }
        catch (Exception e)
        {
            logger.error("转换字符编码异常:" + e.getMessage(), e);
            return "";
        }
    }

    public static void writeFile(String pathname, String content) throws IOException
    {
        File path = new File(pathname);
        File parent = path.getParentFile();
        if (!parent.exists())
        {
            parent.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(path);
        try
        {
            byte[] buff = content.getBytes();
            fos.write(buff, 0, buff.length);
        }
        finally
        {
            fos.close();
        }
    }

    public static String readFile(String stopfile)
    {
        int readnum = 1;
        byte[] buff = new byte[1024];
        FileInputStream fis = null;
        StringBuilder tmp = new StringBuilder();
        try
        {
            fis = new FileInputStream(stopfile);
            while (readnum > 0)
            {

                readnum = fis.read(buff);
                if (readnum > 0)
                {
                    tmp.append(new String(buff, 0, readnum));
                }
            }
        }
        catch(Exception e)
        {
            logger.error("readFile异常:" + e.getMessage(), e);
        }
        finally
        {
            if(fis != null )
                try{
                    fis.close();
                }catch(Exception e){
                    logger.error("readFile异常:" + e.getMessage(), e);
                }
        }

        return tmp.toString();
    }

    public static String nvl(String value)
    {
        return value == null ? "" : value;
    }

    public static String nvlTrim(String value)
    {
        return value == null ? "" : value.trim();
    }

    public static boolean isEmpty(String value)
    {
        return "".equals(nvlTrim(value));
    }

    /**
     * 销量进度条进度值计算
     * @param num 该商品销量
     * @param maxNum 对比队列中最大销量
     * @return 0-1000的进度值
     */
    public static int getProgressNum(int num, int maxNum) {
        double total = (maxNum * 1.05) + 1;
        double progressNum = (new Double(num) / total) * 1000;
        return (int) progressNum;
    }

    /**
     * 排行榜获取商品推荐指数
     * @param rankNum 商品排名
     * @return 推荐指数
     */
    public static int getRecommendNum(int rankNum) {
        //100内随机数
        Random r = new Random();
        int randomNum = (int) (r.nextInt()* 100);
        //概率指数
        int num = 95 - rankNum;
        return num > randomNum ? 5 : 4;
    }


    public static String dateToYYYYMMDD(Date date) {
        String datestr = formatter.format(date);
        return datestr;
    }
}

