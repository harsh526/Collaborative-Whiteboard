# Collaborative-Whiteboard
Collaborative Whiteboard Application in Java using RMI

How to run:
BEFORE EXECUTION

1. Make sure all the files (User.java, UserInterface.java, RegistryService.java, RegistryServiceImpl.java)
 	are in same folder.
2. Compile the files by executing "javac *.java" in terminal.
3. Create stubs for Registry service and User files as: "rmic RegistryServiceImpl" and "rmic User" respectively.



FOR REGISTRY SERVICE:

1. Execute Registry Service program by executing the following command in terminal: "java RegistryServiceImpl <registry-name>".
	Replace <registry-name> with the name you want to give to Registry service.
	
	Example: java RegistryServiceImpl Registry



FOR USERS:

1. Execute User program by executing: "java User <user-name> <color-hexcode> <host-address> <registry-name>".
	Replace:
		<user-name> with the name you want to give to User. (E.g. James)
		<color-hexcode> with hex code for this user's colour. (E.g. #FF0000 for Red)
		<host-address> with the host address. (E.g. 192.168.0.8)
		<registry-name> with the name of Registry.

	Example: java User James #FF0000 192.168.0.8 Registry
