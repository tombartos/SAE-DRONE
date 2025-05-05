all: doc verify

doc:
	$(MAKE) -C doc

compile:
	mvn compile
verify:
	mvn verify

clean:
	-mvn clean
	-$(MAKE) -C doc clean

.PHONY: all clean doc compile verify
