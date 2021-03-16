package com.rick.cryptcloud.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandUtils {

    public static List<String> executeLinuxCmd(String cmd) {
        System.out.println("got cmd job : " + cmd);
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(new String[] {"/bin/sh", "-c", cmd});
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            List<String> list = new ArrayList<>();
            String result = null;
            while ((result = bs.readLine()) != null) {
                System.out.println("job result [" + result + "]");
                list.add(result);
            }
            in.close();
            process.destroy();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
