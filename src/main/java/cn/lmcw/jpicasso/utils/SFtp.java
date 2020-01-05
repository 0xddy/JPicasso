package cn.lmcw.jpicasso.utils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiConsumer;

public class SFtp {

    private static SFtp sFtp = new SFtp();

    public static SFtp getInstance() {
        synchronized (Object.class) {
            if (sFtp == null) {
                synchronized (Object.class) {
                    sFtp = new SFtp();
                }
            }
        }
        return sFtp;
    }

    private String username;
    private String host;
    private String password;
    private int port;

    private JSch jsch = new JSch();

    private SFtp() {
    }

    public SFtp init(String host, String username, String password) {
        return init(host, username, password, 22);
    }

    public SFtp init(String host, String username, String password, int port) {
        this.username = username;
        this.host = host;
        this.password = password;
        this.port = port;
        initSession();
        return this;
    }

    private ConcurrentLinkedDeque<Session> sessions = new ConcurrentLinkedDeque<>();
    private ConcurrentHashMap<Session, ConcurrentLinkedDeque<ChannelSftp>> sessionMap = new ConcurrentHashMap<>();

    // 激活中运行中的通道
    private ConcurrentHashMap<ChannelSftp, Boolean> activChannels = new ConcurrentHashMap<>();

    private synchronized void initSession() {
        // 获取一个channel 进行操作 一般是 每个session 10个限制
        for (int i = 0; i < MAX_SESSION; i++) {
            Session newSesstion = newSession();
            sessionMap.put(newSesstion, new ConcurrentLinkedDeque<>());
            sessions.add(newSesstion);
        }
    }

    private int MAX_SESSION = 10;
    private int MAX_CHANNELS_FOR_SESSION = 10;

    public void connect(SFtpListener sFtpListener) {
        try {
            long count = 10;
            ChannelSftp channelSftp = getFreeChannel();
            while (channelSftp == null && count != 0) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("等到空闲通道...");
                channelSftp = getFreeChannel();
                count--;
            }
            if (channelSftp != null)
                activChannels.put(channelSftp, true);
            if (channelSftp != null && !channelSftp.isConnected())
                channelSftp.connect();
            sFtpListener.doWork(channelSftp);
            if (channelSftp != null)
                activChannels.remove(channelSftp);

        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    private synchronized ChannelSftp getFreeChannel() throws JSchException {

        Iterator<Session> iterator = sessions.iterator();

        ChannelSftp freeChannelSftp = null;
        while (iterator.hasNext()) {
            Session session = iterator.next();
            ConcurrentLinkedDeque<ChannelSftp> channelSftps = sessionMap.get(session);
            // 找到该session下空闲着的通道
            freeChannelSftp = _getFreeChannel(channelSftps);
            if (freeChannelSftp == null) {
                // 看看是否满员
                if (channelSftps.size() < MAX_CHANNELS_FOR_SESSION) {
                    // 可以再创建一个通道
                    if (!session.isConnected())
                        session.connect();
                    freeChannelSftp = (ChannelSftp) session.openChannel("sftp");
                    channelSftps.add(freeChannelSftp);
                    System.out.println("创建个新通道");
                    break;
                } else {
                    //当前session通道已全部占用中
                    System.out.println("当前session通道已全部占用中");
                }
            } else {
                break;
            }

        }

        return freeChannelSftp;
    }

    private synchronized ChannelSftp _getFreeChannel(ConcurrentLinkedDeque<ChannelSftp> concurrentLinkedDeque) {
        ChannelSftp freeChannelSftp = null;
        Iterator<ChannelSftp> iterator = concurrentLinkedDeque.iterator();
        while (iterator.hasNext()) {
            ChannelSftp channelSftp = iterator.next();
            if (!activChannels.containsKey(channelSftp)) {
                freeChannelSftp = channelSftp;
                System.out.println("复用一个空闲通道 " + freeChannelSftp);
                break;
            }
        }
        return freeChannelSftp;
    }

    private Session newSession() {
        Session sesstion = null;
        try {
            sesstion = jsch.getSession(username, host, port);
            sesstion.setPassword(password);
            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "no");
            sesstion.setConfig(properties);

        } catch (JSchException e) {
            e.printStackTrace();
        }
        return sesstion;
    }

    public void destroy() {
        sessionMap.forEach(new BiConsumer<Session, ConcurrentLinkedDeque<ChannelSftp>>() {
            @Override
            public void accept(Session session, ConcurrentLinkedDeque<ChannelSftp> channelSftps) {
                if (channelSftps != null && channelSftps.size() > 0) {
                    //释放全部通道
                    Iterator<ChannelSftp> channelSftpIterator = channelSftps.iterator();
                    ChannelSftp tempChannel = null;
                    while (channelSftpIterator.hasNext()) {
                        tempChannel = channelSftpIterator.next();
                        if (tempChannel != null && !tempChannel.isClosed()) {
                            tempChannel.disconnect();
                            tempChannel = null;
                        }
                    }
                }
                if (session != null && session.isConnected()) {
                    session.disconnect();
                    session = null;
                }

            }
        });
        sessionMap.clear();
        sessions.clear();
        activChannels.clear();
        sFtp = null;
    }

    public static interface SFtpListener {
        void doWork(ChannelSftp sftpChannel);
    }


}
