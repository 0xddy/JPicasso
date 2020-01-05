package cn.lmcw.jpicasso.utils;

import java.io.IOException;
import java.io.InputStreamReader;

@SuppressWarnings("Duplicates")
public class ShellUtils {

    public static Boolean runShell(String command) {
        InputStreamReader stdISR = null;
        InputStreamReader errISR = null;
        Process process = null;
        boolean success = true;
        try {
            process = Runtime.getRuntime().exec(command);

            CommandStreamGobbler errorGobbler = new CommandStreamGobbler(process.getErrorStream(), command, "ERR");
            CommandStreamGobbler outputGobbler = new CommandStreamGobbler(process.getInputStream(), command, "STD");

            errorGobbler.start();
            // 必须先等待错误输出ready再建立标准输出
            while (!errorGobbler.isReady()) {
                Thread.sleep(10);
            }
            outputGobbler.start();
            while (!outputGobbler.isReady()) {
                Thread.sleep(10);
            }

            int exitValue = process.waitFor();
            success = errorGobbler.isSuccess() && outputGobbler.isSuccess();
        } catch (IOException | InterruptedException e) {
            success = false;

        } finally {
            try {
                if (stdISR != null) {
                    stdISR.close();
                }
                if (errISR != null) {
                    errISR.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {

            }
        }
        return success;
    }


    public static Boolean runShell(String command, String file) {
        InputStreamReader stdISR = null;
        InputStreamReader errISR = null;
        Process process = null;
        boolean success = true;
        try {
            process = Runtime.getRuntime().exec(command);

            CommandStreamGobbler errorGobbler = new CommandStreamGobbler(process.getErrorStream(), command, "ERR", file + ".err");
            CommandStreamGobbler outputGobbler = new CommandStreamGobbler(process.getInputStream(), command, "STD", file);

            errorGobbler.start();
            // 必须先等待错误输出ready再建立标准输出
            while (!errorGobbler.isReady()) {
                Thread.sleep(10);
            }
            outputGobbler.start();
            while (!outputGobbler.isReady()) {
                Thread.sleep(10);
            }

            int exitValue = process.waitFor();

            success = errorGobbler.isSuccess() && outputGobbler.isSuccess();
        } catch (IOException | InterruptedException e) {
            success = false;

        } finally {
            try {
                if (stdISR != null) {
                    stdISR.close();
                }
                if (errISR != null) {
                    errISR.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
}

