package com.github.torczuk.docker

import com.github.dockerjava.api.model.Container
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import org.slf4j.LoggerFactory

object Docker {
    private val config = DefaultDockerClientConfig.createDefaultConfigBuilder().build()
    private var docker = DockerClientBuilder.getInstance(config).build()
    private val log = LoggerFactory.getLogger(Docker::class.java)

    fun containers(): List<Container> = docker.listContainersCmd().exec()

    fun container(name: String): Container? = containers()
            .findLast { container ->
                container.names
                        .any { name == it.removePrefix("/") }
            }

    fun pause(name: String): Container? {
        val container = container(name)
        container?.let {
            log.info("pausing ${it.id} - ${it.names.first()}")
            docker.pauseContainerCmd(it.id).exec()
        }
        return container
    }

    fun unpause(name: String): Container? {
        val container = container(name)
        container?.let {
            log.info("unpausing ${it.id} - ${it.names.first()}")
            docker.unpauseContainerCmd(it.id).exec()
        }
        return container
    }

    fun state(name: String): String? = container(name)?.state

    fun status(name: String): String? = container(name)?.status
}

