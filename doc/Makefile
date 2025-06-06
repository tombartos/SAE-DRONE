rapport.pdf:

%.pdf: %.md
	pandoc -s $< -o $@

%.pdf: %.tex
	latexmk -interaction=nonstopmode -pdf $(LATEXMK_OPTIONS) -use-make -M -MP -MF $(@:.pdf=.d) $<

plantuml.d: *.puml
	awk '                                                                   \
		/^@startuml / { out = $$2".png" }                                   \
		/^@startuml$$/ { out = FILENAME; sub(/\.puml/, ".png", out) }       \
		/^@startuml/ { outs[FILENAME] = out " " outs[FILENAME] }            \
		END { for (i in outs) { print outs[i] ": " i "\n\tplantuml $$<" } } \
		' $^ > $@

-include plantuml.d
-include rapport.d

clean:
	-latexmk -C rapport.tex
	-rm rapport.d
	-rm plantuml.d
	-rm *.png

.PHONY: clean
