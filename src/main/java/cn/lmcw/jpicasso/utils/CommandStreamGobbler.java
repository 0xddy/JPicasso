package cn.lmcw.jpicasso.utils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class CommandStreamGobbler extends Thread {

    private InputStream is;

    private String command;

    private String prefix = "";

    private String file;

    private boolean readFinish = false;

    private boolean ready = false;

    private boolean success = true;

    private List<String> infoList = new LinkedList<>();

    CommandStreamGobbler(InputStream is, String command, String prefix) {
        this.is = is;
        this.command = command;
        this.prefix = prefix;
    }

    CommandStreamGobbler(InputStream is, String command, String prefix, String file) {
        this.is = is;
        this.command = command;
        this.prefix = prefix;
        this.file = file;
    }

    @Override
    public void run() {
        InputStreamReader isr = null;
        BufferedWriter bufferedWriter = null;
        try {
            isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            ready = true;

            if (null != file){
                bufferedWriter = new BufferedWriter(new FileWriter(file ,true));
            }

            while ((line = br.readLine()) != null) {
                infoList.add(line);

                if (null != file){
                    assert bufferedWriter != null;
                    bufferedWriter.write(line+ "\r\n");
                    bufferedWriter.flush();
                }
            }
        } catch (IOException ioe) {
            success = false;
            System.out.println("正式执行命令：" + command + "有IO异常");

        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (bufferedWriter != null){
                    bufferedWriter.close();
                }
            } catch (IOException ioe) {
                success = false;
                System.out.println("正式执行命令：" + command + "有IO异常");
            }
            readFinish = true;
        }
    }

    public InputStream getIs() {
        return is;
    }

    public String getCommand() {
        return command;
    }

    public boolean isReadFinish() {
        return readFinish;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getInfoList() {
        return infoList;
    }
}
