# Variables
COMPILER        := javac
RUNNER          := java

# Directories
ClientRoot = client
ClientSrc = client/src
ClientBin = client/bin

ServerRoot = server
ServerSrc = server/src
ServerBin = server/bin

# Compilation Rules
cc-all: $(ClientBin)/*.class $(ServerBin)/*.class
cc-client: $(ClientBin)/*.class
cc-server: $(ServerBin)/*.class

$(ClientBin)/*.class: $(ClientSrc)/*.java
	$(COMPILER) $(ClientSrc)/*.java -d $(ClientBin) $<

$(ServerBin)/*.class: $(ServerSrc)/*.java
	$(COMPILER) $(ServerSrc)/*.java -d $(ServerBin) $<

# Executable functions
run-server:
	$(RUNNER) -classpath $(ServerBin) Server

run-client:
	$(RUNNER) -classpath $(ClientBin) Client $(IP)

java-doc:
	@javadoc -d $(ClientRoot)/ClientDoc -linksource $(ClientSrc)/*.java
	@javadoc -d $(ServerRoot)/ServerDoc -linksource $(ServerSrc)/*.java

#cc-doc:
clean-class:
	@find . -name "*.class" -type f -delete

clean-java:
	@find . -name "*.java" -type f -delete
