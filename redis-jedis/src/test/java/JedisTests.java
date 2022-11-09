import com.google.gson.Gson;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.*;


public class JedisTests {

    /**
     * 测试是否可以联通redis
     */
    @Test
    public void testRedisConnection() {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String result = jedis.ping();
        System.out.println(result);
    }

    /**
     * 测试字符串操作 crud
     */
    @Test
    public void testStringOper01() {
        //建立连接
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //执行redis数据操作(增删改查)
        //1.新增数据
        jedis.set("id", "100");
        jedis.set("name", "roy");

        //2.修改数据
        jedis.incr("id");
        jedis.incrBy("id", 100);
        jedis.set("name", "Mike");//Mike

        //3.查询数据
        String id = jedis.get("id");
        String name = jedis.get("name");
        System.out.println("id=" + id + ";name=" + name);

        //4.删除数据
        jedis.del("name");

        //3.释放资源
        jedis.close();
    }

    /**
     * 测试字符串操作 将map转json再写入到redis
     */
    @Test
    public void testStringOper02() {
        //1.建立连接
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //2.数据操作(将一个map对象转换为json字符串，然后写入到redis)
        //2.1构建map对象
        Map<String, String> map = new HashMap<>();
        map.put("id", "100");
        map.put("name", "Mike");
        //2.2将map转换为字符串
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        //2.3将字符串写入到redis
        String key = UUID.randomUUID().toString();
        jedis.set(key, jsonStr);
        //3.释放资源
        jedis.close();
    }

    /**
     * 课堂练习：
     * 基于hash类型将testStringOper02中对象写入到redis，
     * 并且尝试进行查询，修改，删除等操作。
     */
    @Test
    public void testHash01() {
        //1.建立连接
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //2.执行hash数据操作
        //2.1新增数据
        String key = UUID.randomUUID().toString();
        jedis.hset(key, "id", "500");
        jedis.hset(key, "name", "Jack");
        //2.2修改数据数据
        jedis.hset(key, "name", "Jim");
        //2.3查询数据
        Map<String, String> map = jedis.hgetAll(key);
        System.out.println(map);
        //2.4删除数据
        //jedis.del(key);
        jedis.expire(key, 10);
        //3.释放资源
        jedis.close();
    }

    /**
     * 测试:redis中list结构的应用
     * 基于FIFO(First In First Out)算法,借助redis实现一个队列
     */
//    @Test
//    public void testListOper01() {
//        //1.建立连接
//        Jedis jedis = new Jedis("127.0.0.1", 6379);
//        //2.存储数据
//        jedis.lpush("lst1", "A", "B", "C", "C");
//        //3.更新数据
//        Long pos = jedis.lpos("lst1", "A");//获取A元素的位置
//        jedis.lset("lst1", pos, "D");//将A元素位置的内容修改为D
//        //4.获取数据
//        int len = jedis.llen("lst1").intValue();//获取lst1列表中元素个数
//        List<String> rpop = jedis.rpop("lst1", len);//获取lst1列表中所有元素
//        System.out.println(rpop);
//        //5.释放资源
//        jedis.close();
//    }

    @Test
    public void testListOper02(){
        //1.连接redis
        Jedis jedis=new Jedis("127.0.0.1",6379);
        //2.向队列存数据
//        jedis.lpush("list1","A","B","C");
        //3.按先进先出的顺序从队列取数据
        List<String> list= jedis.brpop(40,"list1");
        System.out.println(list);
        jedis.brpop(40,"list1");
        jedis.brpop(40,"list1");
        jedis.brpop(40,"list1");
        //4.释放资源
        jedis.close();
    }

    //set类型练习
    @Test
    public void testSetOper01() {
        //1.连接redis
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //2.朋友圈点赞
        jedis.sadd("count", "1", "1","1", "2","3");
        //3.取出点赞数
        Set<String> set = jedis.smembers("count");
        System.out.println(set); //set结构去重了
        //4.释放资源
        jedis.close();
    }
}
