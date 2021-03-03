package be.besuper.volumemixercontroller.computer;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import be.besuper.volumemixercontroller.MainActivity;

public class SComputer {

    public static final List<Computer> computers = new ArrayList<>();

    public static boolean deleteFile(final String file_name){
        final File unknow_file = new File(MainActivity.activity.getFilesDir(), file_name);

        if (unknow_file.exists()) {
            return unknow_file.delete();
        }
        return false;
    }

    public static void addComputer(final String bind) {
        final Computer computer = new Computer("Unknow", bind);

        computers.add(computer);

        writeFile(computer.getName() + ".txt", computer.getBind());
    }

    public static void fixComputerName(String name, Computer computer) {
        deleteFile("Unknow.txt");

        computer.setName(name);

        writeFile(name +" .txt", computer.getBind());
    }

    public static void readAllComputers() {
        // To be sure list is empty
        computers.clear();

        final File dir = MainActivity.activity.getFilesDir();

        for (final File file : dir.listFiles()) {
            final String line = readFile(file.getName());
            if (line == null || line.isEmpty()) {
                continue;
            }

            computers.add(new Computer(file.getName().replace(".txt", ""), line));
        }

    }

    public static String readFile(final String file) {
        String response = "";
        try {
            final FileInputStream fis = MainActivity.activity.openFileInput(file);
            response = new BufferedReader(new InputStreamReader(fis)).readLine();
            fis.close();
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return response;
    }

    public static void writeFile(final String file_name, final String content){
        try {
            final FileOutputStream fos = MainActivity.activity.openFileOutput(file_name, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
