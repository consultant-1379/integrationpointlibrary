/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.aia.ipl.test.util

import static org.junit.Assert.fail

import kafka.server.KafkaConfig
import kafka.server.KafkaServer

import org.apache.commons.io.FileUtils
import org.apache.curator.test.TestingServer

/**
 * EmbeddedKafkaAndZookeeperServers provide functional Zookeeper and Kafka servers in runtime.
 * As Kafka and Zookeeper were being instantiate in an embedded manner, the performance is not good.
 */
class EmbeddedKafkaAndZookeeperServers {

    static final int BROKER_ID = 0
    int brokerPort
    String kafkaLogsDirectory
    String zookeeperLogsDirectory

    static TestingServer zookeeperServer = null
    static KafkaServer kafkaServer = null

    /**
     * Instantiates a new embedded kafka and zookeeper servers.
     */
    EmbeddedKafkaAndZookeeperServers(String dirPrefix = "", int brokerPort = 59002) {
        this.brokerPort = brokerPort
        try {
            kafkaLogsDirectory = "target/temp/" + dirPrefix + "kafka-logs-" + BROKER_ID
            zookeeperLogsDirectory = "target/temp/" + dirPrefix + "zookeeper"
            removeExistingLogFiles()
            startZookeeperServer()
            startKafkaServer()
        } catch (final Exception exception) {
            fail "Exception occurred while creating the servers. Exception message: " + exception.getMessage()
        }
    }

    private def startKafkaServer() throws IOException {
        final KafkaConfig config = getKafkaConfig(zookeeperServer.getConnectString())
        kafkaServer = new KafkaServer(config)
        kafkaServer.startup()
    }

    private def startZookeeperServer() {
        final int shouldUseARandomPort = -1
        final File zookeeperLogsDirectory = new File(zookeeperLogsDirectory)
        final boolean shouldStartImmediately = true
        zookeeperServer = new TestingServer(shouldUseARandomPort, zookeeperLogsDirectory, shouldStartImmediately)
    }

    private KafkaConfig getKafkaConfig(final String zookeeperConnectString) throws IOException {
        final Properties props = new Properties([
            ("broker.id"): BROKER_ID,("port"): brokerPort,
            ("log.dir"): kafkaLogsDirectory,
            ("zookeeper.connect"): zookeeperConnectString,
            ("host.name"): "127.0.0.1",
            ("auto.create.topics.enable"): "true"])
        new KafkaConfig(props)
    }

    /**
     * Gets the kafka broker string.
     *
     * @return the kafka broker string
     */
    String getKafkaBrokerString() {
        String.format("localhost:%d", kafkaServer.serverConfig().port())
    }

    /**
     * Gets the zookeeper connect string.
     *
     * @return the zookeeper connect string
     */
    String getZookeeperConnectString() {
        zookeeperServer.getConnectString()
    }

    /**
     * Gets the kafka port.
     *
     * @return the kafka port
     */
    int getKafkaPort() {
        kafkaServer.serverConfig().port()
    }

    /**
     * Stop servers.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    def stopServers() throws IOException {
        kafkaServer.shutdown()
        zookeeperServer.close()
    }

    private def removeExistingLogFiles() throws IOException {
        FileUtils.deleteDirectory new File(kafkaLogsDirectory)
        FileUtils.deleteDirectory new File(zookeeperLogsDirectory)
    }
}
