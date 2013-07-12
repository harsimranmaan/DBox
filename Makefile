all:
	cd DBoxClient && ant jar &&	cp ../DBoxInterface/dist/*.jar dist/ && cp config.properties dist/config/ &&	cd ../DBoxServer &&	ant jar &&	cp ../DBoxInterface/dist/*.jar dist/ && cp config.properties dist/config/ && cp ../DBoxServerUtils/dist/*.jar dist/ && cd ../DBoxBroker && ant jar && cp ../DBoxInterface/dist/*.jar dist/ && cp config.properties dist/config/ && cp ../DBoxServerUtils/dist/*.jar dist/

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
