all: doc maven-compile

doc:
	$(MAKE) -C doc

maven-compile:
	mvn compile

clean:
	-mvn clean
	-$(MAKE) -C doc clean

.PHONY: all clean doc maven-compile
