package com.example.vinaykumarg.dtalarm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Vinay Kumar G on 30-05-2016.
 */
public class jsonclass {
    public String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);
//            Log.e("ssss", buffer.toString());
            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }


}
