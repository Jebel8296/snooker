package com.mstx.framework.gateway;

import com.mstx.framework.gateway.gateway.GateWayVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = {"classpath:application.properties"})
public class GatewayApplication {
    private static Logger logger = LoggerFactory.getLogger(GatewayApplication.class);

    @Autowired
    Environment environment;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        GatewayApplication gatewayApplication = applicationContext.getBean(GatewayApplication.class);
        ClusterManager clusterManager = new ZookeeperClusterManager();
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true).setClusterManager(clusterManager).setClusterHost(gatewayApplication.getClusterHost()).setClusterPublicHost(gatewayApplication.getClusterPublicHost());
        Vertx.clusteredVertx(vertxOptions, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                DeploymentOptions option = new DeploymentOptions();
                vertx.deployVerticle(applicationContext.getBean(GateWayVerticle.class));
                logger.info("GatewayApplication start success.");
            } else {
                logger.error("GatewayApplication start failed.");
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
