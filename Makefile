all: 
	cd DBoxClient && ant jar &&	cd ../DBoxServer &&	ant jar && cd ../DBoxBroker && ant jar

clean:
	rm DBoxClient/dist -rf
	rm DBoxInterface/dist -rf
	rm DBoxServer/dist -rf
	rm DBoxBroker/dist -rf
	rm DBoxServerUtils/dist -rf

server: 
	cd DBoxServer &&	ant jar && cd dist && java -jar DBoxServer.jar

client: 
	cd DBoxClient && ant jar && cd dist && java -jar DBoxClient.jar

broker: 
	cd DBoxBroker && ant jar && cd dist && java -jar DBoxBroker.jar
