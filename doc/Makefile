rapport.pdf:

%.pdf: %.md
	pandoc -s $< -o $@

%.pdf: %.tex
	latexmk -makeindexfudge -interaction=nonstopmode -pdf -use-make -M -MP -MF $(@:.pdf=.d) $<

%.png: %.puml
	plantuml $<

-include rapport.d

clean:
	-latexmk -C rapport.tex
	-rm rapport.d
	-rm *.png

.PHONY: clean
