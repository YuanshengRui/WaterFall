package tech.waterfall.register.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalHostUtils {

    public static String getHostName() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        String hostName = addr.getHostName();
        if (hostName != null && hostName.indexOf(".") > 0) {
            hostName = hostName.substring(0, hostName.indexOf("."));
        }

        return hostName;
    }
}
