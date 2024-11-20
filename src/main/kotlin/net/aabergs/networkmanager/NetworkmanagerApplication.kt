package net.aabergs.networkmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NetworkmanagerApplication

fun main(args: Array<String>) {
	runApplication<NetworkmanagerApplication>(*args)
}
