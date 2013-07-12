all:
	cd DBoxClient && ant jar &&	cd ../DBoxServer &&	ant jar && cd ../DBoxBroker && ant jar

clean:
	rm DBoxClient/dist -rf
	rm DBoxInterface/dist -rf
	rm DBoxServer/dist -rf
	rm DBoxBroker/dist -rf
	rm DBoxServerUtils/dist -rf

server: all
	 cd  DBoxServer/dist && java -jar DBoxServer.jar

client: all
	cd  DBoxClient/dist && java -jar DBoxClient.jar

broker: all
	cd  DBoxBroker/dist && java -jar DBoxBroker.jar
