/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package svgloader;

import java.util.*;
// Joe Nartca (C)
public class Tool {
       
    public static ArrayList<String> split(String S, String k) {
        int b, i = 0;
        int ls = S.length();
        int lk = k.length();
        ArrayList<String> al = new ArrayList<>();
        OUT:for (b = 0; i < ls; ++i) {
            if (S.charAt(i) == k.charAt(0)) {
                int l, j = i + 1;
                for (l = 1; l < lk && j < ls; ++l, ++j)
                    if (S.charAt(j) != k.charAt(l)) continue OUT;
                if (l == lk) {
                    String X = S.substring(b,j).trim();
                    if (X.length() > 0) al.add(X);
                    i = b = j;
                }
            }
        }
        if (b < ls) {
            String X = S.substring(b,ls).trim();
            al.add(X);
        }
        return al;
    }
    
    public static int toInt(String I) {
        boolean neg;
        int li, i = 0;
        if (I.charAt(0) == '-'){
            neg = true;
            li = 1;
        } else {
            neg = false;
            li = 0;
        }
        for (int b = I.length()-1, f = 1; b >= li; --b) {
            i += (I.charAt(b)&0x0F)*f;
            f *= 10;
        }
        return (neg? -i: i);
    }
    public static double toDouble(String D) {
        boolean neg;
        double d = 0.0;
        int li;
        if (D.charAt(0) == '-'){
            neg = true;
            li = 1;
        } else {
            neg = false;
            li = 0;
        }
        if (D != null) {
            int len = D.length();
            for (int b = 0; b < len; ++b)
            if (D.charAt(b) == '.') {
                double f = 10d;
                for (int e = b+1; e < len; ++e) {
                    d += (double)(D.charAt(e)&0x0F)/f;
                    f *= 10;
                }
                f = 1d;
                for (--b; b >= li; --b) {
                    d += (double)(D.charAt(b)&0x0F)*f;
                    f *= 10;
                }
                return (neg? -d:d);
            }
            double f = 1d;
            for (int b = len-1; b >= li; --b) {
                d += (D.charAt(b)&0x0F)*f;
                f *= 10;
            }
        }
        return (neg? -d:d);
    }
   
}