Format_LaTeX
============

GATE document format analyzer for LaTeX documents. Use mime type value "application/x-tex", or file extension ".tex" to access this document format. 

The analyzer does the following:

* strips all comment lines (start with '%' character)

* looks for \xxxx{text} and adds an Original Markup annotation named 'xxxx' surrounding the text (it doesn't remove these markup pieces since LaTeX commands are often content, rather than markup, and this analyzer doesn't really understand the distinction)

* looks for \begin{yyyy}...\end{yyyy} and adds Original Markup annotation named 'yyyy' surrounding the enclosed text

* looks for $...$ math segments and adds Original Markup annotation named 'math'

See the [GATE website](http://gate.ac.uk) for details on downloading and using GATE.

To run tests you also need [JUnit](https://github.com/junit-team/junit)

To build and use the format analyzer, do the following:

1. Edit build.xml to fix the "gate.home" and "junit.jar" locations to be where the files are on your system
2. run 'ant' - this will create the jarfile format-latex.jar
3. In GATE Developer, open the Plugins tool and click "+" to "register a new Creole directory", and add the Format_LaTeX directory; and as appropriate select "Load now" or "Load always".
4. Any document with file name ending in '.tex' will be processed by the new format analyzer, and will have an "Original markups" annotation set to inspect.
