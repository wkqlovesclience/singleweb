package com.gome.stage.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.regex.Pattern;


/**
 * 与String有关的一些方法。如：htmlencode、replace等<br>
 * 该类不允许继承、不允许实例化
 */
public final class StringUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);
	
    /**
     * 判断当前系统是不是西欧编码方式
     */
    public static boolean systemEncodingIsISO8859 = "ISO-8859-1".equals(System.getProperty("file.encoding"));
    private final static char[] digits = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'
    };

    /**
     * 该类不允许实例化<br>
     * --------------------------------------------------------------------------<br>
     * 创建日期：2003-1-9<br>
     * <br>
     * 修改者：<br>
     * 修改日期：<br>
     * 修改原因：<br>
     * --------------------------------------------------------------------------<br>
     */
    private StringUtils() {
    }


    /**
     * 自动裁剪汉字。主要是用于系统是ISO-8859-1编码方式，需要剪一段字符串前面部分。
     * 由于一个汉字占两个位置。所以，剪的时候有时会把汉字分开。目前这种方式不会，<br>
     * --------------------------------------------------------------------------<br>
     * 创建日期：2003-1-9<br>
     * <br>
     * 修改者：<br>
     * 修改日期：<br>
     * 修改原因：<br>
     * --------------------------------------------------------------------------<br>
     *
     * @param hz 要剪切的字符串
     * @param n  剪多长
     * @return 剪切后的结果
     */
    public static String autoCutHZ(String hz,
                                   int n) {
        if (hz == null || hz.length() == 0)
            return "";
        else
            return hz.length() <= n ? hz : cutHZ(hz,
                    n) + "...";
    }

    /**
     * 自动裁剪汉字。主要是用于系统是ISO-8859-1编码方式，需要剪一段字符串前面部分。
     * 由于一个汉字占两个位置。所以，剪的时候有时会把汉字分开。目前这种方式不会，<br>
     * --------------------------------------------------------------------------<br>
     * 创建日期：2003-1-9<br>
     * <br>
     * 修改者：<br>
     * 修改日期：<br>
     * 修改原因：<br>
     * --------------------------------------------------------------------------<br>
     *
     * @param hz 要剪切的字符串
     * @param n  剪多长
     * @return 剪切后的结果
     */
    public static String cutHZ(String hz,
                               int n) {
        if (isEmpty(hz)) {
            return "";
        }
        boolean flag = true;
        String hzCut = "";
        if (hz.length() < n)
            n = hz.length();
        if (n == 0) {
            return "";
        }
        if (n == 1) {
            if (hz.charAt(0) >= '\177')
                return " ";
            else
                return hz.charAt(0) + "";
        }
        for (int i = 0; i < n; i++)
            if (i == n - 1) {
                if (hz.charAt(i) >= '\177') {
                    if (!flag)
                        hzCut = hzCut + hz.charAt(i - 1) + hz.charAt(i);
                } else if (!flag)
                    hzCut = hzCut + "_" + hz.charAt(i);
                else
                    hzCut = hzCut + hz.charAt(i);
            } else if (flag) {
                if (hz.charAt(i) >= '\177')
                    flag = false;
                else
                    hzCut = hzCut + hz.charAt(i);
            } else {
                if (hz.charAt(i) < '\177')
                    hzCut = hzCut + '_' + hz.charAt(i);
                else
                    hzCut = hzCut + hz.charAt(i - 1) + hz.charAt(i);
                flag = true;
            }

        return hzCut;
    }

    /**
     * 字符串替换<br>
     * --------------------------------------------------------------------------<br>
     * 创建日期：2003-1-9<br>
     * <br>
     * 修改者：<br>
     * 修改日期：<br>
     * 修改原因：<br>
     * --------------------------------------------------------------------------<br>
     *
     * @param text 原文字符串
     * @param s1   要替换的字符串
     * @param s2   替换成新的字符串
     * @return 替换后的结果
     */
    public static String replaceStrings(String text,
                                        String s1,
                                        String s2) {
        int pos;
        if (text == null || s2 == null) return text;
        int limit = 0;
        while ((pos = text.indexOf(s1)) >= limit) {
            text = text.substring(0,
                    pos) + s2 + text.substring(pos + s1.length(),
                    text.length());
            limit = pos + s2.length();
        }
        return text;
    }

    /**
     * 字符串替换<br>
     * --------------------------------------------------------------------------<br>
     * 创建日期：2003-1-9<br>
     * <br>
     * 修改者：<br>
     * 修改日期：<br>
     * 修改原因：<br>
     * --------------------------------------------------------------------------<br>
     *
     * @param str    原文字符串
     * @param oldstr 要替换的字符串
     * @param newstr 替换成新的字符串
     * @return 替换后的结果
     */
    public static String replace(String str,
                                 String oldstr,
                                 String newstr)
            {
        if (str == null)
            return null;
        if (oldstr == null || "".equals(oldstr) || newstr == null)
            return str;
        int olen = oldstr.length();
        int iat = 0;
        StringBuilder bstr = new StringBuilder();
        do {
            iat = str.indexOf(oldstr);
            if (iat < 0) {
                bstr.append(str);
                break;
            }
            bstr.append(str.substring(0,
                    iat));
            bstr.append(newstr);
            str = str.substring(iat + olen,
                    str.length());
        } while (true);
        String s = bstr.toString();
        bstr = null;
        return s;
    }

    /**
     * 从指定的位置开始进行字符串替换<br>
     * --------------------------------------------------------------------------<br>
     * 创建日期：2003-1-9<br>
     * <br>
     * 修改者：<br>
     * 修改日期：<br>
     * 修改原因：<br>
     * --------------------------------------------------------------------------<br>
     *
     * @param str    原文字符串
     * @param oldstr 要替换的字符串
     * @param newstr 替换成新的字符串
     * @param start  开始位置
     * @return 替换后的结果
     */
    public static String replace(String str,
                                 String oldstr,
                                 String newstr,
                                 int start)
          {
        if (str == null)
            return null;
        if (oldstr == null || newstr == null)
            return str;
        if (start < 0 || start > str.length()) {
        	return null;
        } else {
        	StringBuilder bstr = new StringBuilder(str.substring(0,
                    start));
            bstr.append(replace(str.substring(start,
                    str.length()),
                    oldstr,
                    newstr));
            return bstr.toString();
        }
    }

    public static String replace(String str,
                                 String oldstr,
                                 String newstr,
                                 int start,
                                 int length)
           {
        if (str == null)
            return null;
        if (oldstr == null || newstr == null)
            return str;
        if (start < 0 || length < 1 || start + length > str.length()) {
           return null;
        } else {
        	StringBuilder bstr = new StringBuilder(str.substring(0,
                    start));
            bstr.append(replace(str.substring(start,
                    start + length),
                    oldstr,
                    newstr));
            bstr.append(str.substring(start + length,
                    str.length()));
            return bstr.toString();
        }
    }

    public static String htmlEncode(String str) {
        if (isEmpty(str)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(str.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = str.length();
        char c;

        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss
                // word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;");
                } else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                }
            } else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                if (c == '"')
                    sb.append("&quot;");
                else if (c == '&')
                    sb.append("&amp;");
                else if (c == '<')
                    sb.append("&lt;");
                else if (c == '>')
                    sb.append("&gt;");
                else if (c == '\n')
                    // Handle Newline
                    sb.append("<br>");
                else if(c=='\r')
                	sb.append("<br>");
                else {
                    sb.append(c);
//                    int ci = 0xffff & c;
//                    if (ci < 160)
//                    // nothing special only 7 Bit
//                        sb.append(c);
//                    else {
//                        // Not 7 Bit use the unicode system
//                        sb.append("&#");
//                        sb.append(new Integer(ci).toString());
//                        sb.append(';');
//                    }
                }
            }
        }
        return sb.toString();
    }

    public static String htmlSmallEncode(String str) {
        if (str == null || str.length() == 0) return "";
        StringBuilder buffer = new StringBuilder((int) (str.length() * 1.5));
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case'&':
                    buffer.append("&amp;");
                    break;
                case'<':
                    buffer.append("&lt;");
                    break;
                case'>':
                    buffer.append("&gt;");
                    break;
                case'"':
                    buffer.append("&#34;");
                    break;
                case'\'':
                    buffer.append("&#39;");
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }
        return buffer.toString();
    }

    public static String htmlSmallDecode(String str)
           {
        str = replace(str, "&#60;", "<");
        str = replace(str, "&#62;", ">");
        return str;
    }

    public static String filterNull(String str) {
        return filterNull(str, 1);
    }

    public static String filterNull(String str, int type) {
        String result;
        if (str != null)
            result = str;
        else
            switch (type) {
                case 2: // '\002'
                    result = "&nbsp;";
                    break;

                case 3: // '\003'
                    result = "-";
                    break;

                case 4: // '\004'
                    result = " ";
                    break;

                default:
                    result = "";
                    break;
            }
        return result;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isTrimEmpty(String s) {
        return s == null || s.length() == 0 || s.trim().length() == 0;
    }

    /**
     * @param s
     * @return
     * @throws UnsupportedEncodingException
     *
     */
    public static String toISO8859FormLocal(String s) throws UnsupportedEncodingException {
        if (systemEncodingIsISO8859) return s;
        if (s == null)
            return s;
        else
            return new String(s.getBytes(),
                    "ISO-8859-1");
    }

    /**
     * @param s
     * @return
     * @throws UnsupportedEncodingException
     *
     */
    public static String toLocaLFormISO8859(String s) throws UnsupportedEncodingException {
        if (systemEncodingIsISO8859) return s;
        if (s == null)
            return s;
        else
            return new String(s.getBytes("ISO-8859-1"));
    }

    public static String multiSeriesSpaceToSingle(String str) {
        if (str == null || str.length() == 0) return str;
        int len = str.length();
        StringBuilder buffer = new StringBuilder(len);
        boolean lastIsWhiteSpace = false;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c == ' ') {
                if (!lastIsWhiteSpace) {
                    buffer.append(c);
                    lastIsWhiteSpace = true;
                }
            } else {
                buffer.append(c);
                lastIsWhiteSpace = false;
            }
        }
        String s = buffer.toString();
        buffer.setLength(0);
        buffer = null;
        return s;
    }

    /**
     * 转字符串转换成小写
     *
     * @param s
     * @return s
     */
    public static String toLower(String s) {
        if (isEmpty(s)) {
            return s;
        } else {
/*
            String enc = System.getProperty("file.encoding");
            if ("GBK".equals(enc) || "GB2312".equals(enc)) {
                return s.toLowerCase();
            } else {
                try {
                    String tmp = new String(s.getBytes(enc), "GBK");
                    tmp = tmp.toLowerCase();
                    return new String(tmp.getBytes("GBK"), enc);
                } catch (UnsupportedEncodingException e) {
                    return s;
                }
            }
*/
        	StringBuilder buffer = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    buffer.append((char) (c + 32));
                } else {
                    buffer.append(c);
                }
            }
            return buffer.toString();
        }
    }

    /**
     * 转字符串转换成大写
     *
     * @param s
     * @return s
     */
    public static String toUpper(String s) {
        if (isEmpty(s)) {
            return s;
        } else {
//            String enc = System.getProperty("file.encoding");
//            if ("GBK".equals(enc) || "GB2312".equals(enc)) {
//                return s.toUpperCase();
//            } else {
//                try {
//                    String tmp = new String(s.getBytes(), "GBK");
//                    tmp = tmp.toUpperCase();
//                    return new String(tmp.getBytes("GBK"));
//                } catch (UnsupportedEncodingException e) {
//                    return s;
//                }
//            }
        	StringBuilder buffer = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= 'a' && c <= 'z') {
                    buffer.append((char) (c - 32));
                } else {
                    buffer.append(c);
                }
            }
            return buffer.toString();
        }
    }

    /**
     * 判断bytes是不是utf-8编码
     *
     * @param bytes
     * @return true or false
     */
    public static boolean isUTF8OfBytes(byte[] bytes) {
        int numChars = bytes.length;
        int i = 0;
        boolean isUtf8 = false;
        try {
            //如果想要理解这段代码，请阅读utf-8的相关标准
            while (i < numChars) {
                byte b = bytes[i++];
                if ((b & 0xE0) == 0xE0) {
                    b = bytes[i++];
                    if ((b & 0x80) == 0x80) {
                        b = bytes[i];//注意：这里为什么不再把i加一次了
                        if ((b & 0x80) == 0x80) {
                            isUtf8 = true;
                            break;
                        }
                    }
/*
                } else if ((c & 0xC0) == 0xC0) {
                    c = bytes[i++];
                    if ((c & 0x80) == 0x80) {
                        isUtf8 = true;
                        break;
                    }
*/
                } else if ((b & 0x80) == 0x80) {
                    i++;
                }
            }
        } catch (Exception e) {
        	logger.error("判断bytes是不是utf-8编码异常:" + e.getMessage(), e);
        }
        return isUtf8;
    }

    /**
     * 判断提交过来的url是不是utf-8字符
     *
     * @param url
     * @return true or false
     */
    public static boolean isUTF8(String url) {
        int numChars = url.length();
        int i = 0;
        boolean isUtf8 = false;
        try {
            while (i < numChars) {
                char c = url.charAt(i);
                if (c == '%') {
//                    int pos = 0;

                    while (((i + 2) < numChars) && (c == '%')) {
                        byte tmp;
                        tmp = getByte(url, i);
                        if ((tmp & 0xE0) == 0xE0) {
                            i += 3;
                            tmp = getByte(url, i);
                            if ((tmp & 0x80) == 0x80) {
//                                i += 3;
                                tmp = getByte(url, i + 3);
                                if ((tmp & 0x80) == 0x80) {
                                    isUtf8 = true;
                                    break;
                                }
                            }
/*
                        } else if ((tmp & 0xC0) == 0xC0) {
                            i += 3;
                            tmp = getByte(url, i);
                            if ((tmp & 0x80) == 0x80) {
                                isUtf8 = true;
                                break;
                            }
*/
                        }
                        if ((tmp & 0x80) == 0x80) {
                            i += 6;
                        } else {
                            i += 3;
                        }
                        if (i < numChars) {
                            c = url.charAt(i);
                        }
                    }

                }
                i += 3;
                if (i < numChars) {
                    c = url.charAt(i);
                }
                if (isUtf8) {
                    return true;
                }
            }
        } catch (Exception e) {
        	logger.error("判断提交过来的url是不是utf-8字符异常:" + e.getMessage(), e);
        }
        return isUtf8;
    }

    private static byte getByte(String param, int i) {
        return (byte) Integer.parseInt(param.substring(i + 1, i + 3), 16);
    }

    public static String getHexValue(byte b) {
        char digit = digits[b & 0x0F];
        char dig1 = digits[(b >>>= 4) & 0x0F];
        return "%" + dig1 + digit;
    }


    public static String formatUrl(String url) {
    	StringBuilder bstr = new StringBuilder(url.length() * 3);
        char[] chars = url.toCharArray();
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            char aChar = chars[i];
            byte b = (byte) aChar;
            if ((b & 0x80) == 0x80) {
                bstr.append(StringUtils.getHexValue(b));
            } else {
                bstr.append(aChar);
            }
        }
        return bstr.toString();
    }

    /**
     * url 编码。该编码的结果可能不是很全面
     *
     * @param url
     * @return url
     */
    public static String encodeUrl(String url) {
    	StringBuilder bstr = new StringBuilder(url.length() * 3);
        char[] chars = url.toCharArray();
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            char aChar = chars[i];
            byte b = (byte) aChar;
            if ((b & 0x80) == 0x80) {
                bstr.append(getHexValue(b));
            } else if (aChar == '?') {
                bstr.append("%3F");
            } else if (aChar == ':') {
                bstr.append("%3A");
            } else if (aChar == '%') {
                bstr.append("%25");
            } else if (aChar == '/') {
                bstr.append("%2F");
            } else if (aChar == '&') {
                bstr.append("%26");
            } else if (aChar == '=') {
                bstr.append("%3D");
            } else if (aChar == ' ') {
                bstr.append('+');
            } else {
                bstr.append(aChar);
            }
        }
        return bstr.toString();
    }

    /**
     * 格式化显示字节数
     *
     * @param size
     * @return 格式化后的显示串。
     */
    public static String formatSize(int size) {
        DecimalFormat format = new DecimalFormat("0.00");

        if (size > 1024 * 1024 * 1024) {
            return format.format((float) size / (1024 * 1024 * 1024)) + "G";
        } else if (size > 1024 * 1024) {
            return format.format((float) size / (1024 * 1024)) + "M";
        } else if (size > 1024) {
            return format.format((float) size / (1024)) + "K";
        } else if (size == 1) {
            return size + "Byte";
        } else {
            return size + "Bytes";
        }
    }

    /**
     * 转换编码为GB2312
     * @param s 字符串
     * @param encoding 原来编码
     * @return 新的字符串
     */
    public static String getGB2312String(String s,String encoding){
    	if(s==null)
    		return s;
    	String newStr=null;
		try {
			if(encoding!=null && !"".equals(encoding))
				newStr = new String(s.getBytes(encoding),"GB2312");
			else
				newStr = new String(s.getBytes(),"GB2312");
		} catch (UnsupportedEncodingException e) {
			logger.error("转换编码为GB2312异常:" + e.getMessage(), e);
		}
    	return newStr;
    }

    /**
     * 编码从GBK转换为ISO-8859-1
     * @param s 字符串
     * @return 新的字符串
     */
    public static String toISO8859FromGbk(String s){
			try {
				s=new String(s.getBytes("gbk"),"iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				logger.error("编码从GBK转换为ISO-8859-1错误",e);
			}
    	return s;
    }

	public static String changeCharset(String s ,String sourceCharset,String destCharset) throws UnsupportedEncodingException{
        if(s==null||s.trim().length()==0||destCharset==null||destCharset.trim().length()==0){
            return "";
        }
        if(sourceCharset==null||sourceCharset.trim().length()==0){
            s=new String(s.getBytes(),destCharset);
        }else{
            s=new String(s.getBytes(sourceCharset),destCharset);
        }
        return s;
    }

    //反转字符串
    public static String reverseStr(String str){
    	char[] chars=str.toCharArray();
    	for(int i=0;i<chars.length/2;i++){
    		char temp=chars[chars.length-i-1];
    		chars[chars.length-i-1]=chars[i];
    		chars[i]=temp;
    	}
    	StringBuilder sb=new StringBuilder();
    	return sb.append(chars).toString();
    }

    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param String
     *            s ,需要得到长度的字符串
     * @return int, 得到的字符串长度
     */
    public static int length(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    /**
     * 截取一段字符的长度,不区分中英文,如果数字不正好，则少取一个字符位
     *
     * @author patriotlml
     * @param String
     *            origin, 原始字符串
     * @param int
     *            len, 截取长度(一个汉字长度按2算的)
     * @return String, 返回的字符串
     */
    public static String substring(String origin, int len) {
        if (origin == null || "".equals(origin)||len<1)
            return "";
        byte[] strByte = new byte[len];
        if (len > length(origin)){
            return origin;}
        int count = 0;
        for (int i = 0; i < len; i++) {
            int value = (int) strByte[i];
            if (value < 0) {
                count++;
            }
        }
        if (count % 2 != 0) {
            len = (len == 1) ? ++len : --len;
        }
        return new String(strByte, 0, len);
    }

    public static boolean isNotNullAndEqual(Object first, Object second) {
    	return first != null && second != null &&
    		String.valueOf(first).equals(String.valueOf(second));
    }

    public static boolean isNotNullAndNotEqual(Object first, Object second) {
    	return first != null && second != null &&
    		!String.valueOf(first).equals(String.valueOf(second));
    }

    public static boolean isNullOrBlank(String value) {
    	return value == null || "".equals(value.trim());
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
    * 删除Html标签
    * @param inputString
    * @return
    */
    public static String removeHtmlTag(String inputString) {
    			if (inputString == null){return null;}

    			String htmlStr = inputString; // 含html标签的字符串
    			String textStr = "";
    			Pattern p_script;
    			java.util.regex.Matcher m_script;
    			Pattern p_style;
    			java.util.regex.Matcher m_style;
    			Pattern p_html;
    			java.util.regex.Matcher m_html;
    			Pattern p_special;
    			java.util.regex.Matcher m_special;
    			try {
    			//定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
    			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
    			//定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
    			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
    			// 定义HTML标签的正则表达式
    			String regEx_html = "<[^>]+>";
    			// 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    			String regEx_special = "\\&[a-zA-Z]{1,10};";
    			
    			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
    			m_script = p_script.matcher(htmlStr);
    			htmlStr = m_script.replaceAll(""); // 过滤script标签
    			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
    			m_style = p_style.matcher(htmlStr);
    			htmlStr = m_style.replaceAll(""); // 过滤style标签
    			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
    			m_html = p_html.matcher(htmlStr);
    			htmlStr = m_html.replaceAll(""); // 过滤html标签
    			p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
    			m_special = p_special.matcher(htmlStr);
    			htmlStr = m_special.replaceAll(""); // 过滤特殊标签
    			textStr = htmlStr;
    			} catch (Exception e) {
    				logger.error("删除Html标签异常:" + e.getMessage(), e);
    			}
    			return textStr;// 返回文本字符串
    }

    public static String substringForNews(String orignal, int count) throws UnsupportedEncodingException   {   
        // 原始字符不为null，也不是空字符串   
        if (orignal != null && !"".equals(orignal)) {   
            // 将原始字符串转换为GBK编码格式   
            orignal = new String(orignal.getBytes(), "GBK");   
            // 要截取的字节数大于0，且小于原始字符串的字节数   
            if (count > 0 && count < orignal.getBytes("GBK").length) {   
            	StringBuilder buff = new StringBuilder();   
                char c;   
                for (int i = 0; i < count; i++) {   
                    // charAt(int index)也是按照字符来分解字符串的   
                    c = orignal.charAt(i);   
                    buff.append(c);  
                    boolean flag =String.valueOf(c).getBytes("GBK").length > 1;
                    if (flag) {   
                        // 遇到中文汉字，截取字节总数减1   
                        --count;   
                    }   
                }   
                return buff.toString();   
            }   
        }   
        return orignal;
    }
    
}
