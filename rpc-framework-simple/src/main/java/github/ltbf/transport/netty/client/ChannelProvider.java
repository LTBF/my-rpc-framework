package github.ltbf.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author shkstart
 * @create 2020-10-04 16:28
 * 获取一个连接Channel
 * 无法处理并发请求*****
 */
@Slf4j
public class ChannelProvider {

    // Netty 客户端配置类
    private static final Bootstrap bootstrap = NettyClient.initializeBootstrap();
    // 连接失败最多重试次数
    private static final int MAX_RETRY_COUNT = 5;
    //
    private static Channel channel = null;


    public static Channel get(InetSocketAddress inetSocketAddress){
        int retry = 1;
        CountDownLatch countDownLatch = new CountDownLatch(1);

        connect(bootstrap, inetSocketAddress, retry, countDownLatch);
        try {
            countDownLatch.await();    // 阻塞，直到连接连接成功或重连失败
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return channel;

    }


    /**
     * 带有重试机制的连接请求
     */
    public static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, int retry, CountDownLatch countDownLatch) {
        int x = 1;

        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接成功，第" + retry + "次连接");
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }

            if (retry > MAX_RETRY_COUNT) {
                log.error("客户端连接失败:连接次数已用完");
                countDownLatch.countDown();
                return;
            }
            // 本次重连间隔
            int delay = 1 << retry;
            // 重新连接
            log.error("客户端连接失败，尝试第{}次重新连接", retry);
            bootstrap.config().group().schedule(() -> {
                connect(bootstrap, inetSocketAddress, retry + 1, countDownLatch);
            }, delay, TimeUnit.SECONDS);
        });

    }


}
