package github.ltbf.util.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shkstart
 * @create 2020-10-07 20:49
 * 基于Curator实现的zookeeper帮助类
 *  - 连接zookeeper
 *  - 创建节点
 *  - 查询节点
 */
public class CuratorHelper {

    private static final Logger logger = LoggerFactory.getLogger(CuratorHelper.class);
    // 连接重试间隔
    private static final int SLEEP_MS_BETWEEN_RETRIES = 100;
    // 最大重试次数
    private static final int MAX_RETYR = 3;
    // zookeeper地址
    private static final String CONNECT_STRING = "127.0.0.1:2181";
    // 连接超时时间
    private static final int CONNECTION_TIMEOUT = 10 * 1000;
    // 会话超时时间
    private static final int SESSION_TIMEOUT = 60 * 1000;
    // 根节点
    public static final String ZK_REGISTER_ROOT_PATH = "/my_rpc_2";
    // 用于缓存zookeeper所提供的服务，ConcurrentHashMap线程安全
    private static final Map<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();


    /**
     * 获取一个zookeeper客户端连接
     * @return
     */
    public static CuratorFramework getZKClient(){

        // 重试策略，重试3次，并且间隔100ms
        RetryPolicy retryPolicy = new RetryNTimes(MAX_RETYR, SLEEP_MS_BETWEEN_RETRIES);

        // 返回客户端连接
        return CuratorFrameworkFactory.builder()
                // 服务器地址
                .connectString(CONNECT_STRING)
                // 连接重试策略
                .retryPolicy(retryPolicy)
                // 连接超时时间
                .connectionTimeoutMs(CONNECTION_TIMEOUT)
                // 会话超时时间
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .build();
    }

    /**
     * 创建临时节点
     */
    public static void createEphemeraNode(CuratorFramework zkClient, String path){
        try{
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("occur exception on :" + e);
        }
    }

    /**
     * 查找指定服务节点下的子节点，获取后就要监听，主要是客户端使用
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String serviceName){

        // 先看一下本机缓存中是否有，有的话，直接返回
        if(serviceAddressMap.containsKey(serviceName)){
            return serviceAddressMap.get(serviceName);
        }

        // 没有的话，向zookeeper查询
        List<String> result = Collections.emptyList();    // 这种方式，没遇到过
        String servicePath = CuratorHelper.ZK_REGISTER_ROOT_PATH + "/" + serviceName;
        try{
            // 获得子节点
            zkClient.getChildren().forPath(servicePath);
            // 加入本地缓存
            serviceAddressMap.put(serviceName, result);
            // 既然在本地缓存了，那么当zookeeper发生变化时，要更新对应本地缓存，所以加入监听机制


        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("occur exception on :" + e);
        }

        return result;

    }

    /**
     * 对Zookeeper节点进行监听，发生改变后，修改本地缓存
     * @param zkClent zookeeper的客户端连接
     * @param serviceName  要监听的服务(节点)
     */
    private static void registerWatcher(CuratorFramework zkClent, String serviceName){
        // 先构造出完整的节点路径
        String servicePath = CuratorHelper.ZK_REGISTER_ROOT_PATH + "/" + serviceName;

        /**
         * 暂时看不懂，需要整理一下
         */
        // 一个监听函数
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                // 重新获取zookeeper服务的节点信息
                List<String> serviceAdress = curatorFramework.getChildren().forPath(servicePath);
                // 放入本地缓存
                serviceAddressMap.put(serviceName, serviceAdress);
            }
        };
        // 绑定
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClent, servicePath, true);
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);

        // 开始
        try{
            pathChildrenCache.start();
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error("occur exception on:" + e);
        }
    }

}
