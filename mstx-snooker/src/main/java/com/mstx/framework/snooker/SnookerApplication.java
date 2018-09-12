package com.mstx.framework.snooker;

import com.mstx.framework.snooker.worker.LoginVerticle;
import com.mstx.framework.snooker.worker.VerifyCodeVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = {"classpath:application.properties"})
public class SnookerApplication {
    private static Logger logger = LoggerFactory.getLogger(SnookerApplication.class);

    @Autowired
    Environment environment;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        SnookerApplication snookerApplication = applicationContext.getBean(SnookerApplication.class);
        ClusterManager clusterManager = new ZookeeperClusterManager();
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true).setClusterManager(clusterManager).setClusterHost(snookerApplication.getClusterHost()).setClusterPublicHost(snookerApplication.getClusterPublicHost());
        Vertx.clusteredVertx(vertxOptions, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                DeploymentOptions option = new DeploymentOptions();
                vertx.deployVerticle(applicationContext.getBean(VerifyCodeVerticle.class), option.setWorker(true));
                vertx.deployVerticle(applicationContext.getBean(LoginVerticle.class), option.setWorker(true));
                logger.info("SnookerApplication start success.");
            } else {
                logger.error("SnookerApplication start failed.");
            }
        });
    }

    public String getClusterPublicHost() {
        return environment.getProperty("cluster.public.host", "localhost");
    }

    public String getClusterHost() {
        return environment.getProperty("cluster.host", "localhost");
    }
}
