import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.RedisApplication;
import com.learn.pojo.Blog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(classes={RedisApplication.class})
public class RedisTemplateTests {
    /**
     * 此对象为操作redis的一个模板对象,基于此对象进行数据存储时，
     * 数据会进行序列化，序列化方式默认为JDK自带的序列化机制。
     */

    @Autowired
    private RedisTemplate redisTemplate;

    /**测试是否能够连通redis*/
    @Test
    public void testGetConnection(){
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        String result = connection.ping();
        System.out.println(result);
    }

    @Test
    public void testStringOper01(){
        //1.获取字符串操作对象(ValueOperations)
        ValueOperations vo = redisTemplate.opsForValue();
        //操作redis数据（直接在opsForValue后面点set，value为数值类型似乎不可以）
        vo.set("msg", 100);
        Object msg = vo.get("msg");
        System.out.println(msg);

//        Long msg1 = vo.increment("msg"); 不可以
//        System.out.println(msg1);

        Long y = vo.increment("y");
        y = vo.increment("y");
//        Object result = vo.get("y"); 不可以
//        System.out.println(result);

        System.out.println(y);
        //存储key/value，设置key的有效期
        vo.set("z", "200", Duration.ofSeconds(10));
    }


    @Test
    void testStringOper02(){
        //1.获取字符串操作对象(ValueOperations)
        ValueOperations vo = redisTemplate.opsForValue();
        //2.按默认序列化方式存储数据
        String token= UUID.randomUUID().toString();
        vo.set(token,"admin");
        //3.指定序列方式进行数据存储
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.string());
        vo.set(token,"Mike");
        //4.更新数据(假如有对应的key则直接进行覆盖)
        vo.set(token, "Jack");
        Object value = vo.get(token);
        System.out.println(value);
        //5.删除数据(存数据时给定有效期-生产环境必须设置)
        vo.set("permissions", "sys:res:update",Duration.ofSeconds(5));
    }

    //--------------------------------------Hash--------------------------------------

    /**
     * Hash的存取
     */
    @Test
    void testHashOper01(){
        //1.获取Hash操作对象(ValueOperations)
        HashOperations ho = redisTemplate.opsForHash();
        //2.以hash类型存储数据
        ho.put("blog","id",100);
        ho.put("blog", "title", "redis....");
        //3.获取数据
        Object id = ho.get("blog", "id");
        Object title = ho.get("blog", "title");
        System.out.println("id="+id+";title="+title);
        Map blog = ho.entries("blog");//取key对应的所有值。
        System.out.println(blog);
    }

    /**
     * 设计一个Blog类，然后通过redisTemplate将此类的对象写入到redis数据库
     * 两种方案：
     * 1)方案1：基于ValueOperations对象实现数据存取
     * 2)方案2：基于HashOperations对象实现数据存储
     */
    @Test
    void testHashOper02() throws JsonProcessingException {
        //1.获取数据操作对象
        ValueOperations vo = redisTemplate.opsForValue();
        HashOperations ho = redisTemplate.opsForHash();

        //2.基于基于ValueOperations存取Blog对象
        Blog blog = new Blog();
        blog.setId(100);
        blog.setTitle("roy年薪百万");
        vo.set("blog-roy", blog); //序列化
        Blog o = (Blog) vo.get("blog-roy"); //反序列化
        System.out.println(o);

        //3.基于HashOperations存取Blog对象
        ObjectMapper mapper = new ObjectMapper(); //对象转json工具类
        String str = mapper.writeValueAsString(blog); //转换
        Map map = mapper.readValue(str, Map.class);
        ho.putAll("blog-luo", map);
        ho.put("blog-luo", "id", 2022);
        Map entries = ho.entries("blog-luo"); //覆盖前面的id
        System.out.println(entries);
    }

    //--------------------------------------List--------------------------------------

    @Test
    void testListOper(){
        //向list集合放数据
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush("lstKey1", "100"); //lpush
        listOperations.leftPushAll("lstKey1", "200","300");
        listOperations.leftPush("lstKey1", "100", "105");
        listOperations.rightPush("lstKey1", "700");
        Object value= listOperations.range("lstKey1", 0, -1);
        System.out.println(value);
        //从list集合取数据
        Object v1=listOperations.leftPop("lstKey1");//lpop
        System.out.println("left.pop.0="+v1); //取出后，原来的集合就失去了
        value= listOperations.range("lstKey1", 0, -1);
        System.out.println(value);
    }

    //--------------------------------------Set--------------------------------------

    @Test
    void testSetOper(){
        SetOperations setOperations=redisTemplate.opsForSet();
        setOperations.add("setKey1", "A","B","C","C");
        Object members=setOperations.members("setKey1");
        System.out.println("setKeys="+members);
        //........
    }

    //--------------------------------------flushall--------------------------------------

    @Test
    void testFlushdb(){
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //redisConnection.flushDb();
                redisConnection.flushAll();
                return "flush ok";
            }
        });
    }
}
