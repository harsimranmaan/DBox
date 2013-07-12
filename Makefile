all:
	cd DBoxClient && ant jar &&	cp ../DBoxInterface/dist/DBoxInterface.jar dist/ &&	cd ../DBoxServer &&	ant jar &&	cp ../DBoxInterface/dist/DBoxInterface.jar dist/ 

clean:
	rm DBoxClient/dist -rf
	rm DBoxInterface/dist -rf
	rm DBoxServer/dist -rf

server: all
	java -jar DBoxServer/dist/DBoxServer.jar

client: all
	java -jar DBoxClient/dist/DBoxClient.jar
