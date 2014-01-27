Format_LaTeX
============

GATE document format analyzer for LaTeX documents. Use mime type value "application/x-tex", or file extension ".tex" to access this document format. 

The analyzer does the following:

* strips all comment lines (start with '%' character)

* looks for \xxxx{text} and adds an Original Markup annotation named 'xxxx' surrounding the text (it doesn't remove these markup pieces since LaTeX commands are often content, rather than markup, and this analyzer doesn't really understand the distinction)

* looks for \begin{yyyy}...\end{yyyy} and adds Original Markup annotation named 'yyyy' surrounding the enclosed text

* looks for $...$ math segments and adds Original Markup annotation named 'math'
