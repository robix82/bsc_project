

pdflatex bachelorproject.tex
cp bachelorproject.aux bachelorproject.tex.aux
bibtex bachelorproject.tex
cp bachelorproject.tex.bbl bachelorproject.bbl
pdflatex bachelorproject.tex
pdflatex bachelorproject.tex

rm *.aux *.log *.out *.bbl  *.blg


