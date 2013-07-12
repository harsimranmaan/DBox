all:
	cd DBoxClient && ant jar &&	cd ../DBoxServer &&	ant jar && cd ../DBoxBroker && ant jar

clean:
	rm DBoxClient/dist -rf
	rm DBoxInterface/dist -rf
	rm DBoxServer/dist -rf
	rm DBoxBroker/dist -rf
	rm DBoxServerUtils/dist -rf

server: all
	java -jar DBoxServer/dist/DBoxServer.jar

client: all
	java -jar DBoxClient/dist/DBoxClient.jar

broker: all
	java -jar DBoxBroker/dist/DBoxBroker.jar
