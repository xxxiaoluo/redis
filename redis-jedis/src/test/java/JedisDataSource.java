import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisDataSource {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 6379;

    /**
     * volatile 关键通常用于修饰属性:
     * 1)保证线程其可见性(一个线程改了,其它CPU线程立刻可见)
     * 2)禁止指令重排序
     * 3)不能保证其原子性(不保证线程安全)
     */
    private static volatile JedisPool jedisPool;

    //方案一：饿汉式池对象的创建
    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig(); //连接池配置对象
        jedisPoolConfig.setMaxTotal(16);
        jedisPoolConfig.setMaxIdle(8);
        if (jedisPool == null) {
            jedisPool = new JedisPool(jedisPoolConfig, IP, PORT);
        }
    }

    public static Jedis getResource() {
        return jedisPool.getResource();
    }

    //方案二:懒汉式池对象的创建
    public static Jedis getConnection() {
        if (jedisPool == null) {
            synchronized (JedisDataSource.class) {
                if (jedisPool == null) {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxTotal(16);
                    jedisPoolConfig.setMaxIdle(8);
                    JedisPool jedisPool = new JedisPool(jedisPoolConfig,IP,PORT);
                    //创建对象分析
                    //1.开辟内存空间
                    //2.执行属性的默认初始化
                    //3.执行构造方法
                    //4.将创建的对象的内存地址赋值给jedisPool变量
                    //假如使用了volatile修饰jedisPool变量,可以保证如上几个步骤是顺序执行的
                }
            }
        }
        return jedisPool.getResource();
    }

    public static void close(){
        jedisPool.close();
    }
}
