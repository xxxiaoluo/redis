import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolTest {

    /**
     * 我们直接基于Jedis访问redis时，每次获取连接，释放连接会带来很大的性能开销，可以借助Jedis连接池，重用创建好的连接，来提高其性能
     */
    @Test
    public void testJedisPool() {
        //定义连接池的配置
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(1000); //最大连接数
        jedisPoolConfig.setMaxIdle(60); //最大空闲数
        //创建连接池
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379);
        //从池中获取一个连接
        Jedis resource = jedisPool.getResource();
//        resource.auth("123456");
        //通过jedis连接存取数据
        resource.set("name", "roy");
        String name = resource.get("name");
        System.out.println(name);
        //将连接返回池中
        resource.close();
        //关闭连接池
        jedisPool.close();
    }


}
