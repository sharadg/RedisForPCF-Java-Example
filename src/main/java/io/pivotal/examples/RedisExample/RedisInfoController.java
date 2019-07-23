package io.pivotal.examples.RedisExample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class RedisInfoController {

    private Logger LOG = Logger.getLogger(RedisInfoController.class.getName());
    private Jedis jedis = null;

    @Value("${vcap.services.redis-demo.credentials.host}")
    private String host;

    @Value("${vcap.services.redis-demo.credentials.port}")
    private int port;

    @Value("${vcap.services.redis-demo.credentials.password}")
    private String password;

    @RequestMapping("/")
    public RedisInstanceInfo getInfo() {
        RedisInstanceInfo info = new RedisInstanceInfo();
        info.setHost(host);
        info.setPort(port);
        info.setPassword(password);

        // the object will be json serialized automatically by Spring web - we just need to return it
        return info;
    }

    @RequestMapping("/set")
    public String setKey(@RequestParam("kn") String key, @RequestParam("kv") String val) {
        LOG.log(Level.WARNING, "Called the key set method, going to set key: " + key + " to val: " + val);

        if(jedis == null || !jedis.isConnected()) {
            jedis = getJedisConnection();
        }
        jedis.set(key, val);

        return "Set key: " + key + " to value: " + val;
    }

    @RequestMapping("/get")
    String getKey(@RequestParam("kn") String key) {
        LOG.log(Level.WARNING, "Called the key get method, going to return val for key: " + key);

        if(jedis == null || !jedis.isConnected()) {
            jedis = getJedisConnection();
        }

        return jedis.get(key);
    }

    @RequestMapping("/kill")
    void killServer() {
        LOG.log(Level.WARNING, "About to kill the service!");
        System.exit(0);
    }

    private Jedis getJedisConnection() {
        // get our connection info from VCAP_SERVICES
        //        RedisInstanceInfo info = getInfo();
        Jedis jedis = new Jedis(host, port);

        // make the connection
        jedis.connect();

        // authorize with our password
        jedis.auth(password);

        return jedis;
    }

}

