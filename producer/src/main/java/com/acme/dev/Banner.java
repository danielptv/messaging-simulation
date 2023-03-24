package com.acme.dev;

import com.github.lalyos.jfiglet.FigletFont;
import org.apache.catalina.util.ServerInfo;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

public final class Banner {
    public static final InetAddress LOCALHOST = getLocalhost();
    private static final String JAVA = Runtime.version().toString() + " - " + System.getProperty("java.vendor");
    private static final String OS_VERSION = System.getProperty("os.name");
    private static final long MEGABYTE = 1024L * 1024L;
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final String USERNAME = System.getProperty("user.name");

    @SuppressWarnings("ImplicitCallToSuper")
    private Banner() {
    }
    public static String getBanner() {
        return """
                %s
                Version              1.0.0
                Spring Boot          %s
                Spring               %s
                Tomcat               %s
                Java                 %s
                OS                   %s
                Hostname             %s
                IP                   %s
                Heap: Size           %d MiB
                Heap: Free           %d MiB
                Username             %s
                JVM Locale           %s
                """
                .formatted(
                        getFiglet(),
                        SpringBootVersion.getVersion(),
                        SpringVersion.getVersion(),
                        ServerInfo.getServerInfo(),
                        JAVA,
                        OS_VERSION,
                        LOCALHOST.getHostName(),
                        LOCALHOST.getHostAddress(),
                        RUNTIME.totalMemory() / MEGABYTE,
                        RUNTIME.freeMemory() / MEGABYTE,
                        USERNAME,
                        Locale.getDefault()
                );
    }

    private static String getFiglet() {
        try {
            return FigletFont.convertOneLine("producer");
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static InetAddress getLocalhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (final UnknownHostException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
