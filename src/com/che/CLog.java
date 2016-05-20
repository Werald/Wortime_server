package com.che;

/**
 * Created by Alexx on 13.05.2016.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class CLog {
    private FileWriter fileWriter;


    public CLog() {
        try {
            fileWriter = new FileWriter("work.log", false);
            fileWriter.write("-------------------------\n");
            fileWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
            log_console("Error at IO");
        }
    }

    public static void log_console(Object o) {
        System.out.println(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)
                + "  " + o);
    }

    public void log(Object object) {
        try {
            fileWriter.write(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME) + ":" + object + "\n");
            log_console(object);
            fileWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error at IO");
        }
    }

}
